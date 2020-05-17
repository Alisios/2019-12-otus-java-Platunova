package ru.otus.backend.eventApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.Message;
import ru.otus.backend.eventApi.helpers.HtmlParser;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.TicketModel;
import ru.otus.helpers.CallbackType;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.Serializers;
import java.util.List;
import java.util.stream.Collectors;

public class Concert implements MonitoredEvent {
    private static Logger logger = LoggerFactory.getLogger(Concert.class);
    private HtmlParser htmlParser;
    Concert(){;}

    public Concert(HtmlParser htmlParser){
        this.htmlParser = htmlParser;
    }

    @Override
    public MessageForFront getConcertInfo(Message message) {
        List<ConcertModel> concertList = htmlParser.getEventsFromHtml(message.getText());
        String payload = "";
        MessageForFront messageForFront = new MessageForFront(MessageType.GET_EVENT_INFO, Serializers.serialize(payload), message.getChatId(), message.getMessageId());
        if (concertList.size() == 0) {
            payload = "Концерты по запрашиваемому исполнителю не найдены";
        } else {
            if (concertList.size() == 1) {
                String ticketRes = getTicketInfo(concertList.get(0).toString(), 0);
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

    @Override
    public String getTicketInfo(String message, int index) {
        ConcertModel concert = concertParser(message, index);
        if (concert == null) return "Повторите пожалуйта запрос! Напишите какого исполнителя Вы ищите.";
        List<TicketModel> ticketModelList = htmlParser.getTicketInfoFromHtml(concert.getConcertUrl());
        String res = analyzeTicketInfo(concert, ticketModelList);
        if (concert.getShouldBeMonitored())
            return concert.toString() + res + "\n \nХотите отслеживать появление билетов в фанзону или на танцпол?";
        else {
            return concert.toString()+"\n" + res;
        }
    }

    private ConcertModel concertParser(String message, int index){
       // logger.info("Message: {}", message);
        List<String> concertList =  List.of(message.split(";\n"));
        if (concertList.size()==0)
            return null;
        ConcertModel concertModel = new ConcertModel();
        int i = 0;
        if(concertList.get(0).contains("По Вашему запросу") && (index==0))  i=1;
        String [] s = concertList.get(index).split("\n");
        concertModel.setArtist(s[i].trim().split("Исполнитель: ")[1]);
        concertModel.setDate(s[++i].trim().replace("Дата: ", ""));
        concertModel.setPlace(s[++i].trim().replace("Место проведения: ", ""));
        concertModel.setConcertUrl((s[++i].trim().replace("Ссылка: ", "")));
        logger.info("ConcertFromModel: {}", concertModel.toString());
        return concertModel;
    }

    private String analyzeTicketInfo(ConcertModel concert, List <TicketModel> ticketModelList){
        Integer minCost = 500000;
        Integer maxCost = 0;
        Integer [] temp = {0,0};
        StringBuilder sb = new StringBuilder();
        if (ticketModelList.size() == 0) {
            concert.setShouldBeMonitored(true);
            return "Билеты на данное мероприятие не найдены";
        }

        for  (TicketModel tm: ticketModelList){
            if ((tm.getType().toLowerCase().contains("танц")) || (tm.getType().toLowerCase().contains("фан")))
                sb.append(tm.toString()).append("\n");
            else {
                String cost = tm.getCost().replace("Р.", "").replace(" ", "");
                if (!cost.contains("—"))  temp[0]=temp[1] = Integer.parseInt(cost);
                else{
                    temp[0] =  Integer.parseInt(cost.split("—")[0]);
                    temp[1] =  Integer.parseInt(cost.split("—")[1]);
                }
                if (temp[0] < minCost) minCost=temp[0];
                if (temp[1] > maxCost) maxCost=temp[1];
            }
        }
        if (sb.toString().equals("")){;//(concert.toString()+"\n")){
            concert.setShouldBeMonitored(true);
            sb.append("\nБилетов на танцпол и фанзону нет.\n");
        }
        if ((temp[0] == 0)&&(temp[1]==0)) sb.append("Билетов в другие зоны нет.\n");
        else sb.append("Минимальная стоимость билетов в другие зоны составляет: ").append(minCost.toString()).append(", максимальная: ").append(maxCost.toString()).append(".");
        return sb.toString();
    }

    @Override
    public ConcertModel getModel(String message){
        return concertParser(message, 0);
    }
}
