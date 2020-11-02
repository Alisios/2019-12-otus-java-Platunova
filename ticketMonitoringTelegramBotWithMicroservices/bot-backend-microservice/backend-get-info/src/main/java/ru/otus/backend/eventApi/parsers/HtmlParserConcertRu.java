package ru.otus.backend.eventApi.parsers;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.TicketModel;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

@Component("htmlParserConcertRu")
public class HtmlParserConcertRu implements HtmlParser {
    private static Logger logger = LoggerFactory.getLogger(HtmlParserConcertRu.class);
    private final String URL_CONCERT = "https://www.concert.ru/Search?q=";
    private final String CONCERT_RU = "https://www.concert.ru";
    private List<String> artists;


    private List getDocument(String message) throws IOException {
        String mesageUrl = "";
        artists = Arrays.asList(message.trim().split(" "));
        if (artists.size() == 0) {
            return Collections.emptyList();
        } else if (artists.size() == 1)
            mesageUrl = message;
        else {
            if (artists.get(0).toLowerCase().equals("the")) {
                if (artists.size() == 2)
                    mesageUrl = artists.get(1);
                else {
                    artists.remove(0);
                    mesageUrl = String.join("+", artists);
                }
            } else
                mesageUrl = String.join("+", artists);
        }
        try {
            Document doc =
                    Jsoup.connect(URL_CONCERT + mesageUrl)
                            .userAgent("Chrome/4.0.249.0 Safari/532.5")
                            .referrer("http://www.google.com")
                            .get();
            Gson gson = new Gson();
            List list = (List) (gson.fromJson(doc.body().text(), HashMap.class).get("Actions"));
            return list;
        } catch (UnknownHostException ex) {
            throw new UnknownHostException("Impossible to connect to the Internet or URL is incorrect: " + ex.getMessage());
        } catch (IOException ex) {
            throw new IOException("Problems with parsing URL in concert.ru: , URL may be incorrect" + ex.getMessage());
        }
    }

    @Override
    public List<ConcertModel> getEventsFromHtml(String message) throws IOException {
        if (message == null || message.equals("") || message.trim().toLowerCase().equals("the"))
            return Collections.emptyList();
        List<ConcertModel> elementList = new ArrayList<>();
        List list = this.getDocument(message.trim());
        if (list.isEmpty()) return Collections.emptyList();
        for (Object element : list) {
            LinkedTreeMap tr = (LinkedTreeMap) element;
            elementList.add(new ConcertModel(tr.get("Name").toString(),
                    tr.get("Dates12").toString() + " " + tr.get("Dates2").toString(),
                    tr.get("PlaceName").toString(),
                    CONCERT_RU + tr.get("LinkUrl").toString()));
        }
        return elementList;
    }

    @Override
    public List<TicketModel> getTicketInfoFromHtml(ConcertModel concertModel) throws IOException {
        if (concertModel == null)
            return Collections.emptyList();
        List<TicketModel> elementList = new ArrayList<>();
        try {
            Document doc1 = Jsoup.connect(concertModel.getConcertUrl()).userAgent("Chrome/4.0.249.0 Safari/532.5").referrer("http://www.google.com").get();
            concertModel.setDate(doc1.select("#eventDatesDiv > table > tbody > tr > td:nth-child(2) > span").text()
                    + doc1.select("#eventDatesDiv > table > tbody > tr > td:nth-child(3) > div").text());
            String url2 = CONCERT_RU + doc1.select(" #eventDatesDiv > table > tbody > tr > td:nth-child(6) > div > a").attr("href");
            Document doc2 = Jsoup.connect(url2).userAgent("Chrome/4.0.249.0 Safari/532.5").referrer("http://www.google.com").get();

            Elements tableOfPrices = doc2.select("#buyContentsDiv > div.buyChoice__list > div.ticketsTable");//"#buyContentsDiv > div.buyChoice__list > div").get(0).select("div.ticketsTable");//getElementsByClass("ticketsTable");
            for (Element element : tableOfPrices.select("tr")) {
                elementList.add(new TicketModel(element.getElementsByClass("ticketsTable__type").text().replace(" / -", ""),

                        element.getElementsByClass("ticketsTable__price").text()));
            }
            if (!elementList.isEmpty())
                elementList.remove(0);
            Elements tableOfPricesFan = doc2.select("#buyContentsDiv > div.buyChoice__list > div.buyChoice__danceParterre");//"#buyContentsDiv > div.buyChoice__list > div").get(0).select("div.ticketsTable");//getElementsByClass("ticketsTable");
            elementList.add(new TicketModel(tableOfPricesFan.select("tr:nth-child(1) > td.buyChoice__ticketType").text(),

                    tableOfPricesFan.select("tr:nth-child(1) > td:nth-child(2)").text()));//#buyContentsDiv > div.buyChoice__list > div.buyChoice__danceParterre > table > tbody > tr:nth-child(2) > td.buyChoice__ticketType
            elementList.add(new TicketModel(tableOfPricesFan.select("tr:nth-child(2) > td.buyChoice__ticketType").text(),

                    tableOfPricesFan.select("tr:nth-child(2) > td:nth-child(2)").text()));
            return elementList;
        } catch (UnknownHostException ex) {
            throw new UnknownHostException("Impossible to connect to the Internet or URL is incorrect: " + ex.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The URL " + concertModel.getConcertUrl() + " is incorrect: " + e.getMessage());
        } catch (IOException e) {
            throw new IOException("Problems with parsing Concert.ru: " + e.getMessage());
        }
    }
}
