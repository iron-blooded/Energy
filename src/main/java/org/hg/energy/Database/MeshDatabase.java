package org.hg.energy.Database;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.hg.energy.Energy;
import org.hg.energy.Mesh;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MeshDatabase {
    Energy plugin;
    Connection connection;
    String database_name = "mesh";

    public MeshDatabase(Energy plugin, Connection connection) {
        this.plugin = plugin;
        this.connection = connection;
        setup();
    }

    public void setup() {
        try {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + database_name
                                                + " (id INTEGER, meshs STRING, PRIMARY KEY (id))").execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        String sql = "INSERT OR REPLACE INTO " + database_name + " (id, meshs) VALUES (?, ?)";
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(plugin.meshes.size());
            for (Mesh mesh : plugin.meshes) {
                dataOutput.writeObject(mesh);
            }
            dataOutput.close();
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, 0);
            stmt.setString(2, Base64Coder.encodeLines(outputStream.toByteArray()));
            stmt.executeUpdate();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Mesh> getListMesh() {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT meshs FROM " + database_name + " WHERE id = ?");
            ps.setInt(1, 0);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String output = rs.getString("meshs");
                ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(output));
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
                int size = dataInput.readInt();
                List<Mesh> list = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    list.add((Mesh) dataInput.readObject());
                }
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
