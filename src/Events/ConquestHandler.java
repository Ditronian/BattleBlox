package Events;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import Main.BattleBlox;
import Main.CommandHandler;
import Main.FileHandler;
import Maps.ConquestMap;
import Maps.GameMap;
import Objectives.CapturePointHandler;
import Objectives.ConquestPoint;
import Objectives.ConquestPointHandler;
import Other.Combat;
import Other.EventPlayer;
import Other.EventTeam;
import net.md_5.bungee.api.ChatColor;

public class ConquestHandler extends GameHandler
{
    protected HashMap<Color, Integer> tickets = new HashMap<Color, Integer>();
    protected int startTickets;
    protected boolean start = false;
    protected HashMap<Color, ArrayList<ConquestPointHandler>> teamPoints = new HashMap<Color, ArrayList<ConquestPointHandler>>();
    protected HashMap<Color, Score> scoreTickets = new HashMap<Color, Score>();
    protected List<ConquestPointHandler> conquestPoints = new ArrayList<ConquestPointHandler>();
    protected int numOfTeams;

    public ConquestHandler(GameMap map, Player player, World world, int numOfTeams, BattleBlox battleblox)
    {
        super(map, player, world, battleblox);
        ConquestMap cMap = (ConquestMap) map;
        this.numOfTeams = numOfTeams;
        addTeam();
        ArrayList<ConquestPointHandler> tempPoints = new ArrayList<ConquestPointHandler>();
        for (int i = 0; i < cMap.getObjectives().size(); i++)    //Adds conquest points to the event
        {
            ConquestPoint cPoint = (ConquestPoint) map.getObjectives().get(i);
            ConquestPointHandler CPHandler = new ConquestPointHandler(map.getObjectives().get(i), cPoint.getName(), eventTeams.get(Color.WHITE), this);
            capturePoints.add(CPHandler);
            conquestPoints.add(CPHandler);
            tempPoints.add(CPHandler);
        }

        //Foreach team, assign it a random obj, set that obj to owned, add it to team's list of owned objs, and remove from temp list so cant be chosen again.
        Random random = new Random();

        for (EventTeam team : eventTeams.values())
        {
            if (team.getColor() == Color.WHITE) continue;
            int r = random.nextInt(tempPoints.size());
            tempPoints.get(r).setCPOwner(team);
            teamPoints.get(team.getColor()).add(tempPoints.get(r));
            tempPoints.remove(r);
        }
        //Turn on all lights
        for (CapturePointHandler capturePoint : capturePoints)
            capturePoint.enableLight();
        updateScoreBoard();
    }

    //Adds the teams to the event.
    @Override
    protected void addTeam()
    {
        List<String> teams = new ArrayList<String>();
        teams.add("Neutral");
        teams.add("Red");
        teams.add("Blue");
        teams.add("Green");
        teams.add("Gray");
        teams.add("Purple");
        teams.add("Orange");
        teams.add("Yellow");

        //Foreach team
        for (int i = 0; i < numOfTeams + 1; i++)
        {
            String string = teams.get(i);
            Color color = EventTeam.getColor(teams.get(i));
            ChatColor chatColor = EventTeam.getChatColor(teams.get(i));

            scoreBoard.registerNewTeam(string);
            scoreBoard.getTeam(string).setPrefix(chatColor.toString());
            eventTeams.put(color, new EventTeam(color, this, scoreBoard.getTeam(string)));
            eventTeams.get(color).setName(string);
            eventTeamNames.put(string, eventTeams.get(color));
            teamPoints.put(color, new ArrayList<ConquestPointHandler>());

        }
        List<String> msg = teams.subList(1, numOfTeams + 1);
        eventStarter.sendMessage(msg.toString() + " teams added.");

    }

    //Starts the game and game timer
    @Override
    public void startGame()
    {
        start = true;
        globalTitleMessage("Game Start!", ChatColor.GOLD);
        cal = Calendar.getInstance();
        for (Player player : players.keySet())
        {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 0);
        }

        calculateTickets();

        //Scheduler/Timer every 1 second
        scheduler.scheduleSyncRepeatingTask(CommandHandler.getPlugin(), new Runnable()
        {
            @Override
            public void run()
            {
                //Foreach capture point, check if is being captured, and handle
                for (CapturePointHandler capturePoint : capturePoints)
                {
                    ConquestPointHandler conquestPoint = (ConquestPointHandler) capturePoint;
                    if (capturePoint.isBeingCaptured() == false && !capturePoint.checkCapturable().equalsIgnoreCase("false")
                            && !capturePoint.checkCapturable().equalsIgnoreCase("deadlocked"))
                    {
                        capturePoint.setBeingCaptured(true);
                        globalTitleMessage(conquestPoint.getName() + " is under attack!", capturePoint.getOwner().getChatColor());
                    }
                    //Checks if conquest point should be being captured
                    if (capturePoint.isBeingCaptured() == true)
                    {
                        capturePoint.updateCapture();
                    }
                }

                //Handles respawning of players
                List<Player> removePlayers = new ArrayList<Player>();
                removePlayers.addAll(deadPlayers.keySet());
                for (Player player : removePlayers)
                {
                    EventPlayer eventPlayer = deadPlayers.get(player);
                    if (eventPlayer.getRespawnTimer() > 0)
                    {
                        eventPlayer.setRespawnTimer(eventPlayer.getRespawnTimer() - 1);
                        sendActionBarMessage(eventPlayer.getPlayer(), "Respawn Timer: " + eventPlayer.getRespawnTimer(), ChatColor.RED);
                    }
                    //Try and respawn player
                    else if (eventPlayer.getRespawnPoint() != null && eventPlayer.getRespawnPoint().getOwner() == eventPlayer.getEventTeam())
                    {
                        deadPlayers.remove(player);
                        respawnPlayer(player, eventPlayer.getRespawnPoint());
                    }
                    else if (eventPlayer.getRespawnPoint() == null)
                    {
                        sendActionBarMessage(eventPlayer.getPlayer(), "You have not selected a valid respawn point.", ChatColor.RED);
                    }
                    else if (eventPlayer.getRespawnPoint().getOwner() != eventPlayer.getEventTeam())
                    {
                        sendActionBarMessage(eventPlayer.getPlayer(), "Your selected respawn point has been lost.", ChatColor.RED);
                    }
                }

                updateTickets();
            }
        }, 0, 20L);
    }

    //Ends the current game gracefully
    @Override
    public void endGame()
    {
        scheduler.cancelAllTasks();
        scoreBoard = null;
        ArrayList<String> winners = new ArrayList<String>();
        //If a team has no objectives, or is defeated they lose
        for (Color color : tickets.keySet())
        {
            EventTeam team = eventTeams.get(color);
            if (!team.isDefeated() && teamPoints.get(color).size() > 0) winners.add(team.getName());
        }

        if(winners.size() == 0) globalTitleMessage("Game Over!", ChatColor.GOLD);
        else if(winners.size() == 1) globalTitleMessage(winners.get(0)+"Team is victorious!", EventTeam.getChatColor(winners.get(0)));
        else if(winners.size() > 1) globalTitleMessage(winners.toString() +"teams are victorious!", ChatColor.GOLD);
        

        //Handle Capture Points
        for (CapturePointHandler conquestPoint : capturePoints) //Resets each rush point
        {
            conquestPoint.reset();
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
    }

    //Handles pvp combat
    @EventHandler
    @Override
    public void onEntityDmgPlayer(EntityDamageByEntityEvent e)
    {
        if (e.getEntity() instanceof Player == false) return;   //who cares since a non player was damaged
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

        if (players.containsKey(damager) && players.get(damager).getEventTeam() == players.get(damagee).getEventTeam()) //Damager and damagee r same team
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

    //Starts the conquest map's scoreboard
    @Override
    public void updateScoreBoard()
    {
        if (scoreBoard.getObjective(DisplaySlot.SIDEBAR) != null) scoreBoard.getObjective(DisplaySlot.SIDEBAR).unregister();

        //Handle objectives
        Objective objective = scoreBoard.registerNewObjective("Objectives", "dummy");   //Add objective
        objective.setDisplayName("Objectives:");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        int counter = 0;

        //Handle Tickets, ONLY if have started.  Otherwise won't have players so cant calculate tickets
        if (start)
        {
            for (Color team : tickets.keySet())
            {
                int t = tickets.get(team);
                String name = eventTeams.get(team).getName();

                scoreTickets.put(team, objective.getScore(EventTeam.getChatColor(name) + name + ": " + t + "/" + startTickets));
                scoreTickets.get(team).setScore(capturePoints.size() + tickets.size() - counter);
                counter++;
            }
        }
        for (int i = 0; i < conquestPoints.size(); i++)      //For each capture point, create score on right side.
        {
            scoreObjectives.put(i,
                    objective.getScore(conquestPoints.get(i).getOwner().getChatColor() + conquestPoints.get(i).getName() + ": " + conquestPoints.get(i).getOwner().getName()));
            scoreObjectives.get(i).setScore(capturePoints.size() + tickets.size() - counter);
            counter++;
        }
    }

    //Handles the teleporting of players after respawn
    @Override
    public Location teleportPlayer(Player player, EventTeam team, CapturePointHandler cpHandler)
    {
        int randomTeleporter = random.nextInt(cpHandler.getTeleporters().size());

        World world = cpHandler.getTeleporters().get(randomTeleporter).getWorld();
        double x = cpHandler.getTeleporters().get(randomTeleporter).getX();
        double y = cpHandler.getTeleporters().get(randomTeleporter).getY() + 1;
        double z = cpHandler.getTeleporters().get(randomTeleporter).getZ();

        return new Location(world, x + .5, y, z + .5);

    }

    //Sets up the game's tickets
    private void calculateTickets()
    {
        startTickets = (players.size() * 100) + (capturePoints.size() * 200);
        for (Color team : eventTeams.keySet())
        {
            if (team == Color.WHITE) continue;
            tickets.put(team, startTickets);
        }
        updateScoreBoard();
    }

    //Update's the game's tickets
    private void updateTickets()
    {
        int maxObjectives = 0;
        boolean tie = false;

        //Get the max, and check for ties
        for (Color tempTeam : tickets.keySet())
        {
            int ownedObjectives = teamPoints.get(tempTeam).size();
            if (ownedObjectives > maxObjectives)
            {
                maxObjectives = ownedObjectives;
                tie = false;
            }
            else if (ownedObjectives == maxObjectives) tie = true;
        }

        //Update tickets
        for (Color tempTeam : tickets.keySet())
        {
            int currentTickets = tickets.get(tempTeam);
            int difference = teamPoints.get(tempTeam).size() - maxObjectives;
            if (!tie && teamPoints.get(tempTeam).size() == maxObjectives) difference++;

            int newTickets = currentTickets - 1 + difference;
            tickets.replace(tempTeam, newTickets);
            if (newTickets < 0) teamDefeated(tempTeam);
        }
        if (scoreBoard != null) updateScoreBoard();
    }

    //Removes the defeated team from the game
    private void teamDefeated(Color color)
    {
        EventTeam team = eventTeams.get(color);
        for (ConquestPointHandler cHandler : teamPoints.get(color))
        {
            cHandler.setCPOwner(eventTeams.get(Color.WHITE));
        }
        for (Player player : team.getPlayers())
        {
            player.teleport(map.getSpawnPoint().getSpawnBlock().getLocationObject().add(0, 2, 0));
            deadPlayers.put(player, players.get(player));
        }
        team.setDefeated(true);
        tickets.remove(color);
        //Check for end of game
        int defeatCount = 0;
        for (EventTeam eTeam : eventTeams.values())
        {
            if (eTeam.isDefeated()) defeatCount++;
        }
        if (defeatCount >= tickets.size() - 1) CommandHandler.endEvent();
    }

    //Tries to respawn any players that were denied due to all spawnpoints being unavailable.
    private void respawnPlayer(Player player, CapturePointHandler cpHandler)
    {
        if (cpHandler == null) return;

        player.teleport(teleportPlayer(player.getPlayer(), players.get(player).getEventTeam(), cpHandler));
        players.get(player).setRespawnTimer(players.get(player).getRespawnTimerDefault());
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent e)
    {
        // Do nothing       
    }

    public HashMap<Color, ArrayList<ConquestPointHandler>> getTeamPoints()
    {
        return teamPoints;
    }

    public void setTeamPoints(HashMap<Color, ArrayList<ConquestPointHandler>> teamPoints)
    {
        this.teamPoints = teamPoints;
    }

    public List<ConquestPointHandler> getConquestPoints()
    {
        return conquestPoints;
    }

    public void setConquestPoints(List<ConquestPointHandler> conquestPoints)
    {
        this.conquestPoints = conquestPoints;
    }

    public HashMap<Color, Integer> getTickets()
    {
        return tickets;
    }

    public void setTickets(HashMap<Color, Integer> tickets)
    {
        this.tickets = tickets;
    }

    public int getStartTickets()
    {
        return startTickets;
    }

    public void setStartTickets(int startTickets)
    {
        this.startTickets = startTickets;
    }

    public boolean isStart()
    {
        return start;
    }

    public void setStart(boolean start)
    {
        this.start = start;
    }

    public HashMap<Color, Score> getScoreTickets()
    {
        return scoreTickets;
    }

    public void setScoreTickets(HashMap<Color, Score> scoreTickets)
    {
        this.scoreTickets = scoreTickets;
    }

    public int getNumOfTeams()
    {
        return numOfTeams;
    }

    public void setNumOfTeams(int numOfTeams)
    {
        this.numOfTeams = numOfTeams;
    }

}
