package ru.otus.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class User implements Serializable  {

    private static final long serialVersionUID = 129348938L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",unique = true, nullable = false)
    private long id;

    @Column(name = "chatId")
    private long chatId;

    @OneToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "concert_id", nullable=false)
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

    public Boolean getIsDateExpired() {
        return isDateExpired;
    }

    public void setIsDateExpired(Boolean isDateExpired) {
        this.isDateExpired = isDateExpired;
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

    @JsonManagedReference
    public void setConcert(ConcertModel concert) {
        this.concert = concert;
    }

    public Boolean getIsMonitoringSuccessful() {
        return isMonitoringSuccessful;
    }

    public void setIsMonitoringSuccessful(Boolean isMonitoringSuccessful) {
        this.isMonitoringSuccessful = isMonitoringSuccessful;
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
