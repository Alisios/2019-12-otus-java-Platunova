package ru.otus.api.model;


import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tUsers")

@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private long id;

  @Column(name = "name")
  private String name;

  @Column(name = "age")
  private int age;

  @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
  @OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "address_id", nullable=false)
  private AddressDataSet address;


  @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
  @OneToMany(mappedBy = "ownerOfPhone", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PhoneDataSet> phones ;

  public User() {
  }

  public User(String name, int age) {
    this.name = name;
    this.age = age;
    this.phones= new ArrayList<>();
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public AddressDataSet getAddress() {
    return address;
  }

  public void setAddress(AddressDataSet address) {
    this.address = address;
  }

  public List<PhoneDataSet> getPhones() {
    return phones;
  }

  public void setPhones(List<PhoneDataSet> phones) {
    this.phones = phones;
  }

  @Override
  public String toString() {
    return "User{" +
            "id=" + id +
            ", name='" + name +
            ", age='" + age +
            ", address=" + address +
            ", phones=" + phones +'\'' +
            '}';
  }
}
