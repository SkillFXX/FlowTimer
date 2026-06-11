package dev.flowhost.flowtimer;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FileManager {

    private final FlowTimer plugin;
    private File dataFile;
    private FileConfiguration dataConfig;
    private File messagesFile;
    private FileConfiguration messagesConfig;

    public FileManager(FlowTimer plugin) {
        this.plugin = plugin;
        initMessages();
        initData();
    }

    private void initMessages() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    private void initData() {
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Error in FlowTimer while creating data.yml");
                e.printStackTrace();
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public String getMsg(String path) {
        String msg = messagesConfig.getString(path, "Message not found: " + path);
        if (msg == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public FileConfiguration getData() {
        return dataConfig;
    }

    public void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Unable to save data.yml");
        }
    }
}
