//Handles the Capturing/Status of all loaded Capture Points in a given GameHandler

package Objectives;

import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import Events.RushHandler;
import Main.CommandHandler;
import Other.EventTeam;
import net.md_5.bungee.api.ChatColor;

public class RushPointHandler extends CapturePointHandler
{
    protected RushHandler rushHandler;

    public RushPointHandler(CapturePoint capturePoint, EventTeam owner, RushHandler rushHandler)
    {
        super(capturePoint, owner);
        this.gameHandler = rushHandler;
        this.rushHandler = rushHandler;
        this.disableLight();								//RushPoint lights are disabled by default
        this.setCPOwner(owner);
        this.captureTimerDefault = 14;								//Default is 5 secs to capture.
        this.captureTimer = captureTimerDefault;
    }

    //Resets the rush point to its default values
    @Override
    public void reset()
    {
        beingCaptured = false;
        isNeutral = false;
        capturingTeam = null;
        previousOwner = null;
        disableLight();
        captureTimer = captureTimerDefault;

    }

    //Handles the capture of the objective
    @Override
    protected void captureObjective(EventTeam capturing, EventTeam losing)
    {
        this.setCPOwner(gameHandler.getTeams().get(Color.RED));
        isNeutral = false;
        captureTimer = captureTimerDefault;
        this.disableLight();
        int currentIndex = gameHandler.getCapturePoints().indexOf(this);
        if (currentIndex == gameHandler.getCapturePoints().size() - 1) 		//game is over
        {
            CommandHandler.endEvent();
        }
        else 	//Advance to the next objective!
        {
            for (Player player : rushHandler.getPlayers().keySet())
            {
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 0);
            }
            rushHandler.setAttackableRushPoint((RushPointHandler) gameHandler.getCapturePoints().get(currentIndex + 1));
            rushHandler.setDefenderTeleportPoint((RushPointHandler) gameHandler.getCapturePoints().get(currentIndex + 1));
            rushHandler.setAttackerTeleportPoint(this);
            gameHandler.globalTitleMessage("Objective: " + (rushHandler.getCapturePoints().indexOf(rushHandler.getAttackableRushPoint())) + " captured!", ChatColor.RED);
            setBeingCaptured(false);
        }

    }

    //Switches objective being captured to neutral
    @Override
    protected void setNeutral()
    {
        int index = rushHandler.getCapturePoints().indexOf((CapturePointHandler) this) + 1;
        if (index < rushHandler.getCapturePoints().size()) rushHandler.setDefenderTeleportPoint((RushPointHandler) rushHandler.getCapturePoints().get(index));
        else rushHandler.setDefenderTeleportPoint("null");     //Removes the defense's tp point since they have lost their last one
        previousOwner = this.getOwner();
        setCPOwner(rushHandler.getTeams().get(Color.WHITE));
        isNeutral = true;
    }

    //Switches objective being captured back to original owner
    @Override
    protected void removeNeutral()
    {
        rushHandler.setDefenderTeleportPoint(this);
        setCPOwner(previousOwner);
        isNeutral = false;
        gameHandler.globalTitleMessage("Objective: " + (rushHandler.getCapturePoints().indexOf(rushHandler.getAttackableRushPoint()) + 1) + " recaptured!",
                previousOwner.getChatColor());
    }
}
