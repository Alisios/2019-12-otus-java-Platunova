package ru.otus.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Cacheable
@Table(name = "tConcertModel")
@Getter
@Setter
@EqualsAndHashCode(exclude = {"id", "tickets"})
@NoArgsConstructor
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

    @OneToOne(optional = false, mappedBy = "concert")
    private User owner = new User(0, null, new Date());

    // @Transient
    private List<TicketModel> tickets = new ArrayList<>();


    public ConcertModel(String artist, String date, String place, String concertUrl) {
        this.artist = artist;
        this.date = date;
        this.place = place;
        this.concertUrl = concertUrl;
    }

    public ConcertModel(String artist, String date, String place, String concertUrl, List<TicketModel> tickets) {
        this.artist = artist;
        this.date = date;
        this.place = place;
        this.concertUrl = concertUrl;
        this.tickets = tickets;
    }

    @JsonBackReference
    public void setOwner(User owner) {
        this.owner = owner;
    }


    @Override
    public String toString() {
        return "Исполнитель: " + artist + '\n' +
                "Дата: " + date + '\n' +
                "Место проведения: " + place + '\n' +
                "Ссылка: " + concertUrl;
    }
}
