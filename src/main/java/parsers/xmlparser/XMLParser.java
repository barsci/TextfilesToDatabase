package parsers.xmlparser;

import model.Contact;
import model.ContactType;
import model.Customer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class XMLParser extends DefaultHandler {

    private Customer customer;
    private List<Customer> customerList = new ArrayList<>();
    private List<Contact> contactList;
    private StringBuilder data;

    public List<Customer> getCustomerList() {
        return customerList;
    }

    boolean bName = false;
    boolean bSurname = false;
    boolean bAge = false;
    boolean bEmail = false;
    boolean bPhone = false;
    boolean bJabber = false;
    boolean bUnknown = false;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (qName.equalsIgnoreCase("person")) {
            customer = new Customer();
            contactList =  new ArrayList<>();
        } else if (qName.equalsIgnoreCase("name")) {
            bName = true;
        } else if (qName.equalsIgnoreCase("surname")) {
            bSurname = true;
        } else if (qName.equalsIgnoreCase("age")) {
            bAge = true;
        } else if (qName.equalsIgnoreCase("phone")) {
            bPhone = true;
        } else if (qName.equalsIgnoreCase("email")) {
            bEmail = true;
        } else if (qName.equalsIgnoreCase("jabber")) {
            bJabber = true;
        }

        data = new StringBuilder();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (bName) {
            customer.setName(data.toString());
            bName = false;
        } else if (bSurname) {
            customer.setSurname(data.toString());
            bSurname = false;
        } else if (bAge) {
            customer.setAge(Integer.valueOf(data.toString()));
            bAge = false;
        } else if (bEmail) {
            contactList.add(new Contact(ContactType.EMAIL, data.toString()));
            bEmail = false;
        } else if (bPhone) {
            contactList.add(new Contact(ContactType.PHONE, data.toString()));
            bPhone = false;
        } else if (bJabber) {
            contactList.add(new Contact(ContactType.JABBER, data.toString()));
            bJabber = false;
        } else if (bUnknown) {
            contactList.add(new Contact(ContactType.UNKNOWN, data.toString()));
            bUnknown = false;
        }

        if (qName.equalsIgnoreCase("person")) {
            customer.setContacts(contactList);
            customerList.add(customer);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        data.append(new String(ch, start, length));
    }
}