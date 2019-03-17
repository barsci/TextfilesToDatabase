package parsers.xmlparser;

import dbconnection.DatabaseOperations;
import model.Contact;
import model.ContactType;
import model.Customer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class XMLParser extends DefaultHandler {

    private Customer customer;
    private List<Contact> contactList;
    private StringBuilder data;
    private DatabaseOperations databaseOperations;

    private boolean bName = false;
    private boolean bSurname = false;
    private boolean bAge = false;
    private boolean bEmail = false;
    private boolean bPhone = false;
    private boolean bJabber = false;
    private boolean bUnknown = false;

    public XMLParser(DatabaseOperations databaseOperations) {
        this.databaseOperations=databaseOperations;
    }

    public void parseXML(File file) {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.parse(file, this);
            System.out.println("parsed file "+file.getPath());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println("omitting file "+file.getPath());
        }
    }

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
        } else if (qName.equalsIgnoreCase("persons")) {
        } else if (qName.equalsIgnoreCase("city")) {
        } else if (qName.equalsIgnoreCase("contacts")) {
        } else {
            bUnknown = true;
        }

        data = new StringBuilder();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        data.append(new String(ch, start, length));
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        String result = data.toString();

        if (bName) {
            customer.setName(result);
            bName = false;
        } else if (bSurname) {
            customer.setSurname(result);
            bSurname = false;
        } else if (bAge) {
            customer.setAge(result);
            bAge = false;
        } else if (bEmail) {
            contactList.add(new Contact(ContactType.EMAIL, result));
            bEmail = false;
        } else if (bPhone) {
            contactList.add(new Contact(ContactType.PHONE, result));
            bPhone = false;
        } else if (bJabber) {
            contactList.add(new Contact(ContactType.JABBER, result));
            bJabber = false;
        } else if (bUnknown) {
            contactList.add(new Contact(ContactType.UNKNOWN, result));
            bUnknown = false;
        }

        if (qName.equalsIgnoreCase("person")) {
            customer.setContacts(contactList);
            try {
                databaseOperations.write(customer);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (qName.equalsIgnoreCase("persons")) {
            try {
                databaseOperations.eof();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}