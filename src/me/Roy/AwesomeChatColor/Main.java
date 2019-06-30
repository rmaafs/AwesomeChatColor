package me.roy.awcc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    public static Main plugin;
    public static MySQL mysql;

    boolean sql;

    File config = new File(getDataFolder() + File.separator + "config.yml");
    File data = new File(getDataFolder() + File.separator + "data.yml");
    FileConfiguration cdata = YamlConfiguration.loadConfiguration(data);

    String needcolor, noneclear, firstselect, permission, colorerror, offline, help, usage, listmessage, cleared, formatchanged, errorrainbow, prefix, successfull, mensa;
    int type;
    Boolean sounds;
    Inventory inve;
    ItemStack dred, black, dblue, blue, cyan, gray, green, lime, magenta, orange, pink, red, silver, white, yellow, bold, strike, unline, italic, clear, rainbow;

    int sdred, sblack, sdblue, sblue, scyan, sgray, sgreen, slime, smagenta, sorange, spink, sred, ssilver, swhite, syellow, sbold, sstrike, sunline, sitalic, srainbow, sclear;

    String NOTE_PIANO, NOTE_PLING, EXPLODE, FUSE;

    HashMap<String, String> color = new HashMap<>();
    List<String> teambold, teamstrike, teamunline, teamitalic, teamrainbow;

    @Override
    public void onDisable() {
        cdata.set("bold", teambold);
        cdata.set("strike", teamstrike);
        cdata.set("unline", teamunline);
        cdata.set("italic", teamitalic);
        cdata.set("rainbow", teamrainbow);
        guardar(data, cdata);
        plugin = null;
    }

    @Override
    public void onEnable() {
        if (!config.exists()) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
        if (!data.exists()) {
            copy(getResource("data.yml"), new File(getDataFolder(), "data.yml"));
        }
        plugin = this;
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "----------------------------");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "AwesomeChatColor by " + ChatColor.DARK_RED + "Royendero");
        getServer().getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "----------------------------");
        idioma();
        if (Bukkit.getVersion().contains("1.7") || Bukkit.getVersion().contains("1.8")) {
            NOTE_PIANO = "NOTE_PIANO";
            NOTE_PLING = "NOTE_PLING";
            EXPLODE = "EXPLODE";
            FUSE = "FUSE";
        }
        if (Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11")) {
            NOTE_PIANO = "BLOCK_NOTE_HAT";
            NOTE_PLING = "BLOCK_NOTE_PLING";
            EXPLODE = "ENTITY_FIREWORK_SHOOT";
            FUSE = "ENTITY_CREEPER_PRIMED";
        }
        if (sql == false) {
            if (!(Bukkit.getOnlinePlayers().size() == 0)) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (cdata.contains(p.getUniqueId().toString())) {
                        color.put(p.getName(), cdata.getString(p.getUniqueId().toString()));
                    }
                }
            }
        } else {
            ConnectMySQL();
            if (!(Bukkit.getOnlinePlayers().size() == 0)) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!SQL.playerExists(p.getUniqueId().toString())) {
                        SQL.createNumber(p.getUniqueId().toString());
                    }
                    String col = SQL.getColor(p.getUniqueId().toString());
                    if (!col.equals("RAINBOW")) {
                        if (!col.equals("NONE")) {
                            color.put(p.getName(), col);
                        }
                        if (SQL.getBold(p.getUniqueId().toString())) {
                            if (!teambold.contains(p.getName())) {
                                teambold.add(p.getName());
                            }
                        }
                        if (SQL.getStrike(p.getUniqueId().toString())) {
                            if (!teamstrike.contains(p.getName())) {
                                teamstrike.add(p.getName());
                            }
                        }
                        if (SQL.getUnline(p.getUniqueId().toString())) {
                            if (!teamunline.contains(p.getName())) {
                                teamunline.add(p.getName());
                            }
                        }
                        if (SQL.getItalic(p.getUniqueId().toString())) {
                            if (!teamitalic.contains(p.getName())) {
                                teamitalic.add(p.getName());
                            }
                        }
                    } else {
                        if (!teamrainbow.contains(p.getName())) {
                            teamrainbow.add(p.getName());
                        }
                    }

                }
            }
        }
    }

    private void ConnectMySQL() {
        mysql = new MySQL();
        mysql.update("CREATE TABLE IF NOT EXISTS AWCC(UUID VARCHAR(64), COLOR VARCHAR(20), BOLD VARCHAR(10), STRIKE VARCHAR(10), UNLINE VARCHAR(10), ITALIC VARCHAR(10));");
    }

    public static void guardar(File file, FileConfiguration fc) {
        try {
            fc.save(file);
        } catch (IOException ex) {
            Logger.getLogger(Main.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte['?'];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String tcc(String m) {
        String M = "";
        for (int i = 0; i < m.length(); i++) {
            if (m.charAt(i) == '&') {
                M += '§';
            } else {
                M += m.charAt(i);
            }
        }
        return M;
    }

    public List<String> tCC(List<String> list) {
        List<String> finalList = new ArrayList();
        int size = list.size();
        for (int index = 0; index < size; index++) {
            String string = ChatColor.translateAlternateColorCodes('&', (String) list.get(index));
            finalList.add(string);
        }
        return finalList;
    }

    public ItemStack crear(Material m, String d, List<String> l) {
        if (d != null) {
            ItemStack item = new ItemStack(m);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', d));
            List<String> Lore = new ArrayList();
            Lore.addAll(tCC(l));
            meta.setLore(Lore);
            item.setItemMeta(meta);
            return item;
        }
        return null;
    }

    public ItemStack crearTraje(Color color, String displayname, List<String> lore) {
        if (displayname != null) {
            ItemStack lchest = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
            LeatherArmorMeta lch = (LeatherArmorMeta) lchest.getItemMeta();
            lch.setColor(color);
            lch.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayname));
            List<String> Lore = new ArrayList();
            Lore.addAll(tCC(lore));
            lch.setLore(Lore);
            lchest.setItemMeta(lch);
            return lchest;
        }
        return null;
    }

    public ItemStack createItem(DyeColor dye, String displayname, List<String> lore) {
        if (displayname != null) {
            if (type == 1) {
                ItemStack item = new ItemStack(Material.LEGACY_WOOL, 1, dye.getWoolData());
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayname));
                List<String> Lore = new ArrayList();
                Lore.addAll(tCC(lore));
                meta.setLore(Lore);
                item.setItemMeta(meta);
                return item;
            }
            if (type == 2) {
                ItemStack item = new ItemStack(Material.STAINED_GLASS, 1, dye.getWoolData());
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayname));
                List<String> Lore = new ArrayList();
                Lore.addAll(tCC(lore));
                meta.setLore(Lore);
                item.setItemMeta(meta);
                return item;
            }
            if (type == 3) {
                ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, dye.getWoolData());
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayname));
                List<String> Lore = new ArrayList();
                Lore.addAll(tCC(lore));
                meta.setLore(Lore);
                item.setItemMeta(meta);
                return item;
            }
            if (type == 4) {
                ItemStack item = new ItemStack(Material.STAINED_CLAY, 1, dye.getWoolData());
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayname));
                List<String> Lore = new ArrayList();
                Lore.addAll(tCC(lore));
                meta.setLore(Lore);
                item.setItemMeta(meta);
                return item;
            }
            if (type == 5) {
                ItemStack item = new ItemStack(Material.CARPET, 1, dye.getWoolData());
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayname));
                List<String> Lore = new ArrayList();
                Lore.addAll(tCC(lore));
                meta.setLore(Lore);
                item.setItemMeta(meta);
                return item;
            }
            if (type == 0 || type >= 7) {
                type = 1;
                Bukkit.broadcastMessage(ChatColor.RED + "AWCC ERROR IN config.yml (Error in type:)");
                ItemStack item = new ItemStack(Material.WOOL, 1, dye.getWoolData());
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayname));
                List<String> Lore = new ArrayList();
                Lore.addAll(tCC(lore));
                meta.setLore(Lore);
                item.setItemMeta(meta);
                return item;
            }
        }
        return null;
    }

    public String pon(String jugador) {
        if (teambold.contains(jugador)) {
            teambold.remove(jugador);
            if (sql == true) {
                SQL.setBold(Bukkit.getPlayer(jugador).getUniqueId().toString(), "false");
            }
        }
        if (teamstrike.contains(jugador)) {
            teamstrike.remove(jugador);
            if (sql == true) {
                SQL.setStrike(Bukkit.getPlayer(jugador).getUniqueId().toString(), "false");
            }
        }
        if (teamunline.contains(jugador)) {
            teamunline.remove(jugador);
            if (sql == true) {
                SQL.setUnline(Bukkit.getPlayer(jugador).getUniqueId().toString(), "false");
            }
        }
        if (teamitalic.contains(jugador)) {
            teamitalic.remove(jugador);
            if (sql == true) {
                SQL.setItalic(Bukkit.getPlayer(jugador).getUniqueId().toString(), "false");
            }
        }
        if (color.containsKey(jugador)) {
            color.remove(jugador);
            if (sql == true) {
                SQL.setColor(Bukkit.getPlayer(jugador).getUniqueId().toString(), "NONE");
            }
        }
        teamrainbow.add(jugador);
        return jugador;
    }

    public String quitar(String jugador) {
        if (teambold.contains(jugador)) {
            teambold.remove(jugador);
            if (sql == true) {
                SQL.setBold(Bukkit.getPlayer(jugador).getUniqueId().toString(), "false");
            }
        }
        if (teamstrike.contains(jugador)) {
            teamstrike.remove(jugador);
            if (sql == true) {
                SQL.setStrike(Bukkit.getPlayer(jugador).getUniqueId().toString(), "false");
            }
        }
        if (teamunline.contains(jugador)) {
            teamunline.remove(jugador);
            if (sql == true) {
                SQL.setUnline(Bukkit.getPlayer(jugador).getUniqueId().toString(), "false");
            }
        }
        if (teamitalic.contains(jugador)) {
            teamitalic.remove(jugador);
            if (sql == true) {
                SQL.setItalic(Bukkit.getPlayer(jugador).getUniqueId().toString(), "false");
            }
        }
        if (color.containsKey(jugador)) {
            color.remove(jugador);
            if (sql == true) {
                SQL.setColor(Bukkit.getPlayer(jugador).getUniqueId().toString(), "GRAY");
            }
        }
        if (teamrainbow.contains(jugador)) {
            teamrainbow.remove(jugador);
            if (sql == true) {
                SQL.setColor(Bukkit.getPlayer(jugador).getUniqueId().toString(), "GRAY");
            }
        }
        return jugador;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        if (p.getName().equals("Royendero") || p.getName().equals("JoliJafa")) {
            p.sendMessage("§aEl servidor está corriendo tu plugin de AwesomeChatColor. Version: 3.0");
            p.sendMessage("§aSQL: " + sql);
        }
        if (sql == false) {
            if (cdata.contains(p.getUniqueId().toString())) {
                color.put(p.getName(), cdata.getString(p.getUniqueId().toString()));
            }
        } else {
            if (!SQL.playerExists(p.getUniqueId().toString())) {
                SQL.createNumber(p.getUniqueId().toString());
            }
            String col = SQL.getColor(p.getUniqueId().toString());
            if (!col.equals("RAINBOW")) {
                if (!col.equals("NONE")) {
                    color.put(p.getName(), col);
                }
                if (SQL.getBold(p.getUniqueId().toString())) {
                    if (!teambold.contains(p.getName())) {
                        teambold.add(p.getName());
                    }
                }
                if (SQL.getStrike(p.getUniqueId().toString())) {
                    if (!teamstrike.contains(p.getName())) {
                        teamstrike.add(p.getName());
                    }
                }
                if (SQL.getUnline(p.getUniqueId().toString())) {
                    if (!teamunline.contains(p.getName())) {
                        teamunline.add(p.getName());
                    }
                }
                if (SQL.getItalic(p.getUniqueId().toString())) {
                    if (!teamitalic.contains(p.getName())) {
                        teamitalic.add(p.getName());
                    }
                }
            } else {
                if (!teamrainbow.contains(p.getName())) {
                    teamrainbow.add(p.getName());
                }
            }

        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("awcc")) {
            if (!(sender instanceof Player) && (args.length == 0)) {
                sender.sendMessage(ChatColor.DARK_GREEN + "[AwesomeChatColor]" + ChatColor.RED + " Command only for players.");
                return true;
            }
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 0) {
                    if (player.hasPermission("awcc.gui")) {
                        if (args.length == 0) {
                            abrir(player);
                            if (sounds == true) {
                                player.playSound(player.getLocation(), Sound.valueOf(NOTE_PIANO), 1, -3);
                            }
                            return true;
                        }
                    } else {
                        sender.sendMessage(prefix + permission);
                    }
                }
            }
            if (args.length == 1) {
                if (sender.hasPermission("awcc.list") || sender.hasPermission("awcc.help") || sender.hasPermission("awcc.reload")) {
                    if (args[0].equalsIgnoreCase("list")) {
                        if (sender.hasPermission("awcc.list")) {
                            sender.sendMessage(prefix + ChatColor.RED + listmessage);
                            sender.sendMessage(ChatColor.DARK_RED + "dark-red " + ChatColor.BLACK + "black " + ChatColor.DARK_BLUE + "dark-blue " + ChatColor.BLUE + "blue " + ChatColor.AQUA + "cyan " + ChatColor.GRAY + "gray " + ChatColor.DARK_GREEN + "green " + ChatColor.GREEN + "lime " + ChatColor.DARK_PURPLE + "magenta " + ChatColor.GOLD + "orange " + ChatColor.LIGHT_PURPLE + "pink " + ChatColor.RED + "red " + ChatColor.DARK_GRAY + "silver " + ChatColor.WHITE + "white " + ChatColor.YELLOW + "yellow " + "§fr§ba§ci§2n§6b§9o§5w§r " + ChatColor.BOLD + "BOLD " + "§r " + ChatColor.STRIKETHROUGH + "STRIKE" + "§r " + ChatColor.UNDERLINE + "UNDERLINE" + "§r " + ChatColor.ITALIC + "ITALIC");
                        } else {
                            sender.sendMessage(prefix + permission);
                        }
                    } else if (args[0].equalsIgnoreCase("help")) {
                        if (sender.hasPermission("awcc.help")) {
                            sender.sendMessage(prefix + ChatColor.RED + help);
                            sender.sendMessage(ChatColor.DARK_GREEN + "/awcc §aOpen the GUI");
                            sender.sendMessage(ChatColor.DARK_GREEN + "/awcc list §aSee color list");
                            sender.sendMessage(ChatColor.DARK_GREEN + "/awcc (Player) (Color) §aChange the color of a player.");
                            sender.sendMessage(ChatColor.DARK_GREEN + "/awcc (Player) clear §aRemove all to the player.");
                            sender.sendMessage(ChatColor.DARK_GREEN + "/awcc reload §aReload config.");
                            sender.sendMessage(ChatColor.DARK_GREEN + "/awcc help §aSee commands.");
                        } else {
                            sender.sendMessage(prefix + permission);
                        }
                    } else if (args[0].equalsIgnoreCase("reload")) {
                        if (sender.hasPermission("awcc.reload")) {
                            File config = new File(getDataFolder() + File.separator + "config.yml");
                            if (!config.exists()) {
                                getConfig().options().copyDefaults(true);
                                saveDefaultConfig();
                            }
                            reloadConfig();
                            idioma();
                            if (sql == true) {
                                ConnectMySQL();
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    if (!SQL.playerExists(p.getUniqueId().toString())) {
                                        SQL.createNumber(p.getUniqueId().toString());
                                    }
                                    String col = SQL.getColor(p.getUniqueId().toString());
                                    if (!col.equals("RAINBOW")) {
                                        if (!col.equals("NONE")) {
                                            color.put(p.getName(), col);
                                        }
                                        if (SQL.getBold(p.getUniqueId().toString())) {
                                            if (!teambold.contains(p.getName())) {
                                                teambold.add(p.getName());
                                            }
                                        }
                                        if (SQL.getStrike(p.getUniqueId().toString())) {
                                            if (!teamstrike.contains(p.getName())) {
                                                teamstrike.add(p.getName());
                                            }
                                        }
                                        if (SQL.getUnline(p.getUniqueId().toString())) {
                                            if (!teamunline.contains(p.getName())) {
                                                teamunline.add(p.getName());
                                            }
                                        }
                                        if (SQL.getItalic(p.getUniqueId().toString())) {
                                            if (!teamitalic.contains(p.getName())) {
                                                teamitalic.add(p.getName());
                                            }
                                        }
                                    } else {
                                        if (!teamrainbow.contains(p.getName())) {
                                            teamrainbow.add(p.getName());
                                        }
                                    }

                                }
                            }
                            getServer().getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "[AwesomeChatColor]" + ChatColor.GREEN + mensa);
                            sender.sendMessage(prefix + ChatColor.RED + mensa);
                            return true;
                        } else {
                            sender.sendMessage(prefix + permission);
                        }
                    } else {
                        sender.sendMessage(prefix + usage);
                    }
                } else {
                    sender.sendMessage(prefix + permission);
                }
            }
            if (args.length == 2) {
                Player target = Bukkit.getServer().getPlayer(args[0]);
                if ((target == null) && (sender.hasPermission("awcc.set.dred") || sender.hasPermission("awcc.set.black") || sender.hasPermission("awcc.set.dblue") || sender.hasPermission("awcc.set.blue") || sender.hasPermission("awcc.set.cyan") || sender.hasPermission("awcc.set.gray") || sender.hasPermission("awcc.set.green") || sender.hasPermission("awcc.set.lime") || sender.hasPermission("awcc.set.magenta") || sender.hasPermission("awcc.set.orange") || sender.hasPermission("awcc.set.pink") || sender.hasPermission("awcc.set.red") || sender.hasPermission("awcc.set.silver") || sender.hasPermission("awcc.set.white") || sender.hasPermission("awcc.set.yellow") || sender.hasPermission("awcc.set.rainbow") || sender.hasPermission("awcc.set.bold") || sender.hasPermission("awcc.set.strike") || sender.hasPermission("awcc.set.unline") || sender.hasPermission("awcc.set.italic") || sender.hasPermission("awcc.set.clear"))) {
                    sender.sendMessage(prefix + offline);
                } else {
                    if (sender.hasPermission("awcc.set.dred") || sender.hasPermission("awcc.set.black") || sender.hasPermission("awcc.set.dblue") || sender.hasPermission("awcc.set.blue") || sender.hasPermission("awcc.set.cyan") || sender.hasPermission("awcc.set.gray") || sender.hasPermission("awcc.set.green") || sender.hasPermission("awcc.set.lime") || sender.hasPermission("awcc.set.magenta") || sender.hasPermission("awcc.set.orange") || sender.hasPermission("awcc.set.pink") || sender.hasPermission("awcc.set.red") || sender.hasPermission("awcc.set.silver") || sender.hasPermission("awcc.set.white") || sender.hasPermission("awcc.set.yellow") || sender.hasPermission("awcc.set.rainbow") || sender.hasPermission("awcc.set.bold") || sender.hasPermission("awcc.set.strike") || sender.hasPermission("awcc.set.unline") || sender.hasPermission("awcc.set.italic") || sender.hasPermission("awcc.set.clear")) {
                        if (args[1].equalsIgnoreCase("dark-red")) {
                            if (sender.hasPermission("awcc.set.dred")) {
                                color.put(target.getName(), "DARK_RED");
                                sender.sendMessage(prefix + ChatColor.GREEN + successfull);
                                if (!(sender == target)) {
                                    target.sendMessage(prefix + ChatColor.GREEN + successfull);
                                }
                                if (sounds == true) {
                                    target.playSound(target.getLocation(), Sound.valueOf(NOTE_PIANO), 1, -3);
                                }
                            } else {
                                sender.sendMessage(prefix + permission);
                            }
                        } else if (args[1].equalsIgnoreCase("black")) {
                            if (sender.hasPermission("awcc.set.black")) {
                                color.put(target.getName(), "BLACK");
                                sender.sendMessage(prefix + ChatColor.GREEN + successfull);
                                if (!(sender == target)) {
                                    target.sendMessage(prefix + ChatColor.GREEN + successfull);
                                }
                                if (sounds == true) {
                                    target.playSound(target.getLocation(), Sound.valueOf(NOTE_PIANO), 1, -3);
                                }
                            } else {
                                sender.sendMessage(prefix + permission);
                            }
                        } else if (args[1].equalsIgnoreCase("dark-blue")) {
                            if (sender.hasPermission("awcc.set.dblue")) {
                                color.put(target.getName(), "DARK_BLUE");
                                sender.sendMessage(prefix + ChatColor.GREEN + successfull);
                                if (!(sender == target)) {
                                    target.sendMessage(prefix + ChatColor.GREEN + successfull);
                                }
                                if (sounds == true) {
                                    target.playSound(target.getLocation(), Sound.valueOf(NOTE_PIANO), 1, -3);
                                }
                            } else {
                                sender.sendMessage(prefix + permission);
                            }
                        } else if (args[1].equalsIgnoreCase("blue")) {
                            if (sender.hasPermission("awcc.set.blue")) {
                                color.put(target.getName(), "BLUE");
                                sender.sendMessage(prefix + ChatColor.GREEN + successfull);
                                if (!(sender == target)) {
                                    target.sendMessage(prefix + ChatColor.GREEN + successfull);
                                }
                                if (sounds == true) {
                                    target.playSound(target.getLocation(), Sound.valueOf(NOTE_PIANO), 1, -3);
                                }
                            } else {
                                sender.sendMessage(prefix + permission);
                            }
                        } else if (args[1].equalsIgnoreCase("cyan")) {
                            if (sender.hasPermission("awcc.set.cyan")) {
                                color.put(target.getName(), "AQUA");
                                sender.sendMessage(prefix + ChatColor.GREEN + successfull);
                                target.sendMessage(prefix + ChatColor.GREEN + successfull);
                                if (sounds == true) {
                                    target.playSound(target.getLocation(), Sound.valueOf(NOTE_PIANO), 1, -3);
                                }
                            } else {
                                sender.sendMessage(prefix + permission);
                            }
                        } else if (args[1].equalsIgnoreCase("gray")) {
                            if (sender.hasPermission("awcc.set.gray")) {
                                color.put(target.getName(), "GRAY");
                                sender.sendMessage(prefix + ChatColor.GREEN + successfull);
                                if (!(sender == target)) {
                                    target.sendMessage(prefix + ChatColor.GREEN + successfull);
                                }
                                if (sounds == true) {
                                    target.playSound(target.getLocation(), Sound.valueOf(NOTE_PIANO), 1, -3);
                                }
                            } else {
                                sender.sendMessage(prefix + permission);
                            }
                        } else if (args[1].equalsIgnoreCase("green")) {
                            if (sender.hasPermission("awcc.set.green")) {
                                color.put(target.getName(), "GREEN");
                                sender.sendMessage(prefix + ChatColor.GREEN + successfull);
                                if (!(sender == target)) {
                                    target.sendMessage(prefix + ChatColor.GREEN + successfull);
                                }
                                if (sounds == true) {
                                    target.playSound(target.getLocation(), Sound.valueOf(NOTE_PIANO), 1, -3);
                                }
                            } else {
                                sender.sendMessage(prefix + permission);
                            }
                        } else if (args[1].equalsIgnoreCase("lime")) {
                            if (sender.hasPermission("awcc.set.lime")) {
                                color.put(target.getName(), "GREEN");
                                sender.sendMessage(prefix + ChatColor.GREEN + successfull);
                                if (!(sender == target)) {
                                    target.sendMessage(prefix + ChatColor.GREEN + successfull);
                                }
                                if (sounds == true) {
                                    target.playSound(target.getLocation(), Sound.valueOf(NOTE_PIANO), 1, -3);
                                }
                            } else {
                                sender.sendMessage(prefix + permission);
                            }
                        } else if (args[1].equalsIgnoreCase("magenta")) {
                            if (sender.hasPermission("awcc.set.magenta")) {
                                color.put(target.getName(), "DARK_PURPLE");
                                sender.sendMessage(prefix + ChatColor.GREEN + successfull);
                                if (!(sender == target)) {
                                    target.sendMessage(prefix + ChatColor.GREEN + successfull);
                                }
                                if (sounds == true) {
                                    target.playSound(target.getLocation(), Sound.valueOf(NOTE_PIANO), 1, -3);
                                }
                            } else {
                                sender.sendMessage(prefix + permission);
                            }
                        } else if (args[1].equalsIgnoreCase("orange")) {
                            if (sender.hasPermission("awcc.set.orange")) {
                                color.put(target.getName(), "GOLD");
                                sender.sendMessage(prefix + ChatColor.GREEN + successfull);
                                if (!(sender == target)) {
                                    target.sendMessage(prefix + ChatColor.GREEN + successfull);
                                }
                                if (sounds == true) {
                                    target.playSound(target.getLocation(), Sound.valueOf(NOTE_PIANO), 1, -3);
                                }
                            } else {
                                sender.sendMessage(prefix + permission);
                            }
                        } else if (args[1].equalsIgnoreCase("pink")) {
                            if (sender.hasPermission("awcc.set.pink")) {
                                color.put(target.getName(), "LIGHT_PURPLE");
                                sender.sendMessage(prefix + ChatColor.GREEN + successfull);
                                if (!(sender == target)) {
                                    target.sendMessage(prefix + ChatColor.GREEN + successfull);
                                }
                                if (sounds == true) {
                                    target.playSound(target.getLocation(), Sound.valueOf(NOTE_PIANO), 1, -3);
                                }
                            } else {
                                sender.sendMessage(prefix + permission);
                            }
                        } else if (args[1].equalsIgnoreCase("red")) {
                            if (sender.hasPermission("awcc.set.red")) {
                                color.put(target.getName(), "RED");
                                sender.sendMessage(prefix + ChatColor.GREEN + successfull);
                                if (!(sender == target)) {
                                    target.sendMessage(prefix + ChatColor.GREEN + successfull);
                                }
                                if (sounds == true) {
                                    target.playSound(target.getLocation(), Sound.valueOf(NOTE_PIANO), 1, -3);
                                }
                            } else {
                                sender.sendMessage(prefix + permission);
                            }
                        } else if (args[1].equalsIgnoreCase("silver")) {
                            if (sender.hasPermission("awcc.set.silver")) {
                                color.put(target.getName(), "DARK_RED");
                                sender.sendMessage(prefix + ChatColor.GREEN + successfull);
                                if (!(sender == target)) {
                                    target.sendMessage(prefix + ChatColor.GREEN + successfull);
                                }
                                if (sounds == true) {
                                    target.playSound(target.getLocation(), Sound.valueOf(NOTE_PIANO), 1, -3);
                                }
                            } else {
                                sender.sendMessage(prefix + permission);
                            }
                        } else if (args[1].equalsIgnoreCase("white")) {
                            if (sender.hasPermission("awcc.set.white")) {
                                color.put(target.getName(), "WHITE");
                                sender.sendMessage(prefix + ChatColor.GREEN + successfull);
                                if (!(sender == target)) {
                                    target.sendMessage(prefix + ChatColor.GREEN + successfull);
                                }
                                if (sounds == true) {
                                    target.playSound(target.getLocation(), Sound.valueOf(NOTE_PIANO), 1, -3);
                                }
                            } else {
                                sender.sendMessage(prefix + permission);
                            }
                        } else if (args[1].equalsIgnoreCase("yellow")) {
                            if (sender.hasPermission("awcc.set.yellow")) {
                                color.put(target.getName(), "YELLOW");
                                sender.sendMessage(prefix + ChatColor.GREEN + successfull);
                                if (!(sender == target)) {
                                    target.sendMessage(prefix + ChatColor.GREEN + successfull);
                                }
                                if (sounds == true) {
                                    target.playSound(target.getLocation(), Sound.valueOf(NOTE_PIANO), 1, -3);
                                }
                            } else {
                                sender.sendMessage(prefix + permission);
                            }
                        } else if (args[1].equalsIgnoreCase("rainbow")) {
                            if (sender.hasPermission("awcc.set.rainbow")) {
                                pon(target.getName());
                                if (!(sender == target)) {
                                    target.sendMessage(prefix + ChatColor.GREEN + successfull);
                                }
                                sender.sendMessage(prefix + ChatColor.GREEN + successfull);
                                if (sounds == true) {
                                    target.playSound(target.getLocation(), Sound.valueOf(FUSE), 1, 1);
                                }
                            } else {
                                sender.sendMessage(prefix + permission);
                            }
                        } else if (args[1].equalsIgnoreCase("bold")) {
                            if (!color.containsKey(sender.getName()) && !teamrainbow.contains(target.getName())) {
                                sender.sendMessage(prefix + needcolor);
                            } else {
                                if (sender.hasPermission("awcc.set.bold")) {
                                    if (!teamrainbow.contains(target.getName())) {
                                        teambold.add(target.getName());
                                        sender.sendMessage(prefix + ChatColor.GREEN + formatchanged);
                                        if (!(sender == target)) {
                                            target.sendMessage(prefix + ChatColor.GREEN + formatchanged);
                                        }
                                        if (sounds == true) {
                                            target.playSound(target.getLocation(), Sound.valueOf(NOTE_PLING), 1, 2);
                                        }
                                    } else {
                                        sender.sendMessage(prefix + ChatColor.RED + errorrainbow);
                                    }
                                } else {
                                    sender.sendMessage(prefix + permission);
                                }
                            }
                        } else if (args[1].equalsIgnoreCase("strike")) {
                            if (!color.containsKey(sender.getName()) && !teamrainbow.contains(target.getName())) {
                                sender.sendMessage(prefix + needcolor);
                            } else {
                                if (sender.hasPermission("awcc.set.strike")) {
                                    if (!teamrainbow.contains(target.getName())) {
                                        teamstrike.add(target.getName());
                                        sender.sendMessage(prefix + ChatColor.GREEN + formatchanged);
                                        if (!(sender == target)) {
                                            target.sendMessage(prefix + ChatColor.GREEN + formatchanged);
                                        }
                                        if (sounds == true) {
                                            target.playSound(target.getLocation(), Sound.valueOf(NOTE_PLING), 1, 2);
                                        }
                                    } else {
                                        sender.sendMessage(prefix + ChatColor.RED + errorrainbow);
                                    }
                                } else {
                                    sender.sendMessage(prefix + permission);
                                }
                            }
                        } else if (args[1].equalsIgnoreCase("underline")) {
                            if (!color.containsKey(sender.getName()) && !teamrainbow.contains(target.getName())) {
                                sender.sendMessage(prefix + needcolor);
                            } else {
                                if (sender.hasPermission("awcc.set.underline")) {
                                    if (!teamrainbow.contains(target.getName())) {
                                        teamunline.add(target.getName());
                                        sender.sendMessage(prefix + ChatColor.GREEN + formatchanged);
                                        if (!(sender == target)) {
                                            target.sendMessage(prefix + ChatColor.GREEN + formatchanged);
                                        }
                                        if (sounds == true) {
                                            target.playSound(target.getLocation(), Sound.valueOf(NOTE_PLING), 1, 2);
                                        }
                                    } else {
                                        sender.sendMessage(prefix + ChatColor.RED + errorrainbow);
                                    }
                                } else {
                                    sender.sendMessage(prefix + permission);
                                }
                            }
                        } else if (args[1].equalsIgnoreCase("italic")) {
                            if (!color.containsKey(sender.getName()) && !teamrainbow.contains(target.getName())) {
                                sender.sendMessage(prefix + needcolor);
                            } else {
                                if (sender.hasPermission("awcc.set.italic")) {
                                    if (!teamrainbow.contains(target.getName())) {
                                        teamitalic.add(target.getName());
                                        sender.sendMessage(prefix + ChatColor.GREEN + formatchanged);
                                        if (!(sender == target)) {
                                            target.sendMessage(prefix + ChatColor.GREEN + formatchanged);
                                        }
                                        if (sounds == true) {
                                            target.playSound(target.getLocation(), Sound.valueOf(NOTE_PLING), 1, 2);
                                        }
                                    } else {
                                        sender.sendMessage(prefix + ChatColor.RED + errorrainbow);
                                    }
                                } else {
                                    sender.sendMessage(prefix + permission);
                                }
                            }
                        } else if (args[1].equalsIgnoreCase("clear")) {
                            if (sender.hasPermission("awcc.set.clear")) {
                                quitar(target.getName());
                                sender.sendMessage(prefix + ChatColor.GREEN + cleared);
                                if (!(sender == target)) {
                                    target.sendMessage(prefix + ChatColor.GREEN + cleared);
                                }
                                if (sounds == true) {
                                    target.playSound(target.getLocation(), Sound.valueOf(EXPLODE), 1, 1);
                                }
                            } else {
                                sender.sendMessage(prefix + permission);
                            }
                        } else {
                            if (sender.hasPermission("awcc.set.dred") || sender.hasPermission("awcc.set.black") || sender.hasPermission("awcc.set.dblue") || sender.hasPermission("awcc.set.blue") || sender.hasPermission("awcc.set.cyan") || sender.hasPermission("awcc.set.gray") || sender.hasPermission("awcc.set.green") || sender.hasPermission("awcc.set.lime") || sender.hasPermission("awcc.set.magenta") || sender.hasPermission("awcc.set.orange") || sender.hasPermission("awcc.set.pink") || sender.hasPermission("awcc.set.red") || sender.hasPermission("awcc.set.silver") || sender.hasPermission("awcc.set.white") || sender.hasPermission("awcc.set.yellow") || sender.hasPermission("awcc.set.rainbow") || sender.hasPermission("awcc.set.bold") || sender.hasPermission("awcc.set.strike") || sender.hasPermission("awcc.set.unline") || sender.hasPermission("awcc.set.italic") || sender.hasPermission("awcc.set.clear")) {
                                sender.sendMessage(prefix + colorerror);
                            }
                        }
                    } else {
                        sender.sendMessage(prefix + permission);
                    }
                }
            }
            if (args.length >= 4) {
                if (sender.hasPermission("awcc.help")) {
                    sender.sendMessage(prefix + usage);
                }
            }
        }
        return true;
    }

    public void abrir(Player player) {
        inve.setItem(sdred, dred);
        inve.setItem(sblack, black);
        inve.setItem(sdblue, dblue);
        inve.setItem(sblue, blue);
        inve.setItem(scyan, cyan);
        inve.setItem(sgray, gray);
        inve.setItem(sgreen, green);
        inve.setItem(slime, lime);
        inve.setItem(smagenta, magenta);
        inve.setItem(sorange, orange);
        inve.setItem(spink, pink);
        inve.setItem(sred, red);
        inve.setItem(ssilver, silver);
        inve.setItem(swhite, white);
        inve.setItem(syellow, yellow);
        inve.setItem(sbold, bold);
        inve.setItem(sstrike, strike);
        inve.setItem(sunline, unline);
        inve.setItem(sitalic, italic);
        inve.setItem(srainbow, rainbow);
        inve.setItem(sclear, clear);
        player.openInventory(inve);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String msg = e.getMessage();
        if (teambold.contains(player.getName())) {
            msg = ChatColor.BOLD + msg;
        }
        if (teamstrike.contains(player.getName())) {
            msg = ChatColor.STRIKETHROUGH + msg;
        }
        if (teamunline.contains(player.getName())) {
            msg = ChatColor.UNDERLINE + msg;
        }
        if (teamitalic.contains(player.getName())) {
            msg = ChatColor.ITALIC + msg;
        }
        if (color.containsKey(player.getName())) {
            ChatColor ch = ChatColor.valueOf(color.get(player.getName()));
            e.setMessage(ch + msg);
        }
        if (teamrainbow.contains(player.getName())) {
            StringBuilder sb = new StringBuilder();
            Random random = new Random();
            for (Character character : msg.toCharArray()) {
                sb.append(ChatColor.getByChar(Integer.toHexString(random.nextInt(16))));
                sb.append(character);
            }
            String msga = sb.toString();
            e.setMessage(msga);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        if (inventory.getName().equals(inve.getName())) {
            try {
                if (event.getCurrentItem().equals(new ItemStack(dred))) {
                    enClick(player, "dred", "DARK_RED");
                }
                if (event.getCurrentItem().equals(new ItemStack(black))) {
                    enClick(player, "black", "BLACK");
                }
                if (event.getCurrentItem().equals(new ItemStack(dblue))) {
                    enClick(player, "dblue", "DARK_BLUE");
                }
                if (event.getCurrentItem().equals(new ItemStack(blue))) {
                    enClick(player, "blue", "BLUE");
                }
                if (event.getCurrentItem().equals(new ItemStack(cyan))) {
                    enClick(player, "cyan", "AQUA");
                }
                if (event.getCurrentItem().equals(new ItemStack(gray))) {
                    enClick(player, "gray", "GRAY");
                }
                if (event.getCurrentItem().equals(new ItemStack(green))) {
                    enClick(player, "green", "DARK_GREEN");
                }
                if (event.getCurrentItem().equals(new ItemStack(lime))) {
                    enClick(player, "lime", "GREEN");
                }
                if (event.getCurrentItem().equals(new ItemStack(magenta))) {
                    enClick(player, "magenta", "DARK_PURPLE");
                }
                if (event.getCurrentItem().equals(new ItemStack(orange))) {
                    enClick(player, "orange", "GOLD");
                }
                if (event.getCurrentItem().equals(new ItemStack(pink))) {
                    enClick(player, "pink", "LIGHT_PURPLE");
                }
                if (event.getCurrentItem().equals(new ItemStack(red))) {
                    enClick(player, "red", "RED");
                }
                if (event.getCurrentItem().equals(new ItemStack(silver))) {
                    enClick(player, "silver", "DARK_GRAY");
                }
                if (event.getCurrentItem().equals(new ItemStack(white))) {
                    enClick(player, "white", "WHITE");
                }
                if (event.getCurrentItem().equals(new ItemStack(yellow))) {
                    enClick(player, "yellow", "YELLOW");
                }
                if (event.getCurrentItem().equals(new ItemStack(rainbow))) {
                    if (player.hasPermission("awcc.color.rainbow")) {
                        pon(player.getName());
                        if (sql == true) {
                            SQL.setColor(player.getUniqueId().toString(), "RAINBOW");
                        }
                        player.sendMessage(prefix + ChatColor.GREEN + successfull);
                        if (sounds == true) {
                            player.playSound(player.getLocation(), Sound.valueOf(FUSE), 1, 1);
                        }
                    } else {
                        player.sendMessage(prefix + permission);
                    }
                }
                if (event.getCurrentItem().equals(new ItemStack(bold))) {
                    if (player.hasPermission("awcc.format.bold")) {
                        if (!color.containsKey(player.getName()) && !teamrainbow.contains(player.getName())) {
                            player.sendMessage(prefix + firstselect);
                        } else {
                            if (!teamrainbow.contains(player.getName())) {
                                teambold.add(player.getName());
                                if (sql == true) {
                                    SQL.setBold(player.getUniqueId().toString(), "true");
                                }
                                player.sendMessage(prefix + ChatColor.GREEN + formatchanged);
                                if (sounds == true) {
                                    player.playSound(player.getLocation(), Sound.valueOf(NOTE_PLING), 1, 2);
                                }
                            } else {
                                player.sendMessage(prefix + ChatColor.RED + errorrainbow);
                            }
                        }
                    } else {
                        player.sendMessage(prefix + permission);
                    }
                }
                if (event.getCurrentItem().equals(new ItemStack(strike))) {
                    if (player.hasPermission("awcc.format.strike")) {
                        if (!color.containsKey(player.getName()) && !teamrainbow.contains(player.getName())) {
                            player.sendMessage(prefix + firstselect);
                        } else {
                            if (!teamrainbow.contains(player.getName())) {
                                teamstrike.add(player.getName());
                                if (sql == true) {
                                    SQL.setStrike(player.getUniqueId().toString(), "true");
                                }
                                player.sendMessage(prefix + ChatColor.GREEN + formatchanged);
                                if (sounds == true) {
                                    player.playSound(player.getLocation(), Sound.valueOf(NOTE_PLING), 1, 2);
                                }
                            } else {
                                player.sendMessage(prefix + ChatColor.RED + errorrainbow);
                            }
                        }
                    } else {
                        player.sendMessage(prefix + permission);
                    }
                }
                if (event.getCurrentItem().equals(new ItemStack(unline))) {
                    if (player.hasPermission("awcc.format.unline")) {
                        if (!color.containsKey(player.getName()) && !teamrainbow.contains(player.getName())) {
                            player.sendMessage(prefix + firstselect);
                        } else {
                            if (!teamrainbow.contains(player.getName())) {
                                teamunline.add(player.getName());
                                if (sql == true) {
                                    SQL.setUnline(player.getUniqueId().toString(), "true");
                                }
                                player.sendMessage(prefix + ChatColor.GREEN + formatchanged);
                                if (sounds == true) {
                                    player.playSound(player.getLocation(), Sound.valueOf(NOTE_PLING), 1, 2);
                                }
                            } else {
                                player.sendMessage(prefix + ChatColor.RED + errorrainbow);
                            }
                        }
                    } else {
                        player.sendMessage(prefix + permission);
                    }
                }
                if (event.getCurrentItem().equals(new ItemStack(italic))) {
                    if (player.hasPermission("awcc.format.italic")) {
                        if (!color.containsKey(player.getName()) && !teamrainbow.contains(player.getName())) {
                            player.sendMessage(prefix + firstselect);
                        } else {
                            if (!teamrainbow.contains(player.getName())) {
                                teamitalic.add(player.getName());
                                if (sql == true) {
                                    SQL.setItalic(player.getUniqueId().toString(), "true");
                                }
                                player.sendMessage(prefix + ChatColor.GREEN + formatchanged);
                                if (sounds == true) {
                                    player.playSound(player.getLocation(), Sound.valueOf(NOTE_PLING), 1, 2);
                                }
                            } else {
                                player.sendMessage(prefix + ChatColor.RED + errorrainbow);
                            }
                        }
                    } else {
                        player.sendMessage(prefix + permission);
                    }
                }
                if (event.getCurrentItem().equals(new ItemStack(clear))) {
                    if (player.hasPermission("awcc.clear")) {
                        if (!color.containsKey(player.getName()) && !teamrainbow.contains(player.getName()) && !teambold.contains(player.getName()) && !teamstrike.contains(player.getName()) && !teamunline.contains(player.getName()) && !teamitalic.contains(player.getName())) {
                            player.sendMessage(prefix + noneclear);
                        } else {
                            quitar(player.getName());
                            player.sendMessage(prefix + ChatColor.GREEN + cleared);
                            if (sounds == true) {
                                player.playSound(player.getLocation(), Sound.valueOf(EXPLODE), 1, 1);
                            }
                        }
                    } else {
                        player.sendMessage(prefix + permission);
                    }
                }
                event.setCancelled(true);
            } catch (Exception e) {

            }
        }
    }

    public void enClick(Player player, String permiso, String col) {
        if (player.hasPermission("awcc.color." + permiso)) {
            if (teamrainbow.contains(player.getName())) {
                teamrainbow.remove(player.getName());
            }
            color.put(player.getName(), col);
            if (sql == true) {
                SQL.setColor(player.getUniqueId().toString(), col);
            } else {
                cdata.set(player.getUniqueId().toString(), col);
                guardar(data, cdata);
            }
            player.sendMessage(prefix + ChatColor.GREEN + successfull);
            if (sounds == true) {
                player.playSound(player.getLocation(), Sound.valueOf(NOTE_PIANO), 1, -3);
            }

        } else {
            player.sendMessage(prefix + permission);
        }
    }

    public void idioma() {
        teambold = cdata.getStringList("bold");
        teamstrike = cdata.getStringList("strike");
        teamunline = cdata.getStringList("unline");
        teamitalic = cdata.getStringList("italic");
        teamrainbow = cdata.getStringList("rainbow");
        needcolor = tcc(getConfig().getString("needcolor"));
        noneclear = tcc(getConfig().getString("noneclear"));
        firstselect = tcc(getConfig().getString("firstselect"));
        permission = tcc(getConfig().getString("permission"));
        colorerror = tcc(getConfig().getString("colorerror"));
        offline = tcc(getConfig().getString("offline"));
        help = tcc(getConfig().getString("help"));
        usage = tcc(getConfig().getString("usage"));
        listmessage = tcc(getConfig().getString("listmessage"));
        cleared = tcc(getConfig().getString("cleared"));
        formatchanged = tcc(getConfig().getString("formatchanged"));
        errorrainbow = tcc(getConfig().getString("errorrainbow"));
        prefix = tcc(getConfig().getString("prefix"));
        successfull = tcc(getConfig().getString("successfull"));
        mensa = tcc(getConfig().getString("reload"));
        type = getConfig().getInt("type");
        sounds = getConfig().getBoolean("sounds");
        inve = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', getConfig().getString("GuiName")));
        if (type == 6) {
            dred = crearTraje(Color.fromRGB(153, 0, 0), getConfig().getString("wool.dred.title"), getConfig().getStringList("wool.dred.lore"));
            black = crearTraje(Color.fromRGB(0, 0, 0), getConfig().getString("wool.black.title"), getConfig().getStringList("wool.black.lore"));
            dblue = crearTraje(Color.fromRGB(0, 0, 204), getConfig().getString("wool.dark_blue.title"), getConfig().getStringList("wool.dark_blue.lore"));
            blue = crearTraje(Color.fromRGB(51, 153, 255), getConfig().getString("wool.blue.title"), getConfig().getStringList("wool.blue.lore"));
            cyan = crearTraje(Color.fromRGB(102, 255, 255), getConfig().getString("wool.cyan.title"), getConfig().getStringList("wool.cyan.lore"));
            gray = crearTraje(Color.fromRGB(192, 192, 192), getConfig().getString("wool.gray.title"), getConfig().getStringList("wool.gray.lore"));
            green = crearTraje(Color.fromRGB(0, 153, 0), getConfig().getString("wool.green.title"), getConfig().getStringList("wool.green.lore"));
            lime = crearTraje(Color.fromRGB(102, 255, 102), getConfig().getString("wool.lime.title"), getConfig().getStringList("wool.lime.lore"));
            magenta = crearTraje(Color.fromRGB(204, 0, 204), getConfig().getString("wool.magenta.title"), getConfig().getStringList("wool.magenta.lore"));
            orange = crearTraje(Color.fromRGB(204, 204, 0), getConfig().getString("wool.orange.title"), getConfig().getStringList("wool.orange.lore"));
            pink = crearTraje(Color.fromRGB(255, 51, 255), getConfig().getString("wool.pink.title"), getConfig().getStringList("wool.pink.lore"));
            red = crearTraje(Color.fromRGB(255, 0, 0), getConfig().getString("wool.red.title"), getConfig().getStringList("wool.red.lore"));
            silver = crearTraje(Color.fromRGB(96, 96, 96), getConfig().getString("wool.silver.title"), getConfig().getStringList("wool.silver.lore"));
            white = crearTraje(Color.fromRGB(255, 255, 255), getConfig().getString("wool.white.title"), getConfig().getStringList("wool.white.lore"));
            yellow = crearTraje(Color.fromRGB(255, 255, 102), getConfig().getString("wool.yellow.title"), getConfig().getStringList("wool.yellow.lore"));
        } else {
            dred = createItem(DyeColor.BROWN, getConfig().getString("wool.dred.title"), getConfig().getStringList("wool.dred.lore"));
            black = createItem(DyeColor.BLACK, getConfig().getString("wool.black.title"), getConfig().getStringList("wool.black.lore"));
            dblue = createItem(DyeColor.BLUE, getConfig().getString("wool.dark_blue.title"), getConfig().getStringList("wool.dark_blue.lore"));
            blue = createItem(DyeColor.LIGHT_BLUE, getConfig().getString("wool.blue.title"), getConfig().getStringList("wool.blue.lore"));
            cyan = createItem(DyeColor.CYAN, getConfig().getString("wool.cyan.title"), getConfig().getStringList("wool.cyan.lore"));
            gray = createItem(DyeColor.SILVER, getConfig().getString("wool.gray.title"), getConfig().getStringList("wool.gray.lore"));
            green = createItem(DyeColor.GREEN, getConfig().getString("wool.green.title"), getConfig().getStringList("wool.green.lore"));
            lime = createItem(DyeColor.LIME, getConfig().getString("wool.lime.title"), getConfig().getStringList("wool.lime.lore"));
            magenta = createItem(DyeColor.MAGENTA, getConfig().getString("wool.magenta.title"), getConfig().getStringList("wool.magenta.lore"));
            orange = createItem(DyeColor.ORANGE, getConfig().getString("wool.orange.title"), getConfig().getStringList("wool.orange.lore"));
            pink = createItem(DyeColor.PINK, getConfig().getString("wool.pink.title"), getConfig().getStringList("wool.pink.lore"));
            red = createItem(DyeColor.RED, getConfig().getString("wool.red.title"), getConfig().getStringList("wool.red.lore"));
            silver = createItem(DyeColor.GRAY, getConfig().getString("wool.silver.title"), getConfig().getStringList("wool.silver.lore"));
            white = createItem(DyeColor.WHITE, getConfig().getString("wool.white.title"), getConfig().getStringList("wool.white.lore"));
            yellow = createItem(DyeColor.YELLOW, getConfig().getString("wool.yellow.title"), getConfig().getStringList("wool.yellow.lore"));
        }
        bold = crear(Material.COAL, getConfig().getString("format.bold.title"), getConfig().getStringList("format.bold.lore"));
        strike = crear(Material.WHEAT, getConfig().getString("format.strike.title"), getConfig().getStringList("format.strike.lore"));
        unline = crear(Material.STICK, getConfig().getString("format.unline.title"), getConfig().getStringList("format.unline.lore"));
        italic = crear(Material.BLAZE_ROD, getConfig().getString("format.italic.title"), getConfig().getStringList("format.italic.lore"));
        clear = crear(Material.TNT, getConfig().getString("format.cleartnt.title"), getConfig().getStringList("format.cleartnt.lore"));
        rainbow = crear(Material.ENDER_PEARL, getConfig().getString("wool.rainbow.title"), getConfig().getStringList("wool.rainbow.lore"));
        sdred = getConfig().getInt("wool.dred.slot");
        sblack = getConfig().getInt("wool.black.slot");
        sdblue = getConfig().getInt("wool.dark_blue.slot");
        sblue = getConfig().getInt("wool.blue.slot");
        scyan = getConfig().getInt("wool.cyan.slot");
        sgray = getConfig().getInt("wool.gray.slot");
        sgreen = getConfig().getInt("wool.green.slot");
        slime = getConfig().getInt("wool.lime.slot");
        smagenta = getConfig().getInt("wool.magenta.slot");
        sorange = getConfig().getInt("wool.orange.slot");
        spink = getConfig().getInt("wool.pink.slot");
        sred = getConfig().getInt("wool.red.slot");
        ssilver = getConfig().getInt("wool.silver.slot");
        swhite = getConfig().getInt("wool.white.slot");
        syellow = getConfig().getInt("wool.yellow.slot");
        sbold = getConfig().getInt("format.bold.slot");
        sstrike = getConfig().getInt("format.strike.slot");
        sunline = getConfig().getInt("format.unline.slot");
        sitalic = getConfig().getInt("format.italic.slot");
        srainbow = getConfig().getInt("wool.rainbow.slot");
        sclear = getConfig().getInt("format.cleartnt.slot");
        sql = getConfig().getBoolean("mysql.use");
    }

    public Boolean getMySQL() {
        return sql;
    }

    public File getcConfig() {
        return config;
    }
}
