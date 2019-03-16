package model;

public class Contact {

    int id;
    private ContactType contactType;
    private String contact;

    public Contact() {
    }

    public Contact(ContactType contactType, String contact) {
        this.contactType = contactType;
        this.contact = contact;
    }

    public ContactType getContactType() {
        return contactType;
    }

    public void setContactType(ContactType contactType) {
        this.contactType = contactType;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "contactType=" + contactType +
                ", contact='" + contact + '\'' +
                '}';
    }
}
