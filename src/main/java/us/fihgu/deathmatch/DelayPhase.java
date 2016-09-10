package us.fihgu.deathmatch;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import us.fihgu.minigamecore.game.GamePhase;
import us.fihgu.minigamecore.game.GameSession;

public class DelayPhase extends GamePhase
{
    int delay;
    BukkitRunnable task;

    /**
     * @param delay in ticks.
     */
    public DelayPhase(GameSession session, int delay)
    {
        super(session);
        this.delay = delay;
    }

    @Override
    protected void onEnteringPhase()
    {
        task = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                exitPhase();
            }
        };
        task.runTaskLater(Loader.instance, delay);
    }

    @Override
    protected void onExitingPhase()
    {
    }

    @Override
    public void dispose()
    {
        try
        {
            if(task != null)
            {
                task.cancel();
            }
        }
        catch (IllegalStateException e){}

        super.dispose();
    }
}
