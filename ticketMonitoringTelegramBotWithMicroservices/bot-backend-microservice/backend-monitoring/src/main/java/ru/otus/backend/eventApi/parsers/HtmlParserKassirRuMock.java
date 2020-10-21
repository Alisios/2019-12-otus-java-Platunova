package ru.otus.backend.eventApi.parsers;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.TicketModel;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

@Component("htmlParserKassirRuMock")
public class HtmlParserKassirRuMock implements HtmlParser {
    private final String URL_KASSIR = "http://localhost:8081/msk.kassir.ru/category/?keyword=";//"https://ponominalu.ru/";//
    private List<String> artists;

    private Elements getDocument(String mesageUrl) throws IOException {

        // mesageUrl = String.join("+", artists);
        try {
            Document doc =
                    Jsoup.connect(URL_KASSIR + mesageUrl)
                            .userAgent("Chrome/4.0.249.0 Safari/532.5")
                            .referrer("http://www.google.com")
                            .get();
            return doc.select("body > div.site > main > div.container.page-category.inner-page > div.js-filter-content.js-pager-container > div > div");
//"https://api.retailrocket.net/api/2.0/recommendation/personal/5c04f01e97a5252b48ef35da/?&stockId=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&session=5ea340977c84cf00017ac022&pvid=657781070454668&isDebug=false&format=json"
        } catch (UnknownHostException ex) {
            throw new UnknownHostException("Impossible to connect to the Internet or URL is incorrect: " + ex.getMessage());
        } catch (IOException ex) {
            throw new IOException("Problems with parsing URL , URL may be incorrect" + ex.getMessage());
        }
        //return null;
    }

    @Override
    public List<ConcertModel> getEventsFromHtml(String message) throws IOException {
        String mesageUrl = "";
        artists = Arrays.asList(message.split(" "));
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

        if (message.trim().toLowerCase().equals("the"))
            return Collections.emptyList();
        List<ConcertModel> elementList = new ArrayList<>();
        for (Element element : this.getDocument(mesageUrl).select("div.caption")) {
            String title = element.select("div.title").first().text();
            if (((artists.size() <= 1) && (title.toLowerCase().contains(message.toLowerCase())))
                    || ((artists.size() > 1) && (title.toLowerCase().contains(artists.get(0).toLowerCase()) ||
                    (title.toLowerCase().equals(" " + artists.get(1).toLowerCase())))))
//body > div.site > main > div:nth-child(3) > div > div.rr-widget.rr-widget-5cbf2cc897a5251814bc9618-search > div.retailrocket-items > div:nth-child(1) > div
//                ((title.toLowerCase().contains(artists.get(0).toLowerCase()) && !title.toLowerCase().equals("the") ||
//                        (title.toLowerCase().equals(" " + artists.get(1).toLowerCase())))) ||
//                        ((title.toLowerCase().equals(artists.get(0).toLowerCase()) && title.toLowerCase().equals("the") &&
//                                (title.toLowerCase().equals(" " + artists.get(1).toLowerCase()))))
                //body > div.site > main > div:nth-child(3) > div > div.rr-widget.rr-widget-5cbf2cc897a5251814bc9618-search
//https://api.retailrocket.net/api/2.0/recommendation/Search/5c04f01e97a5252b48ef35da/?&stockId=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&phrase=twenty%20one&session=5ea340977c84cf00017ac022&pvid=657781070454668&isDebug=false&format=json
                elementList.add(new ConcertModel(title,
                        element.select("div.date").first().text(),
                        element.select("div.place").first().text(),
                        element.select("a").first().attr("href")));
        }
        try {
            if (elementList.isEmpty()) {

                StringBuilder s1 = new StringBuilder();
                s1.append("{")////Москва&phrase=кис%20кис&
                        .append(Jsoup.connect("https://api.retailrocket.net/api/2.0/recommendation/Search/5c04f01e97a5252b48ef35da/?&stockId=Москва&phrase=" + mesageUrl + "&session=5ea340977c84cf00017ac022&pvid=657781070454668&isDebug=false&format=json")
                                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                                .referrer("http://www.google.com")
                                .ignoreContentType(true).execute().body().split("\\{\"OldPrice\":0,")[1])
                        .deleteCharAt(s1.length() - 1);
                Map<String, Object> retMap = new Gson().fromJson(s1.toString(), new TypeToken<HashMap<String, Object>>() {
                }.getType());
                LinkedTreeMap tr = (LinkedTreeMap) retMap.get("Params");
                elementList.add(new ConcertModel(retMap.get("Name").toString(),
                        tr.get("Дата").toString(),
                        tr.get("Площадка").toString(),
                        retMap.get("Url").toString()));
            }
            return elementList;

        } catch (UnknownHostException ex) {
            throw new UnknownHostException("Impossible to connect to the Internet or URL is incorrect: " + ex.getMessage());
        } catch (IOException ex) {
            throw new IOException("Problems with parsing URL , URL may be incorrect" + ex.getMessage());
        }

    }

    @Override
    public List<TicketModel> getTicketInfoFromHtml(ConcertModel concertModel) throws IOException {
        List<TicketModel> elementList = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(concertModel.getConcertUrl()).userAgent("Chrome/4.0.249.0 Safari/532.5").referrer("http://www.google.com").get();
            Elements tableOfPrices = doc.select("#prices > table > tbody");
            for (Element element : tableOfPrices.select("tr")) {
                elementList.add(new TicketModel(element.select(" > td.col-sector").first().text(),
                        element.select("> td.text-nowrap.col-prices").first().text()
                ));
            }
            return elementList;
        } catch (UnknownHostException ex) {
            throw new UnknownHostException("Impossible to connect to the Internet or URL is incorrect: " + ex.getMessage());
        } catch (IOException e) {
            throw new IOException("Problems with parsing kassir.ru: " + e.getMessage());
        }
    }
}
