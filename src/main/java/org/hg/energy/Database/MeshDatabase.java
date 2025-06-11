package org.hg.energy.Database;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.hg.energy.Energy;
import org.hg.energy.Mesh;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MeshDatabase {
    private final Energy plugin;
    private final Connection connection;
    private final String tableName = "mesh";

    public MeshDatabase(Energy plugin, Connection connection) throws SQLException, IOException, ClassNotFoundException {
        this.plugin = plugin;
        this.connection = connection;
        setupTable();
        migrateSchemaIfNeeded();
    }

    private static String serialize(Mesh mesh) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             BukkitObjectOutputStream out = new BukkitObjectOutputStream(baos)) {
            out.writeObject(mesh);
            return Base64Coder.encodeLines(baos.toByteArray());
        }
    }

    private static Mesh deserialize(String base64) throws IOException, ClassNotFoundException {
        byte[] data = Base64Coder.decodeLines(base64);
        try (BukkitObjectInputStream in = new BukkitObjectInputStream(
                new ByteArrayInputStream(data))) {
            return (Mesh) in.readObject();
        }
    }

    /**
     * Если внутри Base64 хранилось сразу несколько Mesh (пишем count + объекты)
     */
    @Deprecated
    private static List<Mesh> deserializeMany(String base64)
    throws IOException, ClassNotFoundException {
        byte[] data = Base64Coder.decodeLines(base64);
        List<Mesh> list = new ArrayList<>();
        try (BukkitObjectInputStream in = new BukkitObjectInputStream(
                new ByteArrayInputStream(data))) {
            int cnt = in.readInt();
            for (int i = 0; i < cnt; i++) {
                list.add((Mesh) in.readObject());
            }
        }
        return list;
    }

    /**
     * Проверяет схему и, если нужно, мигрирует из
     * (id INTEGER PK, meshs STRING)
     * в
     * (uuid STRING PK, mesh STRING)
     */
    @Deprecated
    private void migrateSchemaIfNeeded() throws SQLException, IOException, ClassNotFoundException {
        DatabaseMetaData meta = connection.getMetaData();

        // если есть уже колонка uuid — ничего не делаем
        try (ResultSet rs = meta.getColumns(null, null, tableName, "uuid")) {
            if (rs.next()) {
                plugin.getLogger().info("[" + tableName + "] UUID колонка существует, скип миграции.");
                return;
            }
        }

        // есть ли старая колонка id?
        try (ResultSet rs = meta.getColumns(null, null, tableName, "id")) {
            if (!rs.next()) {
                plugin.getLogger().warning(
                        "[" + tableName + "] Нет колонки 'id' и 'uuid' так что миграция скипается.");
                throw new RuntimeException("[" + tableName + "] Нет колонки 'id' и 'uuid'.");
            }
        }

        plugin.getLogger().info("[" + tableName + "] Старт миграции сетей на UUID...");

        boolean oldAuto = connection.getAutoCommit();
        connection.setAutoCommit(false);
        String tmp = tableName + "_tmp";

        try (Statement st = connection.createStatement()) {
            // 1) удалить старый temp, если остался
            st.executeUpdate("DROP TABLE IF EXISTS " + tmp);

            // 2) создать новую таблицу
            st.executeUpdate(
                    "CREATE TABLE " + tmp + " (" +
                            " uuid STRING PRIMARY KEY," +
                            " mesh STRING" +
                            ")"
                            );
        }

        // 3) прочитать данные из старой таблицы
        String selectOld = "SELECT meshs FROM " + tableName;
        List<Mesh> buffer = new ArrayList<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(selectOld)) {

            while (rs.next()) {
                String b64 = rs.getString("meshs");
                buffer.addAll(deserializeMany(b64));
            }
        }

        // 4) записать в новую таблицу батчем
        String insertNew = "INSERT INTO " + tmp + " (uuid, mesh) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertNew)) {
            for (Mesh m : buffer) {
                ps.setString(
                        1, m.getUuid() != null
                                ? m.getUuid().toString()
                                : UUID.randomUUID().toString()
                            );
                ps.setString(2, serialize(m));
                ps.addBatch();
            }
            ps.executeBatch();
        }

        // 5) заменить таблицы
        try (Statement st = connection.createStatement()) {
            st.executeUpdate("DROP TABLE " + tableName);
            st.executeUpdate("ALTER TABLE " + tmp + " RENAME TO " + tableName);
        }

        connection.commit();
        connection.setAutoCommit(oldAuto);

        plugin.getLogger().info("[" + tableName + "] Migration completed.");
    }

    /**
     * Создаёт таблицу, если её вообще не было.
     */
    private void setupTable() {
        String ddl = "CREATE TABLE IF NOT EXISTS " + tableName +
                " (uuid STRING PRIMARY KEY, mesh STRING)";
        try (Statement st = connection.createStatement()) {
            st.executeUpdate(ddl);
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to create table " + tableName, ex);
        }
    }

    // ------------------------------------------------------------------
    // Вспомогательные методы сериализации / десериализации
    // ------------------------------------------------------------------

    /**
     * Сохраняет или обновляет один Mesh.
     */
    public void setMesh(Mesh mesh) {
        String sql = "INSERT OR REPLACE INTO " + tableName + " (uuid, mesh) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, mesh.getUuid().toString());
            ps.setString(2, serialize(mesh));
            ps.executeUpdate();
        } catch (SQLException | IOException ex) {
            throw new RuntimeException("Ошибка сохранения " + mesh.getUuid(), ex);
        }
    }

    /**
     * Удаляет один Mesh по UUID.
     */
    public void deleteMesh(UUID uuid) {
        String sql = "DELETE FROM " + tableName + " WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Ошибка удаления " + uuid, ex);
        }
    }

    /**
     * Возвращает все Mesh из БД.
     */
    public List<Mesh> getAllMeshes() {
        List<Mesh> result = new ArrayList<>();
        String sql = "SELECT mesh, uuid FROM " + tableName;
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Mesh m = deserialize(rs.getString("mesh"));
                result.add(m);
            }
        } catch (SQLException | IOException | ClassNotFoundException ex) {
            throw new RuntimeException("Ошибка чтения сетей", ex);
        }
        return result;
    }
}
