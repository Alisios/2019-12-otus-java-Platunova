package ru.otus.backend.eventApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.serializer.Serializer;
import org.telegram.telegrambots.api.objects.Message;

import ru.otus.backend.eventApi.helpers.HtmlParser;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.TicketModel;
import ru.otus.backend.model.User;
import ru.otus.helpers.CallbackType;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.MessageForFront;
import ru.otus.helpers.Serializers;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Concert implements MonitoredEvent {
    private static Logger logger = LoggerFactory.getLogger(Concert.class);

    private HtmlParser htmlParser;
    private final Map<Long, Map<Integer, List<ConcertModel>>> cacheMap  = new ConcurrentHashMap<>();
    private Map<Integer,List<ConcertModel>> cacheMap2 = new ConcurrentHashMap<>();
  //  private MessageType messageType = MessageType.HELLO;
   // private int sizeofPossibleEvents  = 0;

    Concert(){;}

    public Concert(HtmlParser htmlParser){
        this.htmlParser = htmlParser;
    }

    @Override
    public MessageForFront getConcertInfo(Message message){
        List <ConcertModel> concertList = htmlParser.getEventsFromHtml(message.getText());
        String payload = "";
        MessageForFront messageForFront= new MessageForFront(MessageType.GET_EVENT_INFO, Serializers.serialize(payload), message.getChatId(), message.getMessageId());
        if (concertList.size() == 0) {
            payload = "Концерты по запрашиваемому исполнителю не найдены";
        }
        else {
            cacheMap2.put(message.getMessageId(), concertList);
            cacheMap.put(message.getChatId(), cacheMap2);
            if (concertList.size() == 1) {
                MessageForFront temp = this.getTicketInfo(message.getChatId(), message.getMessageId(), 0, message);
                if (temp.getCallbackType().equals(CallbackType.IF_SHOULD_BE_MONITORED.getValue()))
                    messageForFront.setCallbackType(CallbackType.IF_SHOULD_BE_MONITORED.getValue());
                    payload = "По Вашему запросу найдена информация: " +
                    Serializers.deserialize(temp.getPayload(), String.class);
        }else {
                messageForFront.setCallbackType(CallbackType.LIST_OF_EVENTS.getValue());
                messageForFront.setNumberOfEvents(concertList.size());
                payload = "По Вашему запросу найдены следующие мероприятия:\n" +
                        concertList.stream().map(i -> (concertList.indexOf(i) + 1 + ") " + i.toString())).collect(Collectors.joining(";\n"));
            }
        }
        messageForFront.setPayload(Serializers.serialize(payload));
        return messageForFront;
    }

    @Override
    public MessageForFront getTicketInfo(Long chartId, int messageId, int index, Message message) {
        String payload = "";
        MessageForFront messageForFront= new MessageForFront(MessageType.GET_TICKET_INFO,  Serializers.serialize(payload), chartId, messageId);
        List <TicketModel> ticketModelList = null;
        if (cacheMap.containsKey(chartId) && (cacheMap.get(chartId).containsKey(messageId)))
                ticketModelList = htmlParser.getTicketInfoFromHtml(cacheMap.get(chartId).get(messageId).get(index).getConcertUrl());
        else
        {   this.getConcertInfo(message);
            return getTicketInfo(chartId, messageId,index, message);
        }
        ConcertModel concert  =cacheMap.get(chartId).get(messageId).get(index);
        String res = analyzeTicketInfo(concert, ticketModelList);
        if (concert.getShouldBeMonitored()) {
            messageForFront.setCallbackType(CallbackType.IF_SHOULD_BE_MONITORED.getValue());
            payload = concert.toString() + res +
                    "\n \nХотите отслеживать появление билетов в фанзону или на танцпол?";
        }else{
            endOfWorkForThisEvent(chartId,messageId);
            payload = concert.toString()+ res;
        }
        messageForFront.setPayload(Serializers.serialize(payload));
        return messageForFront;
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
            sb.append("Билетов на танцпол и фанзону нет.\n");
        }
        if ((temp[0] == 0)&&(temp[1]==0)) sb.append("Билетов в другие зоны нет.\n");
        else sb.append("Минимальная стоимость билетов в другие зоны составляет: ").append(minCost.toString()).append(", максимальная: ").append(maxCost.toString()).append(".");
        return sb.toString();
    }

    @Override
    public ConcertModel getModel(Long chartId, int messageId){
        return cacheMap.get(chartId).get(messageId).get(0);
    }


    @Override
    public void endOfWorkForThisEvent(Long chartId, int messageId){
        logger.info("Clearing cache {}, {}", chartId,messageId);
        //messageType =  MessageType.HELLO;
        cacheMap2.remove(messageId);
        if (cacheMap.get(chartId).size()==0)
            cacheMap.remove(chartId);
    }


    @Override
    public Boolean checkingTickets(User user){
        List <TicketModel> ticketModelList = htmlParser.getTicketInfoFromHtml(user.getConcert().getConcertUrl());
        //обработка ислючения если нет инета - null point или если уже нет события
        String message = analyzeTicketInfo(user.getConcert(), ticketModelList);
        if (!user.getConcert().getShouldBeMonitored()){
            logger.info("Переключение флага ShouldBeMonitored");
            user.setMonitoringSuccessful(true);
            user.setMessageText(message);
            return true;
        }
        if (user.getDateOfMonitorFinish().before(new Date())) {
            logger.info("Переключение флага setDateExpired");
            user.setDateExpired(true);
            return true;
        }
         return false;
    }

}
