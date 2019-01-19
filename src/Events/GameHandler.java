//This class handles an active Map Event, utilizing a loaded GameMap object, will be of some specific type ie Rush

package Events;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.Timer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import com.codingforcookies.armorequip.ArmorEquipEvent;
import Kits.ChainKit;
import Kits.DiamondKit;
import Kits.IronKit;
import Kits.Kit;
import Kits.LeatherKit;
import Main.BattleBlox;
import Main.CommandHandler;
import Maps.GameMap;
import Objectives.CapturePointHandler;
import Other.SpawnPoint;
import Other.EventPlayer;
import Other.EventTeam;
import Other.KillDeath;
import Other.MCLocation;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;

public abstract class GameHandler implements Listener
{
    protected Map<Color, EventTeam> eventTeams = new HashMap<Color, EventTeam>();
    protected Map<String, EventTeam> eventTeamNames = new HashMap<String, EventTeam>();
    protected HashMap<Player, EventPlayer> players = new HashMap<Player, EventPlayer>();
    protected HashMap<Player, EventPlayer> deadPlayers = new HashMap<Player, EventPlayer>();
    protected HashMap<String, EventPlayer> disconnectedPlayers = new HashMap<String, EventPlayer>();
    protected GameMap map;
    protected Player eventStarter;
    protected SpawnPoint spawnPoint;
    protected List<CapturePointHandler> capturePoints = new ArrayList<CapturePointHandler>();
    protected Timer timer;
    protected Scoreboard scoreBoard;
    protected HashMap<Integer, Score> scoreObjectives = new HashMap<Integer, Score>();
    protected BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
    protected Random random = new Random();
    protected World world;
    protected BattleBlox battleblox;

    Calendar cal;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    //Used to determine/add the teams for the given event.
    abstract protected void addTeam();

    //Used to reset the map and safely end the event.
    abstract public void endGame();

    //Used to start the event, and configure it for play.
    abstract public void startGame();

    //Handles pvp combat
    abstract public void onEntityDmgPlayer(EntityDamageByEntityEvent e);

    //Handles the game's scoreboard
    public abstract void updateScoreBoard();

    //Respawns the player on the spawn block
    abstract public void onRespawn(PlayerRespawnEvent e);

    //Listens for players breaking permanent block types and cancels the event.
    abstract public void onBlockBreak(BlockBreakEvent e);

    //Listens for players placing blocks
    abstract public void onBlockPlace(BlockPlaceEvent e);

    //Handles the teleporting of players after respawn
    abstract public Location teleportPlayer(Player player, EventTeam team, CapturePointHandler cpHandler);

    public GameHandler(GameMap map, Player player, World world, BattleBlox battleblox)
    {
        this.battleblox = battleblox;
        this.map = map;
        this.eventStarter = player;
        this.world = world;
        this.spawnPoint = map.getSpawnPoint();
        scoreBoard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        //startGame();
    }

    //Sends a msg to all players in event
    public void globalMessage(String string)
    {
        for (Player player : players.keySet())
            player.sendMessage(string);
    }

    //Sends a title msg to all players in event of the given color
    public void globalTitleMessage(String string, ChatColor color)
    {
        for (Player player : players.keySet())
            player.sendTitle(color + string, "");

    }

    //Sends an actionbar msg to a given player
    public static void sendActionBarMessage(Player player, String string, ChatColor color)
    {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(string).color(color).create());
    }

    //Listens for players pressing pressure plates at a kit location
    @EventHandler
    public void onPlayerInteracts(PlayerInteractEvent e)
    {
        if (e.getAction() == Action.PHYSICAL && e.getClickedBlock().getType() == Material.GOLD_PLATE)
        {
            int x = map.getSpawnPoint().getSpawnBlock().getX();
            int y = map.getSpawnPoint().getSpawnBlock().getY();
            int z = map.getSpawnPoint().getSpawnBlock().getZ();
            Location plate = e.getClickedBlock().getLocation();
            if (plate.equals(MCLocation.getBlockLocationObject(x + 9, y + 1, z - 8, world))) players.get(e.getPlayer()).setKit(new ChainKit(e.getPlayer()));
            else if (plate.equals(MCLocation.getBlockLocationObject(x + 9, y + 1, z - 3, world))) players.get(e.getPlayer()).setKit(new LeatherKit(e.getPlayer()));
            else if (plate.equals(MCLocation.getBlockLocationObject(x + 9, y + 1, z + 3, world))) players.get(e.getPlayer()).setKit(new IronKit(e.getPlayer()));
            else if (plate.equals(MCLocation.getBlockLocationObject(x + 9, y + 1, z + 8, world))) players.get(e.getPlayer()).setKit(new DiamondKit(e.getPlayer()));
        }
    }

    //Listens for player armor changes, and clears/adds potion effects as needed
    @EventHandler
    public void onArmorEquip(ArmorEquipEvent e)
    {
        Player player = e.getPlayer();

        if (e.getOldArmorPiece() != null && e.getOldArmorPiece().getType() != Material.AIR) Kit.clearArmorEffects(player);
        else if (e.getNewArmorPiece() != null && e.getNewArmorPiece().getType() != Material.AIR) scheduler.scheduleSyncDelayedTask(CommandHandler.getPlugin(), new Runnable()
        {
            @Override
            public void run()
            {
                if(players.get(player) != null) players.get(player).getKit().getArmorEffects(player);
            }
        }, 2L);

    }

    //Listens for players disconnecting mid event.
    @EventHandler
    public void onLogoff(PlayerQuitEvent e)
    {
        Player player = e.getPlayer();
        if (!players.containsKey(player)) return;
        Bukkit.getServer().getLogger().info(player.getName() + " disconnected");
        EventPlayer eventPlayer = players.get(player);
        disconnectedPlayers.put(player.getName(), eventPlayer);
        players.remove(player);
        Bukkit.getServer().getLogger().info(player.getName() + " removed from list of event players");
        if (deadPlayers.containsKey(player))
        {
            eventPlayer.setWasDead(true);
            deadPlayers.remove(player);
            Bukkit.getServer().getLogger().info(player.getName() + " removed from dead players");
        }
        eventPlayer.getEventTeam().removePlayer(player);
        Bukkit.getServer().getLogger().info(player.getName() + " removed from his team");
    }

    //Listens for players reconnecting mid event
    @EventHandler
    public void onLogIn(PlayerJoinEvent e)
    {
        Player player = e.getPlayer();
        if (!disconnectedPlayers.containsKey(player.getName()))
        {
            Bukkit.getServer().getLogger().info(player.getName() + " not found in disconnected players");
            return;
        }

        EventPlayer eventPlayer = disconnectedPlayers.get(player.getName());
        eventPlayer.getEventTeam().addPlayer(player);
        Bukkit.getServer().getLogger().info(player.getName() + " added bk to his team");
        disconnectedPlayers.remove(player.getName());
        Bukkit.getServer().getLogger().info(player.getName() + " removed from disconnected players");
    }

    //Handles Death of players by preventing kit drop
    @EventHandler
    public void onDeath(PlayerDeathEvent e)
    {
        if (players.containsKey(e.getEntity()))
        {
            e.setKeepInventory(true);
            e.getEntity().getInventory().clear();
        }

        //Code for handling KDs
        //Deaths
        String deadName = e.getEntity().getName();

        if (!battleblox.getKds().containsKey(deadName)) battleblox.getKds().put(deadName, new KillDeath());

        //Current event is rush
        if (CommandHandler.getCurrentEvent() instanceof RushHandler)
        {
            RushHandler rHandler = (RushHandler) CommandHandler.getCurrentEvent();

            if(rHandler.players.get(e.getEntity()) == null) return;
            if (rHandler.players.get(e.getEntity()).getEventTeam().getName().equalsIgnoreCase("ATTACKERS")) battleblox.getKds().get(deadName).incrementRushAttackerDeaths();
            else if (rHandler.players.get(e.getEntity()).getEventTeam().getName().equalsIgnoreCase("DEFENDERS")) battleblox.getKds().get(deadName).incrementRushDefenderDeaths();
        }
        //Current event is Conquest
        else if (CommandHandler.getCurrentEvent() instanceof ConquestHandler)
        {
            ConquestHandler cHandler = (ConquestHandler) CommandHandler.getCurrentEvent();
            
            if(cHandler.players.get(e.getEntity()) == null) return;
            battleblox.getKds().get(deadName).incrementConquestDeaths();
            //Reduce tickets by 20
            Color color = cHandler.getPlayers().get(e.getEntity()).getEventTeam().getColor();
            cHandler.getTickets().replace(color, cHandler.getTickets().get(color)-20);

        }
        battleblox.getKds().get(deadName).incrementTotalDeaths();
        //---------------------------------------//
        //Kills
        if (e.getEntity().getKiller() == null) return;
        String killerName = e.getEntity().getKiller().getName();
        if (killerName == null) return;

        if (!battleblox.getKds().containsKey(killerName)) battleblox.getKds().put(killerName, new KillDeath());

        //Current event is rush
        if (CommandHandler.getCurrentEvent() instanceof RushHandler)
        {
            RushHandler rHandler = (RushHandler) CommandHandler.getCurrentEvent();

            if (rHandler.players.get(e.getEntity().getKiller()).getEventTeam().getName().equalsIgnoreCase("ATTACKERS"))
                battleblox.getKds().get(killerName).incrementRushAttackerKills();
            else if (rHandler.players.get(e.getEntity().getKiller()).getEventTeam().getName().equalsIgnoreCase("DEFENDERS"))
                battleblox.getKds().get(killerName).incrementRushDefenderKills();
        }
        //Current event is Conquest
        else if (CommandHandler.getCurrentEvent() instanceof ConquestHandler) battleblox.getKds().get(killerName).incrementConquestKills();
        battleblox.getKds().get(killerName).incrementTotalKills();
    }

    //Disables this map's event listener if it has one.
    public void unregisterEvents()
    {
        HandlerList.unregisterAll(this);
    }

    public Map<Color, EventTeam> getTeams()
    {
        return eventTeams;
    }

    public void setTeams(Map<Color, EventTeam> eventTeams)
    {
        this.eventTeams = eventTeams;
    }

    public HashMap<Player, EventPlayer> getPlayers()
    {
        return players;
    }

    public void setPlayers(HashMap<Player, EventPlayer> players)
    {
        this.players = players;
    }

    public GameMap getMap()
    {
        return map;
    }

    public void setMap(GameMap map)
    {
        this.map = map;
    }

    public Player getEventStarter()
    {
        return eventStarter;
    }

    public void setEventStarter(Player eventStarter)
    {
        this.eventStarter = eventStarter;
    }

    public SpawnPoint getSpawnPoint()
    {
        return spawnPoint;
    }

    public void setSpawnPoint(SpawnPoint spawnPoint)
    {
        this.spawnPoint = spawnPoint;
    }

    public List<CapturePointHandler> getCapturePoints()
    {
        return capturePoints;
    }

    public void setCapturePoints(List<CapturePointHandler> capturePoints)
    {
        this.capturePoints = capturePoints;
    }

    public Timer getTimer()
    {
        return timer;
    }

    public void setTimer(Timer timer)
    {
        this.timer = timer;
    }

    public Scoreboard getScoreBoard()
    {
        return scoreBoard;
    }

    public void setScoreBoard(Scoreboard scoreBoard)
    {
        this.scoreBoard = scoreBoard;
    }

    public Map<Color, EventTeam> getEventTeams()
    {
        return eventTeams;
    }

    public void setEventTeams(Map<Color, EventTeam> eventTeams)
    {
        this.eventTeams = eventTeams;
    }

    public HashMap<Player, EventPlayer> getDeadPlayers()
    {
        return deadPlayers;
    }

    public void setDeadPlayers(HashMap<Player, EventPlayer> deadPlayers)
    {
        this.deadPlayers = deadPlayers;
    }

    public HashMap<Integer, Score> getScoreObjectives()
    {
        return scoreObjectives;
    }

    public void setScoreObjectives(HashMap<Integer, Score> scoreObjectives)
    {
        this.scoreObjectives = scoreObjectives;
    }

    public BukkitScheduler getScheduler()
    {
        return scheduler;
    }

    public void setScheduler(BukkitScheduler scheduler)
    {
        this.scheduler = scheduler;
    }

    public Random getRandom()
    {
        return random;
    }

    public void setRandom(Random random)
    {
        this.random = random;
    }

    public Map<String, EventTeam> getEventTeamNames()
    {
        return eventTeamNames;
    }

    public void setEventTeamNames(Map<String, EventTeam> eventTeamNames)
    {
        this.eventTeamNames = eventTeamNames;
    }

}
