package ru.otus.api.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tAddress")
public
class AddressDataSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "street")
    private String street;

    @OneToOne(optional = false, mappedBy = "address")
    private User owner;


    public AddressDataSet() {

    }

    public AddressDataSet(String street, User owner) {
        this.street = street;
        this.owner = owner;

    }

    @Override
    public String toString() {
        return "AddressDataSet{" +
                "id=" + id +
                ", street='" + street +
                ", ownerId=" + owner.getId() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressDataSet that = (AddressDataSet) o;
        return id == that.id &&
                Objects.equals(street, that.street) &&
                Objects.equals(owner.getId(), that.owner.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, street, owner.getId());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
