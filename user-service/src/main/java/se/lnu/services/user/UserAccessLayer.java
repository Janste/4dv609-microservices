package se.lnu.services.user;

import java.sql.*;

import se.lnu.service.common.message.User;

public class UserAccessLayer
{
    private Connection connection = null;
    private static UserAccessLayer instance = null;

    public static UserAccessLayer getInstance() {
        if (instance == null) {
            instance = new UserAccessLayer();
        }
        return instance;
    }

    private UserAccessLayer() {

        String url = "jdbc:mysql://localhost:3306/user_service";
        String username = "root";
        String password = "my_password";

        try
        {
            connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(false);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public boolean registerUser(User user) {

        try {
            String query = "INSERT INTO USERS VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, 0);
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getSecondName());
            ps.setString(4, user.getStreetAddress());
            ps.setString(5, user.getCity());
            ps.setString(6, user.getState());
            ps.setString(7, user.getZipCode());
            ps.setString(8, user.getCountry());
            ps.setString(9, user.getTelephone());
            ps.setString(10, user.getEmail());
            ps.setString(11, user.getPassword());
            ps.executeUpdate();
            ps.close();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public User loginUser(String email, String password) {

        User user = null;

        try {
            String query = "SELECT * FROM USERS WHERE email = ?;";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                String passwordInDatabase = result.getString("password");
                if (passwordInDatabase.equals(password)) {
                    user = new User();
                    user.setFirstName(result.getString("firstName"));
                    user.setSecondName(result.getString("secondName"));
                    user.setStreetAddress(result.getString("streetAddress"));
                    user.setCity(result.getString("city"));
                    user.setState(result.getString("state"));
                    user.setZipCode(result.getString("zipCode"));
                    user.setCountry(result.getString("country"));
                    user.setTelephone(result.getString("telephone"));
                    user.setEmail(result.getString("email"));
                    user.setPassword(result.getString("password"));
                }
            }
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    public boolean changeUser(User user) {

        try {
            String query = "UPDATE Users " +
                    "SET " +
                        "FirstName = ?, " +
                        "SecondName = ?, " +
                        "StreetAddress = ?, " +
                        "City = ?, " +
                        "State = ?, " +
                        "ZipCode = ?, " +
                        "Country = ?, " +
                        "Telephone =?, " +
                        "Email = ?, " +
                        "Password = ? " +
                    "WHERE Email = ?;";

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getSecondName());
            ps.setString(3, user.getStreetAddress());
            ps.setString(4, user.getCity());
            ps.setString(5, user.getState());
            ps.setString(6, user.getZipCode());
            ps.setString(7, user.getCountry());
            ps.setString(8, user.getTelephone());
            ps.setString(9, user.getEmail());
            ps.setString(10, user.getPassword());
            ps.setString(11, user.getEmail());
            ps.executeUpdate();
            ps.close();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    
    public User getUserByEmail(String email) {
    	
        User user = null;

        try {
            String query = "SELECT * FROM USERS WHERE email = ?;";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                user = new User();
                user.setFirstName(result.getString("firstName"));
                user.setSecondName(result.getString("secondName"));
                user.setStreetAddress(result.getString("streetAddress"));
                user.setCity(result.getString("city"));
                user.setState(result.getString("state"));
                user.setZipCode(result.getString("zipCode"));
                user.setCountry(result.getString("country"));
                user.setTelephone(result.getString("telephone"));
                user.setEmail(result.getString("email"));
                user.setPassword(result.getString("password"));
            }
            result.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

}

