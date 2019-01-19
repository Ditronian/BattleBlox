package Other;

import org.bukkit.entity.Player;
import Kits.Kit;
import Objectives.CapturePointHandler;

public class EventPlayer
{
    private Player player;
    private Kit kit;
    private EventTeam eventTeam;
    private final int respawnTimerDefault = 16;
    private int respawnTimer = respawnTimerDefault;
    private boolean wasDead = false;
    private CapturePointHandler respawnPoint;

    public EventPlayer(Player player, EventTeam eventTeam)
    {
        this.player = player;
        this.eventTeam = eventTeam;
    }

    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public EventTeam getEventTeam()
    {
        return eventTeam;
    }

    public void setEventTeam(EventTeam eventTeam)
    {
        this.eventTeam = eventTeam;
    }

    public int getRespawnTimer()
    {
        return respawnTimer;
    }

    public void setRespawnTimer(int respawnTimer)
    {
        this.respawnTimer = respawnTimer;
    }

    public int getRespawnTimerDefault()
    {
        return respawnTimerDefault;
    }

    public Kit getKit()
    {
        return kit;
    }

    public void setKit(Kit kit)
    {
        this.kit = kit;
        kit.getKit(player);
    }

    public boolean isWasDead()
    {
        return wasDead;
    }

    public void setWasDead(boolean wasDead)
    {
        this.wasDead = wasDead;
    }

    public CapturePointHandler getRespawnPoint()
    {
        return respawnPoint;
    }

    public void setRespawnPoint(CapturePointHandler respawnPoint)
    {
        this.respawnPoint = respawnPoint;
    }
}
