package main;

import dbconnection.DBConnection;
import model.Customer;
import parsers.csvparser.CSVParser;
import parsers.xmlparser.XMLParser;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        List<Customer> customers = new ArrayList<>();
        DBConnection dbConnection;
        Scanner scanner = new Scanner(System.in);
        String pathname = scanner.nextLine();

        File file = new File(pathname);

        if(pathname.endsWith(".xml")) {
            XMLParser xmlParser = new XMLParser();
            xmlParser.parseXML(file);
            customers = xmlParser.getCustomerList();
            for (Customer cust : customers) {
                System.out.println(cust);
            }
        } else if (pathname.endsWith(".txt")){
            CSVParser csvParser = new CSVParser();
            csvParser.parse(file);
            customers = csvParser.getCustomerList();
            for (Customer c : customers) {
                System.out.println(c);
            }
            System.out.println("csv");
        } else {
            System.out.println("PODAJ PLIK Z ODPOWIEDNIM ROZSZERZENIEM!");
        }

        try {
            dbConnection = DBConnection.getInstance();
            for(Customer customer: customers) {
                dbConnection.insertCustomerToDatabase(customer);
            }
            System.out.println("Added to DB");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
