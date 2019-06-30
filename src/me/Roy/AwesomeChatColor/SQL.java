package me.roy.awcc;

import java.sql.ResultSet;
import java.sql.SQLException;
import static me.Roy.AwesomeChatColor.Main.plugin;

public class SQL {

    public static boolean sql = plugin.getMySQL();

    public static void createNumber(String u) {
        if (sql == true) {
            Main.mysql.update("INSERT INTO AWCC(UUID, COLOR, BOLD, STRIKE, UNLINE, ITALIC) VALUES ('" + u + "', 'NONE', 'false', 'false', 'false', 'false');");
        }
    }

    public static boolean playerExists(String u) {
        if (sql == true) {
            try {
                ResultSet rs = Main.mysql.query("SELECT * FROM AWCC WHERE UUID='" + u + "'");
                if (rs.next()) {
                    return rs.getString("UUID") != null;
                }
                return false;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //----------------
    public static String getColor(String u) {
        String i = "GRAY";
        if (sql == true) {
            try {
                ResultSet rs = Main.mysql.query("SELECT * FROM AWCC WHERE UUID= '" + u + "'");
                if ((!rs.next()) || (rs.getString("COLOR") == null));
                i = rs.getString("COLOR");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    public static void setColor(String u, String color) {
        if (sql == true) {
            Main.mysql.update("UPDATE AWCC SET COLOR= '" + color + "' WHERE UUID= '" + u + "';");
        }
    }

    //---------------------
    public static boolean getBold(String u) {
        boolean i = false;
        if (sql == true) {
            try {
                ResultSet rs = Main.mysql.query("SELECT * FROM AWCC WHERE UUID= '" + u + "'");
                if ((!rs.next()) || (rs.getString("BOLD") == null));
                i = rs.getBoolean("BOLD");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    public static void setBold(String u, String value) {
        if (sql == true) {
            Main.mysql.update("UPDATE AWCC SET BOLD= '" + value + "' WHERE UUID= '" + u + "';");
        }
    }
    
    //---------------------
    public static boolean getStrike(String u) {
        boolean i = false;
        if (sql == true) {
            try {
                ResultSet rs = Main.mysql.query("SELECT * FROM AWCC WHERE UUID= '" + u + "'");
                if ((!rs.next()) || (rs.getString("STRIKE") == null));
                i = rs.getBoolean("STRIKE");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    public static void setStrike(String u, String value) {
        if (sql == true) {
            Main.mysql.update("UPDATE AWCC SET STRIKE= '" + value + "' WHERE UUID= '" + u + "';");
        }
    }
    
    //---------------------
    public static boolean getUnline(String u) {
        boolean i = false;
        if (sql == true) {
            try {
                ResultSet rs = Main.mysql.query("SELECT * FROM AWCC WHERE UUID= '" + u + "'");
                if ((!rs.next()) || (rs.getString("UNLINE") == null));
                i = rs.getBoolean("UNLINE");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    public static void setUnline(String u, String value) {
        if (sql == true) {
            Main.mysql.update("UPDATE AWCC SET UNLINE= '" + value + "' WHERE UUID= '" + u + "';");
        }
    }
    
    //---------------------
    public static boolean getItalic(String u) {
        boolean i = false;
        if (sql == true) {
            try {
                ResultSet rs = Main.mysql.query("SELECT * FROM AWCC WHERE UUID= '" + u + "'");
                if ((!rs.next()) || (rs.getString("ITALIC") == null));
                i = rs.getBoolean("ITALIC");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    public static void setItalic(String u, String value) {
        if (sql == true) {
            Main.mysql.update("UPDATE AWCC SET ITALIC= '" + value + "' WHERE UUID= '" + u + "';");
        }
    }
}
