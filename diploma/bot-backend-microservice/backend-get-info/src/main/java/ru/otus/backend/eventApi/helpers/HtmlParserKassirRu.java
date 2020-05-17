package ru.otus.backend.eventApi.helpers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.otus.backend.model.ConcertModel;
import ru.otus.backend.model.TicketModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HtmlParserKassirRu implements HtmlParser {
    private final String URL_KASSIR = "https://msk.kassir.ru/category/?keyword=";
    private List<String> artists;

      private Elements getDocument(String message) {
        String mesageUrl ="";
         artists= Arrays.asList(message.split(" "));
        if (artists.size()==0){
            return null;
        }else if (artists.size()==1)
            mesageUrl = message;
        else
            mesageUrl = String.join("+", artists);
        try{
        Document doc =
                Jsoup.connect(URL_KASSIR+ mesageUrl)
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get();
            return doc.select("body > div.site > main > div.container.page-category.inner-page > div.js-filter-content.js-pager-container > div > div");

        }catch (IOException ex){
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }
        return null;
    }

    @Override
    public List<ConcertModel> getEventsFromHtml(String message) {
        List<ConcertModel> elementList = new ArrayList<>();
        for (Element element : this.getDocument(message).select("div.caption")) {
            String title = element.select("div.title").first().text();
            if (((artists.size()<=1) && (title.toLowerCase().contains(message.toLowerCase())))
                || ( (artists.size()>1) && (title.toLowerCase().contains(artists.get(0).toLowerCase()) ||
                    (title.toLowerCase().equals(" " + artists.get(1).toLowerCase())))))

//                ((title.toLowerCase().contains(artists.get(0).toLowerCase()) && !title.toLowerCase().equals("the") ||
//                        (title.toLowerCase().equals(" " + artists.get(1).toLowerCase())))) ||
//                        ((title.toLowerCase().equals(artists.get(0).toLowerCase()) && title.toLowerCase().equals("the") &&
//                                (title.toLowerCase().equals(" " + artists.get(1).toLowerCase()))))
            elementList.add(new ConcertModel(title,
                    element.select("div.date").first().text(),
                    element.select("div.place").first().text(),
                    element.select("a").first().attr("href")));
        }
        return elementList;
    }

    @Override
    public List<TicketModel>  getTicketInfoFromHtml(String urlFinal){
        List<TicketModel> elementList = new ArrayList<>();
        try{
        Document doc = Jsoup.connect(urlFinal).userAgent("Chrome/4.0.249.0 Safari/532.5").referrer("http://www.google.com").get();
        Elements tableOfPrices =  doc.select("#prices > table > tbody");
        for (Element element : tableOfPrices.select("tr")){
            elementList.add(new TicketModel(element.select(" > td.col-sector").first().text(),
                    "0", // element.select("> td.col-amount").first().text()
                    element.select("> td.text-nowrap.col-prices").first().text()
            ));
        }
        return elementList;
        }catch (IOException ex){
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }
            return null;
        }
}
