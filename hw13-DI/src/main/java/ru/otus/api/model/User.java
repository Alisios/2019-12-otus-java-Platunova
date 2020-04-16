package ru.otus.api.model;

import javax.persistence.*;

@Entity
@Table(name = "tUsers")
public class User {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private long id;

  @Column(name = "name")
  private String name;

  @Column(name = "age")
  private int age;

//  @OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
//  @JoinColumn(name = "address_id", nullable=true)
//  private AddressDataSet address;
//
//  @OneToMany(mappedBy = "ownerOfPhone", cascade = CascadeType.ALL, orphanRemoval = true)
//  private List<PhoneDataSet> phones ;

  @Column(name = "login")
  private  String login;

  private  String password;

  public User() {
  }

  public User(String name, int age,  String login, String password) {
    this.name = name;
    this.age = age;
    this.login = login;
    this.password = password;
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


  public void setLogin(String login) {
    this.login = login;
  }

  public void setPassword(String password) {
    this.password = password;
  }
  public String getLogin() {
    return login;
  }

  public String getPassword() {
    return password;
  }

  @Override
  public String toString() {
    return "User{" +
            "name='" + name +
            ", login=" + login +'\'' +
            '}';
  }
}
