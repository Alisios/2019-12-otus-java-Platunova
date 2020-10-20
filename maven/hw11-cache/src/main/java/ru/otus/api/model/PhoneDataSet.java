package ru.otus.api.model;


import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tPhones")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class PhoneDataSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "number")
    private String number;

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    @ManyToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User ownerOfPhone;

    public PhoneDataSet() {
    }

    public PhoneDataSet(String number, User ownerOfPhone) {
        this.number = number;
        this.ownerOfPhone = ownerOfPhone;
    }

    public long getId() {
        return id;
    }

    public User getOwnerOfPhone() {
        return ownerOfPhone;
    }

    public String getNumber() {
        return number;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setOwnerOfPhone(User ownerOfPhone) {
        this.ownerOfPhone = ownerOfPhone;
    }

    @Override
    public String toString() {
        return "PhoneDataSet{" +
                "id=" + id +
                ", number='" + number +
                ", ownerId=" + ownerOfPhone.getId() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneDataSet that = (PhoneDataSet) o;
        return id == that.id &&
                Objects.equals(number, that.number) &&
                Objects.equals(ownerOfPhone, that.ownerOfPhone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, ownerOfPhone);
    }
}
