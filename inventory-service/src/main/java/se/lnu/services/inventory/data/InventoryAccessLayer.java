package se.lnu.services.inventory.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import se.lnu.service.common.animals.Pet;

public class InventoryAccessLayer
{
    private Connection connection = null;
    private static InventoryAccessLayer instance = null;

    public static InventoryAccessLayer getInstance() {
        if (instance == null) {
            instance = new InventoryAccessLayer();
        }
        return instance;
    }

    private InventoryAccessLayer() {
        try
        {
            connection = DriverManager.getConnection("this is an url to mysql database");
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public boolean removePet(Pet pet) {

        try { //TODO
            String sql = "INSERT INTO USER VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.executeUpdate();
            ps.close();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    
    //TODO


}
