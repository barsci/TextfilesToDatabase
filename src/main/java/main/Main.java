package main;

import model.Customer;
import parsers.csvparser.CSVParser;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println();
        CSVParser csvParser = new CSVParser();
        csvParser.parse(new File("C:/Users/Marcin/Downloads/dane-osoby.txt"));
        List<Customer> customers = csvParser.getCustomerList();
        for(Customer c: customers) {
            System.out.println(c);
        }
    }
}
