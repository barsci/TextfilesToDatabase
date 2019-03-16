package model;

import java.util.List;

public class Customer {
    int id;
    String name;
    String surname;
    String age;
    List<Contact> contacts;

    public Customer(){

    }

    public Customer(String name, String surname, String age, List<Contact> contacts) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.contacts = contacts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", age=" + age +
                '}'+ '\n' + contacts.toString();
    }
}