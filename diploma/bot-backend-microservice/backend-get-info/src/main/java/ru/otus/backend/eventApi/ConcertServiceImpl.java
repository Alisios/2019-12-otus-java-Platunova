package ru.otus.backend.eventApi;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.objects.Message;
import ru.otus.backend.eventApi.helper.DateParser;
import ru.otus.backend.eventApi.service.GetEventInfoService;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.TicketModel;
import ru.otus.backend.model.User;
import ru.otus.helpers.CallbackType;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**один из основных классов бизнес логики для мероприятия типа "концерт":
 * прописана логика формирования сообщений пользователю информации о концерте и/или билетах
 * имеет 2 источника информации о мероприятиях - парсинг сайта и,
 * если при парсинге случилось исключение,  то stub сайт
  **/

@Service ("concertServiceImpl")
public class ConcertServiceImpl implements ConcertService {
    private static final Logger logger = LoggerFactory.getLogger(ConcertServiceImpl.class);
    private final DateParser dateParser = new DateParser();
    private GetEventInfoService getEventInfoService;
    private GetEventInfoService getEventInfoServiceOptional;
    private GetEventInfoService getEventInfoServiceRest;

    ConcertServiceImpl() { ; }

    @Autowired
    public ConcertServiceImpl(@Qualifier("getEventInfoServiceRestApi") GetEventInfoService getEventInfoService,
                              @Qualifier("getEventInfoServiceParsing") GetEventInfoService getEventInfoServiceOptional) {
        this.getEventInfoService = getEventInfoService;
        this.getEventInfoServiceOptional = getEventInfoServiceOptional;
        this.getEventInfoServiceRest = getEventInfoService;
    }

    @HystrixCommand(fallbackMethod = "getConcertInfoOptional", commandKey = "getInfo", groupKey = "getInfo")
    @Override
    public MessageForFront getConcertInfo(Message message) throws IOException {
        List<ConcertModel> concertList = getEventInfoService.getEventInformation(message.getText());
        String payload = "";
        MessageForFront messageForFront = new MessageForFront(MessageType.GET_EVENT_INFO, Serializers.serialize(payload), message.getChatId(), message.getMessageId());
        if (concertList.size() == 0) {
            payload = "Концерты по запрашиваемому исполнителю не найдены";
        } else {
            if (concertList.size() == 1) {
                String ticketRes = getTicketInfo(concertList.get(0));
                if (ticketRes.contains("Хотите отслеживать появление билетов в фанзону"))
                    messageForFront.setCallbackType(CallbackType.IF_SHOULD_BE_MONITORED.getValue());
                payload = "По Вашему запросу найдена информация: \n" + ticketRes;
            } else {
                messageForFront.setCallbackType(CallbackType.LIST_OF_EVENTS.getValue());
                messageForFront.setNumberOfEvents(concertList.size());
                payload = "По Вашему запросу найдены следующие мероприятия:\n" +
                        concertList.stream().map(i -> (concertList.indexOf(i) + 1 + ") " +
                                i.toString())).collect(Collectors.joining(";\n"));
            }
        }
        messageForFront.setPayload(Serializers.serialize(payload));
        return messageForFront;
    }

    private MessageForFront getConcertInfoOptional(Message message) throws IOException{
        this.getEventInfoService = this.getEventInfoServiceOptional;
        MessageForFront msg = getConcertInfo(message);
        this.getEventInfoService = this.getEventInfoServiceRest;
        return msg;
    }

    @Override
    @HystrixCommand(fallbackMethod = "getTicketInfoOptional", commandKey = "getTicket", groupKey = "getTicket")
    public String getTicketInfo(String message, int index) throws IOException {
        ConcertModel concert = concertParser(message, index);
        if (concert == null) return "Повторите пожалуйта запрос! Напишите какого исполнителя Вы ищите.";
        return getTicketInfo(concert);
    }

    private String getTicketInfo(ConcertModel concert) throws IOException {
        List<TicketModel> ticketModelList;
        if (!concert.getTickets().isEmpty())
            ticketModelList= concert.getTickets();
        else
            ticketModelList = getEventInfoService.getTicketInformation(concert);
        String res = analyzeTicketInfo(concert, ticketModelList);
        if (concert.getShouldBeMonitored())
            return concert.toString() + res + "\n \nХотите отслеживать появление билетов в фанзону или на танцпол?";
        else {
            return concert.toString() + "\n" + res;
        }
    }

    private String getTicketInfoOptional(String message, int index) throws IOException{
        this.getEventInfoService = this.getEventInfoServiceOptional;
        String msg = getTicketInfo(message, index);
        this.getEventInfoService = this.getEventInfoServiceRest;
        return msg;
    }

    //позволяет не кэшировать промежуточные сообщения
    private ConcertModel concertParser(String message, int index) {
        List<String> concertList = List.of(message.split(";\n"));
        if (!message.contains("Место проведения: ") || concertList.size() == 0)
            return null;
        ConcertModel concertModel = new ConcertModel();
        int i = 0;
        try {
            if (concertList.get(0).contains("По Вашему запросу") && (index == 0)) i = 1;
            String[] s = concertList.get(index).split("\n");
            concertModel.setArtist(s[i].trim().split("Исполнитель: ")[1]);
            concertModel.setDate(s[++i].trim().replace("Дата: ", ""));
            concertModel.setPlace(s[++i].trim().replace("Место проведения: ", ""));
            concertModel.setConcertUrl((s[++i].trim().replace("Ссылка: ", "")));
            logger.info("ConcertFromModel: {}", concertModel.toString());
            return concertModel;
        }
        catch(RuntimeException ex){
            logger.error("Error with parsing concert from message {}, {},\n {}", ex.getMessage(), ex.getCause(), ex.getStackTrace());
            throw new EventException("Error with parsing concert from message "+ ex.getMessage()+" "+ex.getCause());
        }
    }

    private String analyzeTicketInfo(ConcertModel concert, List<TicketModel> ticketModelList) {
        Integer minCost = 500000;
        int maxCost = 0;
        Integer[] temp = {0, 0};
        StringBuilder sb = new StringBuilder();
        if (ticketModelList.size() == 0) {
            concert.setShouldBeMonitored(true);
            return "\nБилеты на данное мероприятие не найдены";
        }

        for (TicketModel tm : ticketModelList) {
            if ((tm.getType().toLowerCase().contains("танц")) || (tm.getType().toLowerCase().contains("фан")) || (tm.getType().toLowerCase().contains("абонемент")))
                sb.append(tm.toString()).append("\n");
            else {
                String cost = tm.getCost()
                        .replace("Р.", "")
                        .replace("Р", "")
                        .replace("руб.", "")
                        .replace("руб", "")
                        .replace(" ", "");
                if (cost.equals(""))
                    continue;
                if (!cost.contains("—")) temp[0] = temp[1] = Integer.parseInt(cost);
                else {
                    temp[0] = Integer.parseInt(cost.split("—")[0]);
                    temp[1] = Integer.parseInt(cost.split("—")[1]);
                }
                if (temp[0] < minCost) minCost = temp[0];
                if (temp[1] > maxCost) maxCost = temp[1];
            }
        }
        if (sb.toString().equals("")) {
            concert.setShouldBeMonitored(true);
            sb.append("\nБилетов на танцпол и фанзону нет.\n");
        }
        if ((temp[0] == 0) && (temp[1] == 0)) sb.append("Билетов в другие зоны нет.\n");
        else
            sb.append("Минимальная стоимость билетов в другие зоны составляет: ").append(minCost.toString()).append(", максимальная: ").append(Integer.toString(maxCost)).append(".");
        return sb.toString();
    }

    @Override
    public User monitorOfEvent(Message message) {
        ConcertModel concertModel = concertParser(message.getText(), 0);
        concertModel.setShouldBeMonitored(true);
        Calendar dateOfMonitorFinish = dateParser.parse(concertModel.getDate());
        dateOfMonitorFinish.set(Calendar.DAY_OF_MONTH, dateOfMonitorFinish.get(Calendar.DAY_OF_MONTH) - 2);
        User user = new User(message.getChatId(), concertModel, dateOfMonitorFinish.getTime());
        concertModel.setOwner(user);
        return user;
    }
}
