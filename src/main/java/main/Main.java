package main;

import model.Customer;
import parsers.csvparser.CSVParser;
import parsers.xmlparser.XMLParser;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String pathname = scanner.nextLine();

        File file = new File(pathname);

        if(pathname.endsWith(".xml")) {
            XMLParser xmlParser = new XMLParser();
            xmlParser.parseXML(file);
            List<Customer> xmlCustomers = xmlParser.getCustomerList();
            for (Customer cust : xmlCustomers) {
                System.out.println(cust);
            }
        } else if (pathname.endsWith(".txt")){
            CSVParser csvParser = new CSVParser();
            csvParser.parse(file);
            List<Customer> customers = csvParser.getCustomerList();
            for (Customer c : customers) {
                System.out.println(c);
            }
            System.out.println("csv");
        } else {
            System.out.println("PODAJ PLIK Z ODPOWIEDNIM ROZSZERZENIEM!");
        }
    }
}
