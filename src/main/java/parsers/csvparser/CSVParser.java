package parsers.csvparser;

import dbconnection.DatabaseOperations;
import javafx.application.Platform;
import javafx.scene.control.Alert;
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

    public void parse(File file) {

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            CSVContactMatcher contactMatcher = new CSVContactMatcher();
            String str;
            boolean encounteredWrongLine = false;
            int parsedLinesCounter = 0;
            while((str=bufferedReader.readLine())!=null && !encounteredWrongLine) {
                ++parsedLinesCounter;
                String[] data = str.split(",");
                if (
                        data.length>3
                        && contactMatcher.customerNameSurnameCityMatcher(data[0])
                        && contactMatcher.customerNameSurnameCityMatcher(data[1])
                        && contactMatcher.customerNameSurnameCityMatcher(data[3])
                ) {
                    customer = new Customer();
                    contactList = new ArrayList<>();
                    customer.setName(data[0]);
                    customer.setSurname(data[1]);
                    if (!data[2].isEmpty()) customer.setAge(data[2]);
                    for (int i = 4; i < data.length; i++) {
                        Contact contact = contactMatcher.matchContact(data[i]);
                        contactList.add(contact);
                    }
                    customer.setContacts(contactList);
                    databaseOperations.write(customer);
                }
                else {
                    encounteredWrongLine=true;
                    System.out.println("Encountered error at line: "+ parsedLinesCounter + " in file: " +file.getPath());
                }
            }
            databaseOperations.eof();
        } catch (SQLException | IOException e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error parsing .csv file!");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            });
        }
    }
}