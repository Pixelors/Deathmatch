package us.fihgu.deathmatch;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import us.fihgu.minigamecore.game.GamePhase;
import us.fihgu.minigamecore.game.GameSession;

import java.util.HashMap;
import java.util.UUID;

public class MainPhase extends GamePhase
{
    private int objective;

    private HashMap<UUID, Integer> killCounts = new HashMap<>();
    Listener killListener;

    public MainPhase(GameSession session, int objective)
    {
        super(session);
        this.objective = objective;
    }

    @Override
    protected void onEnteringPhase()
    {
        killListener = new Listener()
        {
            @EventHandler
            public void onKill(EntityDamageByEntityEvent event)
            {
                Entity damagerEntity = event.getDamager();
                Entity victimEntity = event.getEntity();

                //if both entity is player
                if(damagerEntity instanceof Player && victimEntity instanceof Player)
                {
                    Player damager = (Player) damagerEntity;
                    Player victim = (Player) victimEntity;

                    // If both player belongs to this session.
                    if(session.getPlayers().containsKey(damager.getUniqueId()) && session.getPlayers().containsKey(victim.getUniqueId()))
                    {
                        // If victim will be killed.
                        if(event.getFinalDamage() >= victim.getHealth())
                        {
                            int kills = killCounts.get(damager.getUniqueId());
                            killCounts.put(damager.getUniqueId(), kills + 1);

                            session.getPlayers().keySet().forEach(uuid -> {
                                Player player = Bukkit.getPlayer(uuid);
                                if(player != null && player.isOnline())
                                {
                                    player.sendMessage(ChatColor.GOLD + damager.getDisplayName() + " now has " + (kills + 1) + " points.");
                                }
                            });

                            if(kills + 1 >= objective)
                            {
                                session.getPlayers().keySet().forEach(uuid -> {
                                    Player player = Bukkit.getPlayer(uuid);
                                    if(player != null && player.isOnline())
                                    {
                                        player.sendMessage(ChatColor.GOLD + damager.getDisplayName() + " has won the game!");
                                    }
                                });

                                exitPhase();
                            }
                        }
                    }
                }
            }
        };

        Bukkit.getPluginManager().registerEvents(killListener,Loader.instance);
    }

    @Override
    protected void onExitingPhase()
    {
        HandlerList.unregisterAll(killListener);
    }

    @Override
    public void dispose()
    {
        this.killCounts.clear();
        this.killCounts = null;

        if(this.killListener != null)
        {
            HandlerList.unregisterAll(killListener);
            killListener = null;
        }

        super.dispose();
    }
}
