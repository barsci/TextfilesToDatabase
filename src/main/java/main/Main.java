package main;

import model.Customer;
import org.xml.sax.SAXException;
import parsers.csvparser.CSVParser;
import parsers.xmlparser.XMLParser;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLParser handler = new XMLParser();
            saxParser.parse(new File("C:/Users/Marcin/Downloads/dane-osoby.xml"), handler);
            List<Customer> customers = handler.getCustomerList();
            for(Customer c: customers) {
                System.out.println(c);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        System.out.println();
        CSVParser csvParser = new CSVParser();
        csvParser.parse(new File("C:/Users/Marcin/Downloads/dane-osoby.txt"));
        List<Customer> customers = csvParser.getCustomerList();
        for(Customer c: customers) {
            System.out.println(c);
        }
    }
}
