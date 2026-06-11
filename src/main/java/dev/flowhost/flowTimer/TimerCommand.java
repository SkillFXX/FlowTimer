package dev.flowhost.flowtimer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TimerCommand implements CommandExecutor {

    private final TimerManager timerManager;
    private final FileManager fm;

    public TimerCommand(TimerManager timerManager, FileManager fm) {
        this.timerManager = timerManager;
        this.fm = fm;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("flowtimer.admin")) {
            sender.sendMessage(fm.getMsg("messages.no_permission"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(fm.getMsg("messages.usage"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start" ->
                timerManager.startCountdown();
            case "pause" ->
                timerManager.togglePause();
            case "stop" ->
                timerManager.stopTimer();
            default ->
                sender.sendMessage(fm.getMsg("messages.usage"));
        }
        return true;
    }
}
