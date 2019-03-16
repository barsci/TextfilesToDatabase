package dbconnection;

import model.Contact;
import model.Customer;

import java.sql.*;
import java.util.List;

public class DBConnection {
    private Connection con;
    private static DBConnection dbConnection;

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

    public Connection getConnection() {
        return con;
    }

    public void closeConnection() throws SQLException {
        con.close();
    }

    public void insertCustomerToDatabase(Customer customer) throws SQLException{

        String insertCustomer = "INSERT INTO customers (customerName,customerSurname,Age) VALUES (?,?,?)";
        String insertContact = "INSERT INTO contacts (customer_id,contactType,contact) VALUES (?,?,?)";
        PreparedStatement preparedStatement = con.prepareStatement(insertCustomer, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1,customer.getName());
        preparedStatement.setString(2,customer.getSurname());
        preparedStatement.setString(3,customer.getAge());
        preparedStatement.executeUpdate();

        ResultSet generatedKey = preparedStatement.getGeneratedKeys();
        long refKey = 0;
        if (generatedKey.next()) {
            refKey = generatedKey.getLong(1);
            List<Contact> customerContactList = customer.getContacts();
            for(Contact contact: customerContactList) {
                PreparedStatement contactStatement = con.prepareStatement(insertContact);
                contactStatement.setString(1,String.valueOf(refKey));
                contactStatement.setString(2,String.valueOf(contact.getContactType().getNumVal()));
                contactStatement.setString(3,contact.getContact());
                contactStatement.executeUpdate();
            }
        }
        System.out.println(refKey);
    }
}
