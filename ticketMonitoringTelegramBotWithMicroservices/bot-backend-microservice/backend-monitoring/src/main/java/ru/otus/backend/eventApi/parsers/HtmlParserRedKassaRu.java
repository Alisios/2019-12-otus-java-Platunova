package ru.otus.backend.eventApi.parsers;

import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.TicketModel;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

@Component("htmlParserRedKassaRu")
public class HtmlParserRedKassaRu implements HtmlParser {
    private final String URL_KASSIR = "https://redkassa.ru/events?q="; //"https://ponominalu.ru/";//
    private List<String> artists;


    public Elements getDocument(String message) throws IOException {
        String mesageUrl = "";
        artists = Arrays.asList(message.trim().split(" "));
        if (artists.size() == 0) {
            return null;
        } else if (artists.size() == 1)
            mesageUrl = message;
        else {
            if (artists.get(0).toLowerCase().equals("the")) {
                if (artists.size() == 2)
                    mesageUrl = artists.get(1);
                else {
                    artists.remove(0);
                    mesageUrl = String.join("%20", artists);
                }
            } else
                mesageUrl = String.join("%20", artists);
        }
        try {
            Document doc =
                    Jsoup.connect(URL_KASSIR + mesageUrl)
                            .userAgent("Chrome/4.0.249.0 Safari/532.5")
                            .referrer("http://www.google.com")
                            .get();

            return doc.select("#catalog-items-js");
        } catch (UnknownHostException ex) {
            throw new UnknownHostException("Impossible to connect to the Internet or URL is incorrect: " + ex.getMessage());
        } catch (IOException ex) {
            throw new IOException("Problems with parsing URL , URL may be incorrect" + ex.getMessage());
        }
    }

    @Override
    public List<ConcertModel> getEventsFromHtml(String message) throws IOException {
        List<ConcertModel> elementList = new ArrayList<>();
        if (message.trim().toLowerCase().equals("the"))
            return Collections.emptyList();
        for (Element element : this.getDocument(message.trim()).select("li:nth-child(1) > div")) {
            String title = element.select("div > div > a").first().text();
            Gson gson = new Gson();
            List<DataNode> htmlStr = element.getElementsByTag("script").dataNodes();
            elementList.add(new ConcertModel(title, element.select("div > div > div.event-snippet__info > span").first().text(),
                    element.select("div > div > div.event-snippet__info > a").first().text(),
                    gson.fromJson(htmlStr.get(0).getWholeData(), HashMap.class).get("url").toString()));
        }
        return elementList;
    }

    @Override
    public List<TicketModel> getTicketInfoFromHtml(ConcertModel concertModel) throws IOException {
        List<TicketModel> elementList = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(concertModel.getConcertUrl()).userAgent("Chrome/4.0.249.0 Safari/532.5").referrer("http://www.google.com").get();
            Elements tableOfPrices = doc.select("#vue-instance__tickets > div > div > div:nth-child(2) > div > div:nth-child(2) > div:nth-child(1)");//("#vue-instance__tickets > div > div > div:nth-child(2) > div");
            for (Element element : tableOfPrices.select("tr")) {
                elementList.add(new TicketModel(element.select(" > td.col-sector").first().text(),
                        element.select("> td.text-nowrap.col-prices").first().text()
                )); //#vue-instance__tickets > div > div > div:nth-child(2) > div > div:nth-child(2) > div:nth-child(1) > div:nth-child(1) > div > div.sector-row__title-wrap > p.sector-row__title
            }
            return elementList;
        } catch (UnknownHostException ex) {
            throw new UnknownHostException("Impossible to connect to the Internet or URL is incorrect: " + ex.getMessage());
        } catch (IOException e) {
            throw new IOException("Problems with parsing kassir.ru: " + e.getMessage());
        }
    }
}
