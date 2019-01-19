package Main;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import Events.ConquestHandler;
import Events.GameHandler;
import Events.RushHandler;
import Kits.ChainKit;
import Kits.DiamondKit;
import Kits.IronKit;
import Kits.LeatherKit;
import Maps.ConquestMap;
import Maps.GameMap;
import Maps.RushMap;
import Objectives.CapturePointHandler;
import Other.KillDeath;

public class CommandHandler
{
    private static GameMap mapUnderConstruction;
    private static GameHandler currentEvent;
    private static Plugin plugin;
    private static HashMap<String, GameMap> maps;
    private static BattleBlox battleblox;

    public static void receivedCommand(CommandSender sender, Command cmd, String commandLabel, String[] args, BattleBlox bb)
    {
        battleblox = bb;
        if (cmd.getName().equalsIgnoreCase("battleblox") && sender instanceof Player)	//If command is a battleblox command issued by a player
        {
            Player player = (Player) sender;
            if (args.length <= 3 && args[0].equalsIgnoreCase("start"))	//Is a start command
            {
                if (currentEvent != null) player.sendMessage("There is currently an ongoing event.");
                else startEvent(player, args); //send to startEvent method
            }

            else if (args.length == 2 && args[0].equalsIgnoreCase("create"))	//Is a create command
            {
                if (mapUnderConstruction != null) player.sendMessage("You are already currently constructing a map.");
                else if (args[1].equalsIgnoreCase("rush")) createRushMap(player);  //Is a rush map, send to createRushMap method
                else if (args[1].equalsIgnoreCase("conquest")) createConquestMap(player);  //Is a rush map, send to createRushMap method
            }

            else if (args.length == 1 && args[0].equalsIgnoreCase("confirm"))	//Is a confirm finish creation command
            {
                if (mapUnderConstruction == null) player.sendMessage("No map is currently being constructed.");
                else confirmCreateMap(player);  //confirm map construction
            }

            else if (args.length == 1 && args[0].equalsIgnoreCase("cancel"))	//Is a cancel map creation command
            {
                if (mapUnderConstruction == null) player.sendMessage("No map is currently being constructed.");
                else cancelCreateMap(player);  //Is a rush map, send to createRushMap method
            }

            else if (args.length == 2 && args[0].equalsIgnoreCase("delete"))	//Is a delete map command
            {
                if (maps.containsKey(args[1]))
                {
                    maps.remove(args[1]);
                    player.sendMessage("Map deleted.");
                }
                else player.sendMessage("Map not found.");  //Map file not found in maps
            }

            else if (args.length == 1 && args[0].equalsIgnoreCase("list"))	//Is a list maps command
            {
                for (HashMap.Entry<String, GameMap> entry : maps.entrySet())
                    player.sendMessage(entry.getKey());
            }

            else if (args.length == 1 && args[0].equalsIgnoreCase("end"))	//Is an end event command
            {
                if (currentEvent == null) player.sendMessage("No event is currently underway");
                else endEvent();
            }

            else if (args.length == 2 && args[0].equalsIgnoreCase("join")) //Is an event join command
            {
                if (currentEvent == null) player.sendMessage("No event is currently underway");
                else joinEvent(player, args[1]);
            }

            else if (args.length == 2 && args[0].equalsIgnoreCase("objective")) //Is a cmd to set the next objective name for the conquest map under construction.
            {
                if (mapUnderConstruction == null) player.sendMessage("No conquest map is currently being constructed.");
                else if (mapUnderConstruction instanceof ConquestMap == false) player.sendMessage("No conquest map is currently being constructed.");
                else
                {
                    ConquestMap map = (ConquestMap) mapUnderConstruction;
                    map.setObjName(args[1]);
                }
            }

            else if (args.length == 1 && args[0].equalsIgnoreCase("commence"))   //Is an end event command
            {
                if (currentEvent == null) player.sendMessage("No event is currently underway");
                else currentEvent.startGame();
            }

            else if (args.length == 2 && args[0].equalsIgnoreCase("edit"))   //Is an edit map capture points command
            {
                if (mapUnderConstruction == null) player.sendMessage("No map is currently being constructed");
                else if (!args[1].equalsIgnoreCase("end")) editMap(player, args[1]);
                else if (args[1].equalsIgnoreCase("end"))
                {
                    mapUnderConstruction.setEditPoint(null);
                    player.sendMessage("Capture Point edit mode terminated.");
                }
                else help(sender);
            }

            else help(sender);	//Improper command, send player command help
        }
        if (cmd.getName().equalsIgnoreCase("kit") && sender instanceof Player)
        {
            Player player = (Player) sender;
            if (currentEvent != null && currentEvent.getPlayers().containsKey(player) && args.length == 1)
            {
                if (currentEvent.getDeadPlayers().containsKey(player))
                {
                    if (args[0].equalsIgnoreCase("iron")) currentEvent.getPlayers().get(player).setKit(new IronKit(player));
                    else if (args[0].equalsIgnoreCase("diamond")) currentEvent.getPlayers().get(player).setKit(new DiamondKit(player));
                    else if (args[0].equalsIgnoreCase("chain")) currentEvent.getPlayers().get(player).setKit(new ChainKit(player));
                    else if (args[0].equalsIgnoreCase("leather")) currentEvent.getPlayers().get(player).setKit(new LeatherKit(player));
                    else player.sendMessage("No such kit exists, please try iron/diamond/chain/leather");
                }
                else player.sendMessage("Kits may only be spawned in the respawn zone.");
            }
            else if (args.length != 1) help(sender);
            else player.sendMessage("You must be in an event to use this command");
        }

        if (cmd.getName().equalsIgnoreCase("select") && sender instanceof Player)
        {
            Player player = (Player) sender;

            if (currentEvent != null && currentEvent.getPlayers().containsKey(player) && args.length == 1)
            {
                if (currentEvent.getCapturePoints().get(0).getName() == null)
                {
                    player.sendMessage("This event controls all respawn points.");
                    return;
                }
                for (CapturePointHandler capturePoint : currentEvent.getCapturePoints())
                {
                    if (args[0].equalsIgnoreCase(capturePoint.getName()) && capturePoint.getOwner() == currentEvent.getPlayers().get(player).getEventTeam())
                    {
                        currentEvent.getPlayers().get(player).setRespawnPoint(capturePoint);
                        player.sendMessage("Respawn point set to: " + args[0]);
                        return;
                    }
                    else if (args[0].equalsIgnoreCase(capturePoint.getName()) && capturePoint.getOwner() != currentEvent.getPlayers().get(player).getEventTeam())
                    {
                        player.sendMessage("Your team does not own this objective.");
                        return;
                    }
                }
                player.sendMessage("Objective not found.");
                return;
            }
            else if (args.length != 1) help(sender);
            else player.sendMessage("You must be in an event to use this command");
        }
        //Command to show kd
        if (cmd.getName().equalsIgnoreCase("kd") && sender instanceof Player)
        {
            Player player = (Player) sender;
            if (args.length == 0)   //Show sender's info
            {
                if (!battleblox.getKds().containsKey(player.getName())) battleblox.getKds().put(player.getName(), new KillDeath());
                player.sendMessage(battleblox.getKds().get(player.getName()).toString());
            }

            else if (args.length == 1)    //Show target info
            {
                String name = args[0];
                if (!battleblox.getKds().containsKey(name))
                {
                    player.sendMessage("Player not found, either name is incorrect or they have no data.");
                    return;
                }
                else player.sendMessage(battleblox.getKds().get(name).toString());
            }
            else help(sender);
        }
    }

    //-------------------------- Private Methods --------------------------//

    //This method sends the command sender the help information.
    private static void help(CommandSender sender)
    {
        sender.sendMessage("BattleBlox:");
        sender.sendMessage("/battleblox start [map name]:   Starts an event for the chosen map.");
        sender.sendMessage("/battleblox end:   Ends the current event.");
        sender.sendMessage("/battleblox create [map type]:   Creates an EventMap of the chosen type on your current map.");
        sender.sendMessage("/battleblox join [teamColor]:   Joins the current event on the chosen team.");
        sender.sendMessage("/battleblox commence:   Starts the current event.");
        sender.sendMessage("/battleblox confirm:   Finalizes construction of a current EventMap.");
        sender.sendMessage("/battleblox cancel:   Cancels the construction of the current EventMap");
        sender.sendMessage("/battleblox list:   Lists all current maps.");
        sender.sendMessage("/battleblox delete [map name]:   Deletes the selected map.");
        sender.sendMessage("-------------------");
        sender.sendMessage("/kd [optional: PlayerName]:   Shows player's kd info.");
    }

    //Takes over for the onCommand method and begins RushMap creation
    private static void createRushMap(Player player)
    {
        World world = player.getWorld();
        String mapName = world.getName();
        if (maps.containsKey(mapName))	//Player's map has already been constructed.
        {
            player.sendMessage("Your current map has already been created.");
            return;
        }
        equipPlayer(player);					//Give player tools needed to make maps.
        GameMap map = new RushMap(mapName);
        mapUnderConstruction = map;
        Bukkit.getServer().getPluginManager().registerEvents(map, plugin);		//This code activates the given map's listener
        player.sendMessage("Rush map creation has begun, please use the following blocks for:");
        player.sendMessage("Beacon: Placing Rush Capture Points");
        player.sendMessage("Sea Lantern: Placing teleport locations for the last placed Capture Point");
        player.sendMessage("Purpur Pillar: Placing the single map spawn location");
    }

    //Takes over for the onCommand method and begins RushMap creation
    private static void createConquestMap(Player player)
    {
        World world = player.getWorld();
        String mapName = world.getName();
        if (maps.containsKey(mapName))  //Player's map has already been constructed.
        {
            player.sendMessage("Your current map has already been created.");
            return;
        }
        equipPlayer(player);                    //Give player tools needed to make maps.
        GameMap map = new ConquestMap(mapName);
        mapUnderConstruction = map;
        Bukkit.getServer().getPluginManager().registerEvents(map, plugin);      //This code activates the given map's listener
        player.sendMessage("Conquest map creation has begun, please use the following blocks for:");
        player.sendMessage("Beacon: Placing Rush Capture Points");
        player.sendMessage("Sea Lantern: Placing teleport locations for the last placed Capture Point");
        player.sendMessage("Purpur Pillar: Placing the single map spawn location");
        player.sendMessage("You must set each objective name after you place them by doing /battleblox objective [name]");
    }

    //Takes over for the onCommand method and begins event for specified map in arguments
    private static void startEvent(Player player, String[] args)
    {
        String mapName = args[1];
        if (maps.containsKey(mapName))		//Checks if map name exists
        {
            GameHandler event;
            World world = Bukkit.getWorld(mapName);
            if (mapName.contains("RUSH")) event = new RushHandler(maps.get(mapName), player, world, battleblox);
            else if (mapName.contains("CONQUEST") && args.length == 3)
            {
                int numOfTeams = 0;
                try
                {
                    numOfTeams = Integer.parseInt(args[2]);
                }
                catch (NumberFormatException e)
                {
                    player.sendMessage("Invalid number of teams provided.");
                    return;
                }
                if (maps.get(mapName).getObjectives().size() < numOfTeams)
                {
                    player.sendMessage("Too many teams for this map.");
                    return;
                }

                event = new ConquestHandler(maps.get(mapName), player, world, numOfTeams, battleblox);

            }
            else if(mapName.contains("CONQUEST") && args.length != 3)
            {
                player.sendMessage("Invalid arguments, make sure you specified how many teams for conquest.");
                return;
            }
            else return;
            currentEvent = event;
            Bukkit.getServer().getPluginManager().registerEvents(event, plugin);
            Bukkit.broadcastMessage("Event Started!");
            Bukkit.broadcastMessage("Type '/battleblox join [Team Color]' to join.");
        }
        else player.sendMessage("Map file not found.");
    }

    //Equips the player automatically with the needed blocks to build a map.
    private static void equipPlayer(Player player)
    {
        ItemStack objective = new ItemStack(Material.BEACON);
        ItemStack teleporter = new ItemStack(Material.SEA_LANTERN);
        ItemStack spawner = new ItemStack(Material.PURPUR_PILLAR);
        ItemStack boundary = new ItemStack(Material.GLOWSTONE);

        player.setGameMode(GameMode.CREATIVE);
        player.getInventory().setItem(0, objective);		//Objective
        player.getInventory().setItem(1, teleporter);	//Teleport location
        player.getInventory().setItem(2, boundary);		//Chunk boundary
        player.getInventory().setItem(3, spawner);		//Spawn location
    }

    //Takes over for the onCommand method and handles the final creation of the current map
    private static void confirmCreateMap(Player player)
    {
        if (mapUnderConstruction.getSpawnPoint() == null) player.sendMessage("Map spawn point not set!");
        else
        {
            maps.put(mapUnderConstruction.getMapName(), mapUnderConstruction);
            mapUnderConstruction.unregisterEvents();
            mapUnderConstruction = null;
            player.sendMessage("Map construction complete!");
            FileHandler.saveMaps(maps);
        }
    }

    //Takes over for the onCommand method and cancels creation of the current map
    private static void cancelCreateMap(Player player)
    {
        mapUnderConstruction.unregisterEvents();
        mapUnderConstruction = null;
        player.sendMessage("Map construction canceled!");
    }

    //Ends the current event
    public static void endEvent()
    {
        currentEvent.endGame();
        currentEvent.unregisterEvents();
        currentEvent = null;
    }

    //Tries to put the player on the selected team in the current event
    private static void joinEvent(Player player, String teamColor)
    {
        Color color = null;

        if (teamColor.equalsIgnoreCase("Red")) color = Color.RED;
        else if (teamColor.equalsIgnoreCase("Blue")) color = Color.BLUE;
        else if (teamColor.equalsIgnoreCase("Green")) color = Color.GREEN;
        else if (teamColor.equalsIgnoreCase("Gray")) color = Color.GRAY;
        else if (teamColor.equalsIgnoreCase("PURPLE")) color = Color.PURPLE;
        else if (teamColor.equalsIgnoreCase("ORANGE")) color = Color.ORANGE;
        else if (teamColor.equalsIgnoreCase("YELLOW")) color = Color.YELLOW;
        else
        {
            player.sendMessage("Invalid team name, please choose the color corresponding with the team you wish to join.");
            return;
        }

        if (color != null && currentEvent.getTeams().containsKey(color))
        {
            if (currentEvent.getPlayers().containsKey(player))  //Player is already on a team, handle removal
            {
                currentEvent.getPlayers().get(player).getEventTeam().removePlayer(player);
                currentEvent.getPlayers().remove(player);
                if (currentEvent.getDeadPlayers().containsKey(player)) currentEvent.getDeadPlayers().remove(player);
            }
            currentEvent.getTeams().get(color).addPlayer(player);
        }
        else player.sendMessage("That team color is not available for this event.");
    }

    private static void editMap(Player player, String arg)
    {
        Integer integer;
        try
        {
            integer = Integer.parseInt(arg);
        }
        catch (Exception e)
        {
            player.sendMessage("Invalid capture point, please enter an integer.");
            return;
        }
        if (integer.intValue() <= mapUnderConstruction.getCapturePoints().size() && integer.intValue() > 0)
        {
            mapUnderConstruction.setEditPoint(integer.intValue() - 1);
            player.sendMessage("Now editing Capture Point #" + integer.intValue());
            player.sendMessage("'/battleblox edit end' when finished.");
        }
        else player.sendMessage("No such capture point exists.");
    }

    public static GameMap getMapUnderConstruction()
    {
        return mapUnderConstruction;
    }

    public static void setMapUnderConstruction(GameMap mapUnderConstruction)
    {
        CommandHandler.mapUnderConstruction = mapUnderConstruction;
    }

    public static GameHandler getCurrentEvent()
    {
        return currentEvent;
    }

    public static void setCurrentEvent(GameHandler currentEvent)
    {
        CommandHandler.currentEvent = currentEvent;
    }

    public static Plugin getPlugin()
    {
        return plugin;
    }

    public static void setPlugin(Plugin plugin)
    {
        CommandHandler.plugin = plugin;
    }

    public static HashMap<String, GameMap> getMaps()
    {
        return maps;
    }

    public static void setMaps(HashMap<String, GameMap> maps)
    {
        CommandHandler.maps = maps;
    }
}
