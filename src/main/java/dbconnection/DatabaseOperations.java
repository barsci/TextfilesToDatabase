package dbconnection;

import model.Customer;

import java.sql.SQLException;

public interface DatabaseOperations {
    void write(Customer customer) throws SQLException;
    void eof() throws SQLException;
}
