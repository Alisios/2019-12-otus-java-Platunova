package ru.otus.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Cacheable
@Table(name = "tUsers")
@Getter
@ToString
@Setter
@EqualsAndHashCode(exclude = {"dateOfMonitorFinish"})
@NoArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 129348938L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private long id;

    @Column(name = "chatId")
    private long chatId;

    @OneToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "concert_id", nullable = false)
    private ConcertModel concert;

    @Column(name = "isMonitoringSuccessful")
    private Boolean isMonitoringSuccessful = false;

    @Column(name = "isDateExpired")
    private Boolean isDateExpired = false;

    @Column(name = "dateOfMonitorFinish")
    private Date dateOfMonitorFinish;

    @Column(name = "messageText")
    private String messageText = "";


    public User(long chatId, ConcertModel concert, Date dateOfMonitorFinish) {
        this.chatId = chatId;
        this.concert = concert;
        this.dateOfMonitorFinish = dateOfMonitorFinish;
    }

    public User(Long id, long chatId, ConcertModel concert, Date dateOfMonitorFinish) {
        this.id = id;
        this.chatId = chatId;
        this.concert = concert;
        this.dateOfMonitorFinish = dateOfMonitorFinish;
    }


    @JsonManagedReference
    public void setConcert(ConcertModel concert) {
        this.concert = concert;
    }

}
