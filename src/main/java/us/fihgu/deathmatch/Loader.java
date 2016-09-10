package us.fihgu.deathmatch;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import us.fihgu.minigamecore.game.MinigameManager;

public class Loader extends JavaPlugin
{
	public static Loader instance;

	//TODO: edit plugin config and yml file.

	@Override 
	public void onEnable()
	{
		instance = this;

		//create default config file
		this.saveDefaultConfig();

		MinigameManager.registerMinigame(new Deathmatch());
	}
	
	@Override
	public void onDisable()
	{

	}

	private void registerCommandExecutor(String command, CommandExecutor executor)
	{
		this.getCommand(command).setExecutor(executor);
	}
}