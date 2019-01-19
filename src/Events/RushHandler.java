//This class handles an active Rush Map Event, utilizing a loaded Rush Map object

package Events;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import Main.BattleBlox;
import Main.CommandHandler;
import Main.FileHandler;
import Maps.GameMap;
import Objectives.Boundary;
import Objectives.CapturePointHandler;
import Objectives.RushPointHandler;
import Other.Combat;
import Other.EventPlayer;
import Other.EventTeam;
import net.md_5.bungee.api.ChatColor;

public class RushHandler extends GameHandler implements Listener
{

    protected RushPointHandler attackableRushPoint;	//Also serves as the defender rush spawn point
    protected RushPointHandler attackerTeleportPoint;
    protected RushPointHandler defenderTeleportPoint;
    protected boolean startSpawn = true;

    public RushHandler(GameMap map, Player player, World world, BattleBlox battleblox)
    {
        super(map, player, world, battleblox);
        addTeam();
        for (int i = 0; i < map.getObjectives().size(); i++)    //Adds capture points to the event
        {
            CapturePointHandler capturePoint;
            if (i == 0) capturePoint = new RushPointHandler(map.getObjectives().get(i), eventTeams.get(Color.RED), this);
            else capturePoint = new RushPointHandler(map.getObjectives().get(i), eventTeams.get(Color.BLUE), this);
            capturePoints.add(capturePoint);
        }
        attackableRushPoint = (RushPointHandler) capturePoints.get(1);  //Defaults the first attackable objective to the first index in objectives
        attackerTeleportPoint = (RushPointHandler) capturePoints.get(capturePoints.indexOf(attackableRushPoint) - 1);
        defenderTeleportPoint = attackableRushPoint;
        attackableRushPoint.enableLight();
        updateScoreBoard();
    }

    //Adds the attacking (red) and defending (blue) teams to the event.
    @Override
    protected void addTeam()
    {
        scoreBoard.registerNewTeam("Attackers");
        scoreBoard.registerNewTeam("Neutral");
        scoreBoard.registerNewTeam("Defenders");
        scoreBoard.getTeam("Attackers").setPrefix(ChatColor.RED.toString());
        scoreBoard.getTeam("Defenders").setPrefix(ChatColor.BLUE.toString());
        eventTeams.put(Color.RED, new EventTeam(Color.RED, this, scoreBoard.getTeam("Attackers")));
        eventTeams.put(Color.BLUE, new EventTeam(Color.BLUE, this, scoreBoard.getTeam("Defenders")));
        eventTeams.put(Color.WHITE, new EventTeam(Color.WHITE, this, scoreBoard.getTeam("Neutral")));
        eventTeams.get(Color.RED).setName("ATTACKERS");
        eventTeams.get(Color.BLUE).setName("DEFENDERS");
        eventTeams.get(Color.WHITE).setName("NEUTRAL");
        eventTeamNames.put("Attackers", eventTeams.get(Color.RED));
        eventTeamNames.put("Neutral", eventTeams.get(Color.WHITE));
        eventTeamNames.put("Defenders", eventTeams.get(Color.BLUE));
        eventStarter.sendMessage(eventTeams.size() + " teams added.");
    }

    //Starts the game and game timer
    @Override
    public void startGame()
    {

        globalTitleMessage("Game Start!", ChatColor.GOLD);
        cal = Calendar.getInstance();
        for (Player player : players.keySet())
        {

            //respawnPlayer(player);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 0);
        }

        //Scheduler/Timer every 1 second
        scheduler.scheduleSyncRepeatingTask(CommandHandler.getPlugin(), new Runnable()
        {
            @Override
            public void run()
            {
                if (attackableRushPoint.isBeingCaptured() == false && !attackableRushPoint.checkCapturable().equalsIgnoreCase("false")
                        && !attackableRushPoint.checkCapturable().equalsIgnoreCase("deadlocked"))
                {
                    attackableRushPoint.setBeingCaptured(true);
                    globalTitleMessage("Objective: " + (capturePoints.indexOf(attackableRushPoint) + 1) + " is under attack!", ChatColor.BLUE);
                }
                //Checks if rush point should be being captured
                if (attackableRushPoint.isBeingCaptured() == true)
                {
                    attackableRushPoint.updateCapture();
                }

                //Handles respawning of players
                List<Player> removePlayers = new ArrayList<Player>();
                removePlayers.addAll(deadPlayers.keySet());
                for (Player player : removePlayers)
                {
                    EventPlayer eventPlayer = deadPlayers.get(player);
                    if (startSpawn && eventPlayer.getEventTeam().getColor() == Color.BLUE)
                    {
                        eventPlayer.setRespawnTimer(eventPlayer.getRespawnTimerDefault() / 2);
                    }
                    if (eventPlayer.getRespawnTimer() > 0)
                    {
                        eventPlayer.setRespawnTimer(eventPlayer.getRespawnTimer() - 1);
                        sendActionBarMessage(eventPlayer.getPlayer(), "Respawn Timer: " + eventPlayer.getRespawnTimer(), ChatColor.RED);
                    }
                    else //Try and respawn player
                    {
                        deadPlayers.remove(player);
                        respawnPlayer(player);
                    }
                }
                if (startSpawn) startSpawn = false;
            }
        }, 0, 20L);
    }

    //Ends the current game gracefully
    @Override
    public void endGame()
    {
        scheduler.cancelAllTasks();
        scoreBoard = null;
        globalTitleMessage("Game Over!", ChatColor.GOLD);

        //Handle Capture Points
        for (CapturePointHandler rushPoint : capturePoints) //Resets each rush point
        {
            rushPoint.reset();
        }
        //Handle Players
        for (Player player : players.keySet())
        {

            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 0);
            player.setDisplayName(player.getName());
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            player.loadData();
        }

        if (cal != null)
        {
            String worldName = world.getName();
            String time = sdf.format(cal.getTime());
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "lb rollback world \"" + worldName + "\" since \"" + time + "\" ");
        }
        FileHandler.saveKD(battleblox.getKds());
    }

    //Listens for players breaking permanent block types and cancels the event.
    @EventHandler
    @Override
    public void onBlockBreak(BlockBreakEvent e)
    {
        Block block = e.getBlock();

        if (block.getType() == Material.BEACON || block.getType() == Material.STAINED_GLASS || block.getType() == Material.SEA_LANTERN || block.getType() == Material.GOLD_BLOCK
                || block.getType() == Material.PURPUR_PILLAR || block.getType() == Material.GOLD_PLATE || block.getType() == Material.ARMOR_STAND)
        {
            e.setCancelled(true);
            e.getPlayer().sendMessage("This block is not breakable during this event.");
            return;
        }

        if (players.containsKey(e.getPlayer()) && players.get(e.getPlayer()).getEventTeam().getColor() != Color.BLUE) e.getPlayer().damage(1.0);
    }

    //Listens for players placing blocks
    @EventHandler
    @Override
    public void onBlockPlace(BlockPlaceEvent e)
    {
        if (players.containsKey(e.getPlayer()) && players.get(e.getPlayer()).getEventTeam().getColor() != Color.BLUE) e.getPlayer().damage(1.0);
    }

    //Handles pvp combat
    @EventHandler
    @Override
    public void onEntityDmgPlayer(EntityDamageByEntityEvent e)
    {
        if (e.getEntity() instanceof Player == false) return;	//who cares since a non player was damaged
        Player damagee = (Player) e.getEntity();
        Player damager = null;
        if (e.getDamager() instanceof Arrow)    //Player shot by an arrow
        {

            Arrow arrow = (Arrow) e.getDamager();
            if (arrow.getShooter() instanceof Player)
            {
                damager = (Player) arrow.getShooter();
                Combat.checkHeadShot(arrow, damagee, damager);
            }    //Arrow was shot by a player
            else return;
        }
        else if (e.getDamager() instanceof Player) damager = (Player) e.getDamager(); //Player was hurt by another player
        else return;    //Player was hurt by a non player or arrow, who cares.

        //Check if damagee is blocking, and damager is duel-wielding, set 5s cooldown for damagee shield
        /*if (damagee.isHandRaised() && damagee.isBlocking() && damager.getInventory().getItemInMainHand().getType() == Material.DIAMOND_SWORD
                && damager.getInventory().getItemInOffHand().getType() == Material.DIAMOND_AXE)
        {
            damagee.setCooldown(damagee.getInventory().getItemInOffHand().getType(), 100);
            globalMessage(damagee.isHandRaised()+"");
        }*/

        if (players.containsKey(damager) && players.get(damager).getEventTeam() == players.get(damagee).getEventTeam())	//Damager and damagee r same team
        {
            damager.sendMessage(damagee.getName() + " is on your team!");
            e.setCancelled(true);
        }
        if (deadPlayers.containsKey(damager) || deadPlayers.containsKey(damagee))
        {
            damager.sendMessage("Fighting is not allowed in the spawn area!");
            e.setCancelled(true);
        }
    }

    //Handles Respawns and teleporting
    @EventHandler
    @Override
    public void onRespawn(PlayerRespawnEvent e)
    {
        if (!players.keySet().contains(e.getPlayer())) return;  //If player is not in the event, who cares where he spawns.
        e.setRespawnLocation(map.getSpawnPoint().getSpawnBlock().getLocationObject().add(0, 2, 0));
        deadPlayers.put(e.getPlayer(), players.get(e.getPlayer()));
    }

    //Stops player movement event
    @EventHandler
    public void onPlayerMoves(PlayerMoveEvent e)
    {
        //if player is in game, and player is on red team
        if (players.containsKey(e.getPlayer()) && players.get(e.getPlayer()).getEventTeam().getColor() == Color.RED)
        {
            for (Boundary boundary : attackableRushPoint.getBoundaries())
            {
                if (e.getTo().getChunk() == boundary.getChunk()) e.setCancelled(true);
                sendActionBarMessage(e.getPlayer(), "This area is off limits!", ChatColor.RED);
            }
        }
    }

    //Starts the rush map's scoreboard
    @Override
    public void updateScoreBoard()
    {
        if (scoreBoard.getObjective(DisplaySlot.SIDEBAR) != null) scoreBoard.getObjective(DisplaySlot.SIDEBAR).unregister();
        Objective objective = scoreBoard.registerNewObjective("Objectives", "dummy");	//Add objective
        objective.setDisplayName("Objectives:");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        for (int i = 0; i < capturePoints.size(); i++)		//For each capture point, create score on right side.
        {
            if (capturePoints.get(i).getOwner().getName().equalsIgnoreCase("attackers"))
            {
                scoreObjectives.put(i, objective.getScore(ChatColor.RED + "Capture Point " + (i + 1) + ": Attackers"));
            }
            else if (capturePoints.get(i).getOwner().getName().equalsIgnoreCase("neutral"))
            {
                scoreObjectives.put(i, objective.getScore(ChatColor.WHITE + "Capture Point " + (i + 1) + ": Neutral"));
            }
            else if (capturePoints.get(i).getOwner().getName().equalsIgnoreCase("defenders"))
            {
                scoreObjectives.put(i, objective.getScore(ChatColor.BLUE + "Capture Point " + (i + 1) + ": Defenders"));
            }
            scoreObjectives.get(i).setScore(capturePoints.size() - i);
        }
    }

    //Handles the teleporting of players after respawn
    @Override
    public Location teleportPlayer(Player player, EventTeam team, CapturePointHandler cpHandler)
    {
        int randomIndex = random.nextInt(cpHandler.getTeleporters().size());
        World world = cpHandler.getTeleporters().get(randomIndex).getWorld();
        double x = cpHandler.getTeleporters().get(randomIndex).getX();
        double y = cpHandler.getTeleporters().get(randomIndex).getY() + 1;
        double z = cpHandler.getTeleporters().get(randomIndex).getZ();

        return new Location(world, x + .5, y, z + .5);

    }

    //Tries to respawn any players that were denied due to all spawnpoints being unavailable.
    private void respawnPlayer(Player player)
    {
        if (players.get(player).getEventTeam().getColor() == Color.RED)
        {
            player.teleport(teleportPlayer(player.getPlayer(), players.get(player).getEventTeam(), attackerTeleportPoint));
            players.get(player).setRespawnTimer(players.get(player).getRespawnTimerDefault());
        }

        else if (players.get(player).getEventTeam().getColor() == Color.BLUE && defenderTeleportPoint != null)
        {
            player.teleport(teleportPlayer(player.getPlayer(), players.get(player).getEventTeam(), defenderTeleportPoint));
            players.get(player).setRespawnTimer(players.get(player).getRespawnTimerDefault());
        }
    }

    public RushPointHandler getAttackableRushPoint()
    {
        return attackableRushPoint;
    }

    public void setAttackableRushPoint(RushPointHandler attackableRushPoint)
    {
        this.attackableRushPoint = attackableRushPoint;
        attackableRushPoint.enableLight();
    }

    public RushPointHandler getAttackerTeleportPoint()
    {
        return attackerTeleportPoint;
    }

    public void setAttackerTeleportPoint(RushPointHandler attackerTeleportPoint)
    {
        this.attackerTeleportPoint = attackerTeleportPoint;
    }

    public RushPointHandler getDefenderTeleportPoint()
    {
        return defenderTeleportPoint;
    }

    public void setDefenderTeleportPoint(RushPointHandler defenderTeleportPoint)
    {
        this.defenderTeleportPoint = defenderTeleportPoint;
    }

    public void setDefenderTeleportPoint(String s)
    {
        if (s.equals("null")) this.defenderTeleportPoint = null;
    }

}
