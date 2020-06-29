package ru.otus.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Cacheable
@Table(name = "tConcertModel")
public class ConcertModel implements Serializable {
    private static final long serialVersionUID = 129348939L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "artist")
    private String artist;
    @Column(name = "date")
    private String date;
    @Column(name = "place")
    private String place;
    @Column(name = "concertUrl")
    private String concertUrl;
    @Column(name = "shouldBeMonitored")
    private Boolean shouldBeMonitored = false;

    @OneToOne(optional = false, mappedBy = "concert")//, cascade = CascadeType.ALL)// cascade = CascadeType.ALL)//,fetch = FetchType.EAGER, orphanRemoval = true)//)
    private User owner = new User (0,null, new Date());

    @Transient
    private List <TicketModel> tickets = new ArrayList<>();

    public ConcertModel(){};

    public ConcertModel (String artist, String date, String place,String concertUrl){
        this.artist = artist;
        this.date = date;
        this.place = place;
        this.concertUrl = concertUrl;
    };

    public ConcertModel (String artist, String date, String place,String concertUrl, List <TicketModel>  tickets){
        this.artist = artist;
        this.date = date;
        this.place = place;
        this.concertUrl = concertUrl;
        this.tickets = tickets;
    };

    @JsonBackReference
    public void setOwner(User owner) {
        this.owner = owner;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }



    public List<TicketModel> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketModel> tickets) {
        this.tickets = tickets;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getConcertUrl() {
        return concertUrl;
    }

    public void setConcertUrl(String concertUrl) {
        this.concertUrl = concertUrl;
    }

    public Boolean getShouldBeMonitored() {
        return shouldBeMonitored;
    }

    public void setShouldBeMonitored(Boolean shouldBeMonitored) {
        this.shouldBeMonitored = shouldBeMonitored;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConcertModel concert = (ConcertModel) o;
        return Objects.equals(artist, concert.artist) &&
                Objects.equals(date, concert.date) &&
                Objects.equals(place, concert.place) &&
                Objects.equals(shouldBeMonitored, concert.shouldBeMonitored) &&
                Objects.equals(concertUrl, concert.concertUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artist, date, place, concertUrl);
    }

    @Override
    public String toString() {
        return "Исполнитель: " + artist + '\n' +
                "Дата: " + date + '\n' +
                "Место проведения: " + place + '\n' +
                "Ссылка: " + concertUrl;//+ '\n'+
    }
}
