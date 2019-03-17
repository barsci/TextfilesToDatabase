package dbconnection;

import model.Contact;
import model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBConnection implements DatabaseOperations{
    private Connection con;
    private static DBConnection dbConnection;
    private List<Customer> customerList = new ArrayList<>();
    private int recordCount = 0;

    private DBConnection() throws SQLException{
        con = DriverManager.getConnection("jdbc:mysql://localhost/from_textfile?serverTimezone=Europe/Warsaw" +
                "&useSSL=False", "user", "password");
    }

    public static DBConnection getInstance() throws SQLException {
        if(dbConnection == null){
            dbConnection = new DBConnection();
        }
        return dbConnection;
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
