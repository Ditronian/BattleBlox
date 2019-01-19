package Maps;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import Objectives.CapturePoint;
import Other.SpawnPoint;

public abstract class GameMap implements Listener, Serializable
{

	private static final long serialVersionUID = 4750985420676756116L;
	protected String mapName;
	protected SpawnPoint spawnPoint;
	protected List<CapturePoint> capturePoints = new ArrayList<CapturePoint>();
	protected Integer editPointIndex;

	public GameMap(String mapName)
	{
		this.mapName = mapName;
	}

	//Listens for block placement
	@EventHandler
	abstract public void onBlockPlacement(BlockPlaceEvent e);

	//Disables this map's event listener if it has one.
	public void unregisterEvents()
	{
		HandlerList.unregisterAll(this);
	}

	

	public String getMapName()
	{
		return mapName;
	}

	public void setMapName(String mapName)
	{
		this.mapName = mapName;
	}

	public SpawnPoint getSpawnPoint()
	{
		return spawnPoint;
	}

	public void setSpawnPoint(SpawnPoint spawnPoint)
	{
		this.spawnPoint = spawnPoint;
	}

	public List<CapturePoint> getObjectives()
	{
		return capturePoints;
	}

	public void setObjectives(List<CapturePoint> capturePoints)
	{
		this.capturePoints = capturePoints;
	}

    public List<CapturePoint> getCapturePoints()
    {
        return capturePoints;
    }

    public void setCapturePoints(List<CapturePoint> capturePoints)
    {
        this.capturePoints = capturePoints;
    }

    public Integer getEditPoint()
    {
        return editPointIndex;
    }

    public void setEditPoint(Integer editPoint)
    {
        this.editPointIndex = editPoint;
    }

}
