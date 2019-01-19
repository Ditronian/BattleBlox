package Objectives;

import org.bukkit.Color;
import Events.ConquestHandler;
import Main.CommandHandler;
import Other.EventTeam;

public class ConquestPointHandler extends CapturePointHandler
{
    protected ConquestHandler conquestHandler;
    protected String name;

    public ConquestPointHandler(CapturePoint capturePoint, String name, EventTeam owner, ConquestHandler conquestHandler)
    {
        super(capturePoint, owner);
        this.gameHandler = conquestHandler;
        this.conquestHandler = conquestHandler;
        this.name = name;
        this.disableLight();                                //RushPoint lights are disabled by default
        this.setCPOwner(owner);
        this.captureTimerDefault = 28;                              //Default is 5 secs to capture.
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
        this.setCPOwner(gameHandler.getTeams().get(capturing.getColor()));
        isNeutral = false;
        captureTimer = captureTimerDefault;
        conquestHandler.getTeamPoints().get(capturing.getColor()).add(this);
        conquestHandler.getTeamPoints().get(losing.getColor()).remove(this);
        int numOfObjOwners = 0;
        for (EventTeam team : conquestHandler.getEventTeams().values())
        {
            if (conquestHandler.getTeamPoints().get(team.getColor()).size() > 0) numOfObjOwners++;
        }
        if (numOfObjOwners == 1)      //game is over, OR insert ticket victory here
        {
            CommandHandler.endEvent();
        }
        else    //Announce obj captured
        {
            gameHandler.globalTitleMessage("Objective: " + this.name + " captured!", capturing.getChatColor());
            setBeingCaptured(false);
        }

    }

    //Switches objective being captured to neutral
    @Override
    protected void setNeutral()
    {
        previousOwner = this.getOwner();
        setCPOwner(conquestHandler.getTeams().get(Color.WHITE));
        isNeutral = true;
    }

    //Switches objective being captured back to original owner
    @Override
    protected void removeNeutral()
    {
        setCPOwner(previousOwner);
        isNeutral = false;
        gameHandler.globalTitleMessage("Objective: " + this.name + " recaptured!", previousOwner.getChatColor());
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
