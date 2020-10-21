package ru.otus.backend.model;

import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;


import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@EqualsAndHashCode(exclude = {"id", "tickets"})
public class ConcertRestModel implements Serializable {
    private static final long serialVersionUID = 129348555L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "concert_id_seq")
    @SequenceGenerator(name = "concert_id_seq", sequenceName = "concert_id_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private long id;

    @Column(name = "artist")
    private String artist;
    @Column(name = "date")
    private String date;
    @Column(name = "place")
    private String place;
    @Column(name = "concertUrl")
    private String concertUrl;


    @Column(name = "tickets")
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    List<TicketModel> tickets = new ArrayList<>();

    @Column(name = "ticketsToString")
    private String ticketsToString;

    public ConcertRestModel(String artist, String date, String place, String concertUrl, List<TicketModel> tickets) {
        this.artist = artist;
        this.date = date;
        this.place = place;
        this.concertUrl = concertUrl;
        this.tickets = tickets;
        this.ticketsToString = tickets.toString();
    }

    public List<TicketModel> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketModel> newlist) {
        this.tickets = new ArrayList<>(newlist);
        this.tickets.forEach(ticket -> ticket.setOwner(this));
    }

    public void addTickets(List<TicketModel> newlist) {
        newlist.forEach(ticket -> ticket.setOwner(this));
        this.tickets.addAll(newlist);

    }
}
