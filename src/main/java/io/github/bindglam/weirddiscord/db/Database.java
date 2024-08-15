package io.github.bindglam.weirddiscord.db;

import io.github.bindglam.weirddiscord.WeirdDiscord;
import io.github.bindglam.weirddiscord.models.PlayerData;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;

public class Database {
    private Connection connection;

    public Connection getConnection() throws SQLException {
        if(connection != null){
            return connection;
        }

        return createConnection();
    }

    public Connection createConnection() throws SQLException {
        String url = "jdbc:" + WeirdDiscord.INSTANCE.getConfig().getString("database.type") + "://" + WeirdDiscord.INSTANCE.getConfig().getString("database.address") + "/" + WeirdDiscord.INSTANCE.getConfig().getString("database.database");
        connection = DriverManager.getConnection(url, WeirdDiscord.INSTANCE.getConfig().getString("database.user"), WeirdDiscord.INSTANCE.getConfig().getString("database.password"));
        return connection;
    }

    public void initializeDatabase() throws SQLException {
        Statement statement = getConnection().createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS " + WeirdDiscord.INSTANCE.getConfig().getString("database.table_name") + "(uuid varchar(36) primary key, id varchar(30))";
        statement.execute(sql);
        statement.close();
    }

    public PlayerData findPlayerDataByUUID(UUID uuid) throws SQLException {
        PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM " + WeirdDiscord.INSTANCE.getConfig().getString("database.table_name") + " WHERE uuid = ?");
        statement.setString(1, uuid.toString());
        ResultSet results = statement.executeQuery();

        if(results.next()){
            PlayerData playerData = new PlayerData(uuid, results.getString("id"));

            statement.close();

            return playerData;
        }
        statement.close();

        return null;
    }

    public void createPlayerStats(PlayerData data) throws SQLException {
        PreparedStatement statement = getConnection().prepareStatement("INSERT INTO " + WeirdDiscord.INSTANCE.getConfig().getString("database.table_name") + "(uuid, id) VALUES (?, ?)");
        statement.setString(1, data.getUuid().toString());
        statement.setString(2, data.getId());
        statement.executeUpdate();
        statement.close();
    }

    public void updatePlayerStats(PlayerData data) throws SQLException {
        PreparedStatement statement = getConnection().prepareStatement("UPDATE " + WeirdDiscord.INSTANCE.getConfig().getString("database.table_name") + " SET id = ? WHERE uuid = ?");
        statement.setString(1, data.getId());
        statement.setString(2, data.getUuid().toString());
        statement.executeUpdate();
        statement.close();
    }

    public void deletePlayerData(PlayerData data) throws SQLException {
        PreparedStatement statement = getConnection().prepareStatement("DELETE FROM " + WeirdDiscord.INSTANCE.getConfig().getString("database.table_name") + " WHERE uuid = ?");
        statement.setString(1, data.getUuid().toString());
        statement.executeUpdate();
        statement.close();
    }

    public PlayerData getPlayerDataFromDatabase(UUID player) throws SQLException {
        PlayerData data = findPlayerDataByUUID(player);

        if(data == null){
            data = new PlayerData(player, "");
            createPlayerStats(data);
        }

        return data;
    }
}
