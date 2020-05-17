package ru.otus.backend.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Cacheable
@Table(name = "tUsers")
public class User implements Serializable  {

    private static final long serialVersionUID = 129348938L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    // @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id",unique = true, nullable = false)
    private long id;

    @Column(name = "chatId")
    private long chatId;

    @OneToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "concert_id", nullable=false)
//    @JoinTable(name="user_concert",
//            joinColumns=@JoinColumn(name="concert_id", nullable=false),
//            inverseJoinColumns=@JoinColumn(name="user_id"))
    private ConcertModel concert;

    @Column(name = "isMonitoringSuccessful")
    private Boolean isMonitoringSuccessful =  false;

    @Column(name = "isDateExpired")
    private Boolean isDateExpired  = false;

    @Column(name = "dateOfMonitorFinish")
    private Date dateOfMonitorFinish;

    @Column(name = "messageText")
    private String messageText="" ;

    public User() { }

    public User( long chatId, ConcertModel concert, Date dateOfMonitorFinish) {
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public long getChatId() {
        return chatId;
    }
    public String getMessageText() {
        return messageText;
    }

    public Boolean getDateExpired() {
        return isDateExpired;
    }

    public void setDateExpired(Boolean dateExpired) {
        isDateExpired = dateExpired;
    }
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public ConcertModel getConcert() {
        return concert;
    }

    public void setConcert(ConcertModel concert) {
        this.concert = concert;
    }

    public Boolean getMonitoringSuccessful() {
        return isMonitoringSuccessful;
    }

    public void setMonitoringSuccessful(Boolean monitoringSuccessful) {
        isMonitoringSuccessful = monitoringSuccessful;
    }

    public Date getDateOfMonitorFinish() {
        return dateOfMonitorFinish;
    }

    public void setDateOfMonitorFinish(Date dateOfMonitorFinish) {
        this.dateOfMonitorFinish = dateOfMonitorFinish;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return chatId == user.chatId &&
                id == user.id &&
                Objects.equals(concert, user.concert) &&
                Objects.equals(isMonitoringSuccessful, user.isMonitoringSuccessful) &&
                Objects.equals(isDateExpired, user.isDateExpired) &&
                Objects.equals(messageText, user.messageText);// &&
        // Objects.equals(dateOfMonitorFinish, user.dateOfMonitorFinish);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, concert, isMonitoringSuccessful, dateOfMonitorFinish,messageText);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", ChatId=" + chatId +
                ", concert=" + concert +
                ", messageText=" + messageText +
                "isMonitoringSuccessful=" + isMonitoringSuccessful +
                ", dateOfMonitorFinish=" + dateOfMonitorFinish +
                ", isDateExpired=" + isDateExpired +
                '}';
    }
}
