package dev.flowhost.flowtimer;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TimerManager {

    private final FlowTimer plugin;
    private final FileManager fm;
    private final BossBar bossBar;

    private TimerState state = TimerState.STOPPED;

    private long elapsedMillis = 0;
    private long lastStartTime = 0;
    private int countdownSeconds = 5;

    public TimerManager(FlowTimer plugin) {
        this.plugin = plugin;
        this.fm = plugin.getFileManager();
        this.bossBar = Bukkit.createBossBar(fm.getMsg("bossbar.stopped"), BarColor.WHITE, BarStyle.SOLID);
        this.bossBar.setVisible(true);

        for (Player p : Bukkit.getOnlinePlayers()) {
            this.bossBar.addPlayer(p);
        }
        startBossBarTask();
        startAutoSaveTask();
    }

    public TimerState getState() {
        return state;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public void startCountdown() {
        if (state == TimerState.RUNNING || state == TimerState.COUNTDOWN) {
            return;
        }

        this.state = TimerState.COUNTDOWN;
        this.countdownSeconds = plugin.getConfig().getInt("countdown-seconds", 5);
        this.elapsedMillis = 0;

        new BukkitRunnable() {
            int current = countdownSeconds;

            @Override
            public void run() {
                if (state != TimerState.COUNTDOWN) {
                    this.cancel();
                    return;
                }

                if (current > 0) {
                    broadcast(fm.getMsg("messages.countdown").replace("%time%", String.valueOf(current)));
                    playSound(Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f);
                    bossBar.setTitle(fm.getMsg("bossbar.countdown").replace("%time%", String.valueOf(current)));
                    bossBar.setColor(BarColor.YELLOW);
                    current--;
                } else {
                    startTimer();
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void startTimer() {
        this.state = TimerState.RUNNING;
        this.lastStartTime = System.currentTimeMillis();
        this.bossBar.setColor(BarColor.GREEN);
        playSound(Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f);
        broadcast(fm.getMsg("messages.started"));
    }

    public void togglePause() {
        if (state == TimerState.RUNNING) {
            elapsedMillis += (System.currentTimeMillis() - lastStartTime);
            state = TimerState.PAUSED;
            bossBar.setColor(BarColor.RED);
            bossBar.setTitle(fm.getMsg("bossbar.paused").replace("%time%", formatTime(elapsedMillis)));
            broadcast(fm.getMsg("messages.paused"));
            saveData();
        } else if (state == TimerState.PAUSED) {
            lastStartTime = System.currentTimeMillis();
            state = TimerState.RUNNING;
            bossBar.setColor(BarColor.GREEN);
            broadcast(fm.getMsg("messages.resumed"));
        }
    }

    public void stopTimer() {
        this.state = TimerState.STOPPED;
        this.elapsedMillis = 0;
        this.bossBar.setColor(BarColor.WHITE);
        this.bossBar.setTitle(fm.getMsg("bossbar.stopped"));
        broadcast(fm.getMsg("messages.stopped"));
        saveData();
    }

    public void finishTimer(Player winner) {
        if (state != TimerState.RUNNING) {
            return;
        }

        elapsedMillis += (System.currentTimeMillis() - lastStartTime);
        state = TimerState.FINISHED;
        bossBar.setColor(BarColor.PURPLE);

        String finalTime = formatTime(elapsedMillis);
        bossBar.setTitle(fm.getMsg("bossbar.finished").replace("%time%", finalTime));
        broadcast(fm.getMsg("messages.finished").replace("%player%", winner.getName()).replace("%time%", finalTime));
        playSound(Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f);
        saveData();
    }

    private void startBossBarTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (state == TimerState.RUNNING) {
                    long currentElapsed = elapsedMillis + (System.currentTimeMillis() - lastStartTime);
                    bossBar.setTitle(fm.getMsg("bossbar.running").replace("%time%", formatTime(currentElapsed)));
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 1L);
    }

    private void startAutoSaveTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (state == TimerState.RUNNING || state == TimerState.PAUSED) {
                    saveData();
                }
            }
        }.runTaskTimer(plugin, 600L, 600L);
    }

    private String formatTime(long millis) {
        long ms = millis % 1000;
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = (millis / (1000 * 60 * 60));

        if (hours > 0) {
            return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, ms);
        }
        return String.format("%02d:%02d.%03d", minutes, seconds, ms);
    }

    private void broadcast(String message) {
        Bukkit.broadcastMessage(message);
    }

    private void playSound(Sound sound, float pitch) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), sound, 1.0f, pitch);
        }
    }

    public void saveData() {
        FileConfiguration data = fm.getData();
        data.set("state", state.name());
        long timeToSave = (state == TimerState.RUNNING) ? elapsedMillis + (System.currentTimeMillis() - lastStartTime) : elapsedMillis;
        data.set("time", timeToSave);
        fm.saveData();
    }

    public void loadData() {
        FileConfiguration data = fm.getData();
        if (data.contains("state")) {
            this.state = TimerState.valueOf(data.getString("state", "STOPPED"));
            this.elapsedMillis = data.getLong("time", 0);

            if (state == TimerState.RUNNING) {
                this.state = TimerState.PAUSED;
            }
            if (state == TimerState.PAUSED || state == TimerState.FINISHED) {
                bossBar.setTitle(fm.getMsg("bossbar." + state.name().toLowerCase()).replace("%time%", formatTime(elapsedMillis)));
            }
        }
    }

    public void cleanup() {
        bossBar.removeAll();
    }
}
