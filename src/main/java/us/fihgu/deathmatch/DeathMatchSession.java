package us.fihgu.deathmatch;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import us.fihgu.minigamecore.game.*;
import us.fihgu.minigamecore.game.phase.VoteMapPhase;
import us.fihgu.minigamecore.game.phase.WorldTransferPhase;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DeathMatchSession extends GameSession
{
    Supplier<World> activeWorld;
    LinkedList<GamePhase> phases;

    protected DeathMatchSession(Lobby lobby, Minigame game)
    {
        super(lobby, game);

        phases = new LinkedList<>();

        //create a lobby world
        WorldTransferPhase lobbyTransferPhase = new WorldTransferPhase(this, () -> new Map("Lobby", Configuration.MAP_PATH + "lobby"), null);
        phases.add(lobbyTransferPhase);

        //set lobby world as the currently active world
        SetActiveWorldPhase setLobbyWorld = new SetActiveWorldPhase(this, lobbyTransferPhase);
        phases.add(setLobbyWorld);

        //Create a list of Maps that contains all maps in the config file.
        List<String> mapNames = Loader.instance.getConfig().getStringList("maps");
        LinkedList<Map> maps = mapNames.stream().map(mapName -> new Map(mapName, Configuration.MAP_PATH + mapName)).collect(Collectors.toCollection(LinkedList::new));

        //wait for player to teleport
        phases.add(new DelayPhase(this, 20));

        //A vote map phase that last 30s, player will vote for the next map to play.
        VoteMapPhase voteMapPhase = new VoteMapPhase(this, 30, maps);
        phases.add(voteMapPhase);

        //Transfer players to the map voted.
        WorldTransferPhase worldTransferPhase = new WorldTransferPhase(this, voteMapPhase, null);
        phases.add(worldTransferPhase);

        //set the arena as new active world.
        SetActiveWorldPhase setArenaWorld = new SetActiveWorldPhase(this, worldTransferPhase);
        phases.add(setArenaWorld);

        //wait for player to teleport
        phases.add(new DelayPhase(this, 20));

        //start The main phase.
        phases.add(new MainPhase(this, 20));
    }

    @Override
    protected GamePhase getNextPhase()
    {
        if(phases != null && !phases.isEmpty())
        {
            return phases.pop();
        }
        return null;
    }

    @Override
    public void onPlayerJoin(Player player)
    {
        World activeWorld = this.activeWorld.get();
        if(activeWorld != null)
        {
            player.teleport(activeWorld.getSpawnLocation());
            player.setBedSpawnLocation(activeWorld.getSpawnLocation(), true);
        }
    }

    @Override
    public void onPlayerLeave(Player player)
    {
        player.setBedSpawnLocation(Bukkit.getWorlds().get(0).getSpawnLocation(), true);
    }

    @Override
    public void dispose()
    {
        activeWorld = null;
        phases = null;
        super.dispose();
    }
}
