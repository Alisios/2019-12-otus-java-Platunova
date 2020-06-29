package ru.otus.backend.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@Entity
@Cacheable
@Table(name = "tTicket")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TicketModel implements Serializable {
    private static final long serialVersionUID = 129348556L;

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
   // @GeneratedValue//(strategy = GenerationType.SEQUENCE)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticket_id_seq")
    @SequenceGenerator(name = "ticket_id_seq", sequenceName = "ticket_id_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private long id;

    @Column(name = "type")
    private String type;

    @Column(name = "cost")
    private String cost;

    @ManyToOne(optional = false)
    @JoinColumn(name = "concert_id", nullable=false)
    @JsonBackReference
    private ConcertRestModel owner = new ConcertRestModel(0,"","","","",new ArrayList<TicketModel>(),"");

    public TicketModel(String type, String cost){
        this.type = type;
        this.cost = cost;
    }

    @JsonBackReference
    public void setOwner(ConcertRestModel owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return type+": "+
                cost;
    }
}
