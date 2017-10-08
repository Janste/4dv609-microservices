package application.account;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
        try
        {
            connection = DriverManager.getConnection("this is an url to mysql database");
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public boolean saveUser(User user) {

        try {
            String sql = "INSERT INTO USER VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement ps = connection.prepareStatement(sql);
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


}
