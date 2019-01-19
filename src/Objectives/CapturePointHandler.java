//Handles the Capturing/Status of all loaded Capture Points in a given GameHandler, will have a specific type related to event type, ie RushPointHandler

package Objectives;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import Events.GameHandler;
import Other.EventTeam;
import Other.MCLocation;
import net.md_5.bungee.api.ChatColor;

public abstract class CapturePointHandler
{
    protected CapturePoint capturePoint;
    protected EventTeam owner;
    protected EventTeam capturingTeam;
    protected EventTeam previousOwner;
    protected Block ownerBlock;
    protected Block controlBlock;
    protected Block objectiveBlock;
    protected List<Location> teleporters = new ArrayList<Location>();
    protected List<Boundary> boundaries = new ArrayList<Boundary>();
    protected GameHandler gameHandler;
    protected boolean isDim;
    protected boolean isNeutral;
    protected boolean beingCaptured = false;
    protected int captureTimerDefault;
    protected int captureTimer;
    protected String name;

    //Resets the objective to its default, pre-match state.
    abstract public void reset();

    //Handles the capturing of an objective if updateCapture() deems it necessary
    protected abstract void captureObjective(EventTeam capturing, EventTeam losing);

    //Switches objective being captured to neutral
    protected abstract void setNeutral();

    //Switches objective being captured back to original owner
    protected abstract void removeNeutral();

    public CapturePointHandler(CapturePoint capturePoint, EventTeam owner)
    {
        this.capturePoint = capturePoint;
        this.owner = owner;
        this.objectiveBlock = capturePoint.getObjectiveBlockLocation().getLocationObject().getBlock();
        controlBlock = this.objectiveBlock.getLocation().subtract(0, 1, 0).getBlock();		//find the block below the placed beacon
        ownerBlock = this.objectiveBlock.getLocation().add(0, 1, 0).getBlock();		//find the block above the placed beacon
        for (MCLocation teleporterLocation : capturePoint.getTeleporters())
        {
            teleporters.add(teleporterLocation.getLocationObject());
        }

        for (Boundary boundaryLocation : capturePoint.getBoundaries())
        {
            boundaries.add(boundaryLocation);
        }
    }

    //Sets the owner of the objective and sets it's control block to the correct color
    @SuppressWarnings("deprecation")
    public void setCPOwner(EventTeam owner)
    {
        if (owner.getColor() == Color.BLUE) ownerBlock.setData(DyeColor.BLUE.getWoolData());
        else if (owner.getColor() == Color.RED) ownerBlock.setData(DyeColor.RED.getWoolData());
        else if (owner.getColor() == Color.GREEN) ownerBlock.setData(DyeColor.GREEN.getWoolData());
        else if (owner.getColor() == Color.GRAY) ownerBlock.setData(DyeColor.GRAY.getWoolData());
        else if (owner.getColor() == Color.PURPLE) ownerBlock.setData(DyeColor.PURPLE.getWoolData());
        else if (owner.getColor() == Color.ORANGE) ownerBlock.setData(DyeColor.ORANGE.getWoolData());
        else if (owner.getColor() == Color.YELLOW) ownerBlock.setData(DyeColor.YELLOW.getWoolData());
        else ownerBlock.setData(DyeColor.WHITE.getWoolData());
        this.owner = owner;
        if (gameHandler.getScoreBoard() != null) gameHandler.updateScoreBoard();
    }

    //Checks if any points can be captured
    public String checkCapturable()
    {
        Beacon beacon = (Beacon) objectiveBlock.getState();
        if (beacon.getEntitiesInRange() != null)
        {
            int enemies = 0;
            int friendlies = 0;
            HashMap<EventTeam, Integer> playersOfTeam = new HashMap<EventTeam, Integer>();
            for (LivingEntity entity : beacon.getEntitiesInRange())
            {
                if (entity instanceof Player == false) continue;	//Ignore if non player
                Player player = (Player) entity;
                if (inRange(player.getLocation(), beacon.getLocation()) == false) continue;   //Ignore if not in range
                if (owner.getPlayers().contains(player) == true)
                {
                    friendlies += 1;
                    GameHandler.sendActionBarMessage(player, "Time to Capture: " + captureTimer, ChatColor.RED);
                }
                else if (isNeutral == true && previousOwner.getPlayers().contains(player) == true)
                {
                    friendlies += 1;
                    GameHandler.sendActionBarMessage(player, "Time to Capture: " + captureTimer, ChatColor.RED);
                }
                else
                {
                    EventTeam team = gameHandler.getPlayers().get(player).getEventTeam();
                    if (!playersOfTeam.containsKey(team)) playersOfTeam.put(team, 0);
                    enemies += 1;
                    playersOfTeam.replace(team, playersOfTeam.get(team) + 1);
                    GameHandler.sendActionBarMessage(player, "Time to Capture: " + captureTimer, ChatColor.RED);
                }
            }
            //More enemies than friendlies in range.
            if (enemies > friendlies)
            {
                EventTeam maxTeam = null;

                for (EventTeam team : playersOfTeam.keySet())
                {
                    if (maxTeam == null) maxTeam = team;
                    if (playersOfTeam.get(team) > playersOfTeam.get(maxTeam)) maxTeam = team;
                }
                return maxTeam.getName();
            }
            else if (enemies == friendlies) return "deadlocked";
            else return "false";  //More friendlies than enemies in range 
        }
        else return "false";		//Nobody in range
    }

    //Handles the capture process for the objective
    public void updateCapture()
    {
        String capture = checkCapturable();
        
      //Enemies are superior to defenders, capture = name of most superior enemy team
        if(!capture.equalsIgnoreCase("deadlocked") && !capture.equalsIgnoreCase("false"))
        {
            EventTeam capturer = gameHandler.getEventTeamNames().get(capture);
            if (captureTimer > 0) captureTimer -= 1;        //Minus time remaining by 1
            if (captureTimer == captureTimerDefault / 2 && isNeutral == false) setNeutral();
            if (captureTimer == 0)      //Capture objective if timer is at 0
            {
                captureObjective(capturer, previousOwner);
            }

        }   
        else if (capture.equalsIgnoreCase("deadlocked")) return;  //Do nothing for now since deadlocked
        else if (capture.equalsIgnoreCase("false"))	//Enemies no longer have control
        {
            if (captureTimer < captureTimerDefault) captureTimer += 1;		//Add time remaining by 1
            if (captureTimer == captureTimerDefault)		//Capture objective if timer is at 0
            {
                if (isNeutral == true) removeNeutral();
                this.setBeingCaptured(false);
            }

        }

    }

    //Changes the control block to Bedrock to poweroff the beacon
    public void disableLight()
    {
        controlBlock.setType(Material.getMaterial("BEDROCK"));
        this.isDim = true;
    }

    //Changes the control block to a Gold_Block to power on the beacon
    public void enableLight()
    {
        controlBlock.setType(Material.getMaterial("GOLD_BLOCK"));
        this.isDim = false;
    }

    //Returns whether or not a player is in range of the objective
    private boolean inRange(Location playerLoc, Location objLoc)
    {
        //If player is within 3 below, 5 above, and 10 to either side of objective, return true
        if (playerLoc.getBlockY() > objLoc.getBlockY() - 3 && playerLoc.getBlockY() < objLoc.getBlockY() + 5
                && Math.abs(playerLoc.getBlockX() - objLoc.getBlockX()) < 10 && Math.abs(playerLoc.getBlockZ() - objLoc.getBlockZ()) < 10)
        {
            return true;
        }
        else return false;
    }

    public boolean isDim()
    {
        return isDim;
    }

    public void setDim(boolean isDim)
    {
        this.isDim = isDim;
    }

    public boolean isBeingCaptured()
    {
        return beingCaptured;
    }

    public void setBeingCaptured(boolean beingCaptured)
    {
        this.beingCaptured = beingCaptured;
    }

    public int getCaptureTimer()
    {
        return captureTimer;
    }

    public void setCaptureTimer(int captureTimer)
    {
        this.captureTimer = captureTimer;
    }

    public CapturePoint getCapturePoint()
    {
        return capturePoint;
    }

    public void setCapturePoint(CapturePoint capturePoint)
    {
        this.capturePoint = capturePoint;
    }

    public EventTeam getOwner()
    {
        return owner;
    }

    public void setOwner(EventTeam owner)
    {
        this.owner = owner;
    }

    public Block getOwnerBlock()
    {
        return ownerBlock;
    }

    public void setOwnerBlock(Block ownerBlock)
    {
        this.ownerBlock = ownerBlock;
    }

    public Block getControlBlock()
    {
        return controlBlock;
    }

    public void setControlBlock(Block controlBlock)
    {
        this.controlBlock = controlBlock;
    }

    public Block getObjectiveBlock()
    {
        return objectiveBlock;
    }

    public void setObjectiveBlock(Block objectiveBlock)
    {
        this.objectiveBlock = objectiveBlock;
    }

    public GameHandler getGameHandler()
    {
        return gameHandler;
    }

    public void setGameHandler(GameHandler gameHandler)
    {
        this.gameHandler = gameHandler;
    }

    public List<Location> getTeleporters()
    {
        return teleporters;
    }

    public void setTeleporters(List<Location> teleporters)
    {
        this.teleporters = teleporters;
    }

    public List<Boundary> getBoundaries()
    {
        return boundaries;
    }

    public void setBoundaries(List<Boundary> boundaries)
    {
        this.boundaries = boundaries;
    }

    public EventTeam getCapturingTeam()
    {
        return capturingTeam;
    }

    public void setCapturingTeam(EventTeam capturingTeam)
    {
        this.capturingTeam = capturingTeam;
    }

    public EventTeam getPreviousOwner()
    {
        return previousOwner;
    }

    public void setPreviousOwner(EventTeam previousOwner)
    {
        this.previousOwner = previousOwner;
    }

    public boolean isNeutral()
    {
        return isNeutral;
    }

    public void setNeutral(boolean isNeutral)
    {
        this.isNeutral = isNeutral;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
