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

        Scanner scanner = new Scanner(System.in);
        String pathname = scanner.nextLine();
        File file = new File(pathname);
        DBConnection dbConnection = DBConnection.getInstance();
        try {
            if(pathname.endsWith(".xml")) {
                dbConnection.setDatabaseConnection();
                XMLParser xmlParser = new XMLParser(dbConnection);
                xmlParser.parseXML(file);
            } else if (pathname.endsWith(".txt")){
                dbConnection.setDatabaseConnection();
                CSVParser csvParser = new CSVParser(dbConnection);
                csvParser.parse(file);
                System.out.println("csv");
            } else {
                System.out.println("PODAJ PLIK Z ODPOWIEDNIM ROZSZERZENIEM!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}