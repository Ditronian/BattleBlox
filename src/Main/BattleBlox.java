package Main;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import Maps.GameMap;
import Other.KillDeath;


public class BattleBlox extends JavaPlugin
{
	private HashMap<String, GameMap> maps = new HashMap<String, GameMap>();
	private HashMap<String, KillDeath> kds = new HashMap<String, KillDeath>();
	
	//------------- Public Methods -------------//

	//Automatically ran when server is put online
	public void onEnable()
	{
		maps = FileHandler.loadMaps(maps);	//Loads maps from the file
		kds = FileHandler.loadKD(kds);    //Loads maps from the file
		CommandHandler.setPlugin(this);		//Passes a reference to the plugin object to the Command Handler
		CommandHandler.setMaps(maps);		//Passes a reference to the maps hashmap to the Command Handler
		Bukkit.getServer().getLogger().info("BattleBlox enabled");		//Sends message to the Server console
	}

	//Automatically ran when server is taken offline
	public void onDisable()
	{
		FileHandler.saveMaps(maps);
		FileHandler.saveKD(kds);
		Bukkit.getServer().getLogger().info("BattleBlox disabled");		//Sends message to the Server console
	}

	//Recieves all commands ever entered and passes them to the CommandHandler class for processing
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		CommandHandler.receivedCommand(sender, cmd, commandLabel, args, this);
		return true;
	}



	public HashMap<String, GameMap> getMaps()
	{
		return maps;
	}

	public void setMaps(HashMap<String, GameMap> maps)
	{
		this.maps = maps;
	}

    public HashMap<String, KillDeath> getKds()
    {
        return kds;
    }

    public void setKds(HashMap<String, KillDeath> kds)
    {
        this.kds = kds;
    }

//private void endEvent()	

}