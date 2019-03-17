package parsers.csvparser;

import dbconnection.DatabaseOperations;
import model.Contact;
import model.Customer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CSVParser {

    private Customer customer;
    private List<Contact> contactList;
    private DatabaseOperations databaseOperations;

    public CSVParser(DatabaseOperations databaseOperations) {
        this.databaseOperations=databaseOperations;
    }

    public void parse(File file){

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            CSVContactMatcher contactMatcher = new CSVContactMatcher();
            String str;
            while((str=bufferedReader.readLine())!=null) {
                String[] data = str.split(",");
                customer = new Customer();
                contactList = new ArrayList<>();
                customer.setName(data[0]);
                customer.setSurname(data[1]);
                if(!data[2].isEmpty()) customer.setAge(data[2]);
                for(int i=4; i<data.length; i++) {
                    Contact contact = contactMatcher.matchContact(data[i]);
                    contactList.add(contact);
                }
                customer.setContacts(contactList);
                databaseOperations.write(customer);
            }
            databaseOperations.eof();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}