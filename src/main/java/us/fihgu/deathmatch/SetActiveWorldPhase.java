package us.fihgu.deathmatch;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import us.fihgu.minigamecore.game.GamePhase;

import java.util.function.Supplier;

/**
 * Created by fihgu on 9/9/2016.
 */
public class SetActiveWorldPhase extends GamePhase
{
    Supplier<World> worldSupplier;
    public SetActiveWorldPhase(DeathMatchSession session, Supplier<World> worldSupplier)
    {
        super(session);
        this.worldSupplier = worldSupplier;
    }

    @Override
    protected void onEnteringPhase()
    {
        ((DeathMatchSession)session).activeWorld = worldSupplier;

        session.getPlayers().keySet().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if(player != null && player.isOnline())
            {
                player.setBedSpawnLocation(worldSupplier.get().getSpawnLocation(), true);
            }
        });

        this.exitPhase();
    }

    @Override
    protected void onExitingPhase()
    {

    }

    @Override
    public void dispose()
    {
        worldSupplier = null;
        super.dispose();
    }
}
