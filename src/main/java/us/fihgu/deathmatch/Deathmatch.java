package us.fihgu.deathmatch;

import us.fihgu.minigamecore.game.*;

public class Deathmatch extends Minigame
{
    public Deathmatch()
    {
        super("deathmatch", "Death Match", "Be the first one to reach target kills");
    }

    @Override
    public GameSession createGameSession(Lobby lobby)
    {
        return new DeathMatchSession(lobby, this);
    }
}
