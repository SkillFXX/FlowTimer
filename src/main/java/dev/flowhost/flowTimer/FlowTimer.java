package dev.flowhost.flowtimer;

import org.bukkit.plugin.java.JavaPlugin;

public final class FlowTimer extends JavaPlugin {

    private FileManager fileManager;
    private TimerManager timerManager;

    @Override
    public void onEnable() {
        this.fileManager = new FileManager(this);

        this.timerManager = new TimerManager(this);
        this.timerManager.loadData();

        getCommand("ft").setExecutor(new TimerCommand(this.timerManager, this.fileManager));
        getServer().getPluginManager().registerEvents(new PlayerListener(this.timerManager), this);

        getLogger().info("FlowTimer has been successfully activated!");
    }

    @Override
    public void onDisable() {
        if (this.timerManager != null) {
            this.timerManager.saveData();
            this.timerManager.cleanup();
        }
        getLogger().info("FlowTimer has been disabled (Data saved).");
    }

    public FileManager getFileManager() {
        return fileManager;
    }
}
