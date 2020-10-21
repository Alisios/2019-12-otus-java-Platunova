package ru.otus.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(exclude = {"id"})
@Entity
@Table(name = "tTicket")
public class TicketModel implements Serializable {
    private static final long serialVersionUID = 129348556L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private long id;

    @Column(name = "type")
    private String type;

    @Column(name = "cost")
    private String cost;

    @ManyToOne(optional = false)
    @JoinColumn(name = "concert_id", nullable = false)
    @JsonBackReference
    private ConcertRestModel owner = new ConcertRestModel(0, "", "", "", "", new ArrayList<TicketModel>(), "");


    public TicketModel(String type, String cost) {
        this.type = type;
        this.cost = cost;
    }

    @JsonBackReference
    public void setOwner(ConcertRestModel owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return type + ": " +
                cost;
    }
}
