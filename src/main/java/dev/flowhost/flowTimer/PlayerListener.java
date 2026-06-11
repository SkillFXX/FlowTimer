package dev.flowhost.flowtimer;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerListener implements Listener {

    private final TimerManager timerManager;

    public PlayerListener(TimerManager timerManager) {
        this.timerManager = timerManager;
    }

    private boolean isFrozen() {
        TimerState state = timerManager.getState();
        return state == TimerState.STOPPED || state == TimerState.COUNTDOWN || state == TimerState.PAUSED || state == TimerState.FINISHED;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        timerManager.getBossBar().addPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (isFrozen()) {
            // Permet de tourner la tête, mais pas de se déplacer physiquement
            if (e.getFrom().getX() != e.getTo().getX()
                    || e.getFrom().getY() != e.getTo().getY()
                    || e.getFrom().getZ() != e.getTo().getZ()) {
                e.setTo(e.getFrom().setDirection(e.getTo().getDirection()));
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (isFrozen()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (isFrozen()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (isFrozen()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && isFrozen()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if (isFrozen()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {

        if (e.getFrom().getWorld().getEnvironment() == World.Environment.THE_END
                && e.getTo().getWorld().getEnvironment() == World.Environment.NORMAL) {

            if (timerManager.getState() == TimerState.RUNNING) {
                timerManager.finishTimer(e.getPlayer());
            }
        }
    }
}
