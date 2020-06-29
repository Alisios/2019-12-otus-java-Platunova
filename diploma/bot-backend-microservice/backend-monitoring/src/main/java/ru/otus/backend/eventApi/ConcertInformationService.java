package ru.otus.backend.eventApi;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import ru.otus.backend.eventApi.service.GetEventInfoService;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.TicketModel;
import ru.otus.backend.model.User;

import java.io.IOException;
import java.util.List;

/**
 * получение информации о концерте и билетах на него
 */

@Service
public class ConcertInformationService implements EventInformationService {

    private GetEventInfoService getEventInfoService;
    private GetEventInfoService getEventInfoServiceOptional;
    private GetEventInfoService getEventInfoServiceRest;

    ConcertInformationService() { ; }

    @Autowired
    public ConcertInformationService(@Qualifier("getEventInfoServiceRestApi") GetEventInfoService getEventInfoService,
                                     @Qualifier("getEventInfoServiceParsing") GetEventInfoService getEventInfoServiceOptional) {
        this.getEventInfoService = getEventInfoService;
        this.getEventInfoServiceOptional = getEventInfoServiceOptional;
        this.getEventInfoServiceRest = getEventInfoService;
    }

    @Override
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    @HystrixCommand(fallbackMethod = "getTicketInformationFallback", commandKey = "getTicketInfoInMonitoring")
    public String getTicketInformation(User user) throws IOException {
        return getTicketInformation(user.getConcert());
    }

    private String getTicketInformation(ConcertModel concert) throws IOException {
        return analyzeTicketInfo(concert, getEventInfoService.getTicketInformation(concert));
    }

    private String getTicketInformationFallback(User user) throws IOException {
        this.getEventInfoService = this.getEventInfoServiceOptional;
        String message = getTicketInformation(user.getConcert());
        this.getEventInfoService = this.getEventInfoServiceRest;
        return message;
    }

    private String analyzeTicketInfo(ConcertModel concert, List<TicketModel> ticketModelList) {
        concert.setShouldBeMonitored(false);
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
}
