package dbconnection;

import model.Contact;
import model.Customer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DBConnection implements DatabaseOperations{
    private static final String filename = "db.properties";
    private Connection con;
    private static DBConnection dbConnection;
    private List<Customer> customerList = new ArrayList<>();
    private int recordCount = 0;

    private DBConnection() {
    }

    public static DBConnection getInstance() {
        if(dbConnection == null){
            dbConnection = new DBConnection();
        }
        return dbConnection;
    }

    public void setDatabaseConnection() throws SQLException{
        Properties properties = new Properties();
        try {
            InputStream inputStream = DBConnection.class.getClassLoader().getResourceAsStream(filename);
            properties.load(inputStream);

            String url = properties.getProperty("jdbc.url");
            String username = properties.getProperty("jdbc.username");
            String password = properties.getProperty("jdbc.password");

            con = DriverManager.getConnection(url, username, password);
            if(inputStream!=null) {
                inputStream.close();
            }
        } catch (IOException e) {
            System.out.println("Cannot set connection config to database!");
        }
    }

    public void closeConnection() throws SQLException {
        con.close();
    }

    public void write(Customer customer) throws SQLException{

        recordCount += customer.getSize();
        customerList.add(customer);

        if(recordCount>1000) {
            pushCustomerListToDatabase();
            customerList = new ArrayList<>();
            recordCount=0;
        }
    }

    public void eof() throws SQLException{
        if(customerList.size()!=0) {
            pushCustomerListToDatabase();
            closeConnection();
        }
    }

    public void pushCustomerListToDatabase() throws SQLException{
        String insertCustomer = "INSERT INTO customers (customerName,customerSurname,Age) VALUES (?,?,?)";
        String insertContact = "INSERT INTO contacts (customer_id,contactType,contact) VALUES (?,?,?)";
        PreparedStatement preparedStatement = con.prepareStatement(insertCustomer, Statement.RETURN_GENERATED_KEYS);
        PreparedStatement contactStatement = con.prepareStatement(insertContact);

        for (Customer customer : customerList) {
            preparedStatement.setString(1, customer.getName());
            preparedStatement.setString(2, customer.getSurname());
            preparedStatement.setString(3, customer.getAge());
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        long refKey;

        for (Customer customer : customerList) {
            List<Contact> contactList = customer.getContacts();

            if (generatedKeys.next()) {
                refKey = generatedKeys.getLong(1);

                for (Contact contact : contactList) {
                    contactStatement.setLong(1, refKey);
                    contactStatement.setInt(2, contact.getContactType().getNumVal());
                    contactStatement.setString(3, contact.getContact());
                    contactStatement.addBatch();
                }
                System.out.println(refKey);
            }
        }
        contactStatement.executeBatch();
    }
}
