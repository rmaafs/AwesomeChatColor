package me.roy.awcc;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import static me.Roy.AwesomeChatColor.Main.plugin;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MySQL {
    
    public File config = plugin.getcConfig();
    public FileConfiguration cconfig = YamlConfiguration.loadConfiguration(config);

    private String HOST = "";
    private String PORT = "";
    private String DATABASE = "";
    private String USER = "";
    private String PASSWORD = "";

    private Connection con;

    public MySQL() {
        this.HOST = cconfig.getString("mysql.ip/host");
        this.PORT = cconfig.getString("mysql.port");
        this.DATABASE = cconfig.getString("mysql.database");
        this.USER = cconfig.getString("mysql.username");
        this.PASSWORD = cconfig.getString("mysql.password");
        connect();
    }

    public void connect() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE, USER, PASSWORD);
            getServer().getConsoleSender().sendMessage("§aAwesomeChatColor, Successfully connected to database.");
        } catch (SQLException e) {
            getServer().getConsoleSender().sendMessage("§aAwesomeChatColor, §cERROR MYSQL: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (con != null) {
                con.close();
                getServer().getConsoleSender().sendMessage("§aAwesomeChatColor, database closed");
            }
        } catch (SQLException e) {
            getServer().getConsoleSender().sendMessage("§aAwesomeChatColor, §cERROR MYSQL: " + e.getMessage());
        }
    }

    public void update(String qry) {
        try {
            Statement st = con.createStatement();
            st.executeUpdate(qry);
            st.close();
        } catch (SQLException e) {
            connect();
            System.err.println(e);
        }
    }

    public ResultSet query(String qry) {
        ResultSet rs = null;

        try {
            Statement st = con.createStatement();
            rs = st.executeQuery(qry);
        } catch (SQLException e) {
            connect();
            System.err.println(e);
        }
        return rs;
    }
}
