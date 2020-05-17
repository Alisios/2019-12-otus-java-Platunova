package ru.otus.backend.eventApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.otus.backend.eventApi.helpers.HtmlParser;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.TicketModel;
import ru.otus.backend.model.User;

import java.util.Date;
import java.util.List;

public class ConcertMonitoring implements MonitoringService {
    private static Logger logger = LoggerFactory.getLogger(ConcertMonitoring.class);

    private HtmlParser htmlParser;

    ConcertMonitoring(){;}

    public ConcertMonitoring(HtmlParser htmlParser){
        this.htmlParser = htmlParser;
    }

    @Override
     public String analyzeTicketInfo(ConcertModel concert, List <TicketModel> ticketModelList){
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
