package org.hg.energy.Database;

import org.hg.energy.Energy;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SetupDatabase {
    public Connection connection;
    public MeshDatabase meshDatabase;
    private Energy plugin;

    public SetupDatabase(Energy plugin) {
        this.plugin = plugin;
        try {
            File folder = new File(plugin.getDataFolder() + File.separator);
            if (!folder.exists()) {
                folder.mkdir();
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(
                    "jdbc:sqlite:" + plugin.getDataFolder() + File.separator + "database.db");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return;
        }
        try {
            this.meshDatabase = new MeshDatabase(plugin, connection);
        } catch (SQLException | IOException |ClassNotFoundException e){
            throw new RuntimeException(e);
        }
    }
}
