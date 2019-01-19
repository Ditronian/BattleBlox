//Ideas: Make objectives flash between team colors whilst being captured
//		Associate glass blocks with beacons w/ team colors

package Objectives;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import Other.MCLocation;

public abstract class CapturePoint implements Serializable
{
	private static final long serialVersionUID = -4612444227683761023L;
	protected MCLocation objectiveBlockLocation;	//The Beacon
	protected MCLocation controlBlockLocation;		//Block underneath the beacon, used to control its light
	protected MCLocation ownerBlockLocation;		//Glass block above beacon that determines its color.

	protected List<MCLocation> teleporters = new ArrayList<MCLocation>();
	protected List<Boundary> boundaries = new ArrayList<Boundary>();
	
	

	public CapturePoint(Block block, Block controlBlock, Block ownerBlock)
	{
		this.objectiveBlockLocation = new MCLocation(block.getLocation(), "objective");
		this.controlBlockLocation = new MCLocation(controlBlock.getLocation());
		this.ownerBlockLocation = new MCLocation(ownerBlock.getLocation());
		spawnPowerBlocks(block);
	}

	//Spawns the 3x3 square of gold blocks under the beacon to power it.
	public void spawnPowerBlocks(Block objectiveBlock)
	{
		Location objectiveBlockLocation = objectiveBlock.getLocation();
		objectiveBlockLocation.subtract(0, 1, 0).getBlock().setType(Material.getMaterial("GOLD_BLOCK"));
		objectiveBlock.getLocation().subtract(1, 1, 0).getBlock().setType(Material.getMaterial("GOLD_BLOCK"));
		objectiveBlock.getLocation().subtract(0, 1, 1).getBlock().setType(Material.getMaterial("GOLD_BLOCK"));
		objectiveBlock.getLocation().subtract(1, 1, 1).getBlock().setType(Material.getMaterial("GOLD_BLOCK"));
		objectiveBlock.getLocation().subtract(-1, 1, 0).getBlock().setType(Material.getMaterial("GOLD_BLOCK"));
		objectiveBlock.getLocation().subtract(0, 1, -1).getBlock().setType(Material.getMaterial("GOLD_BLOCK"));
		objectiveBlock.getLocation().subtract(-1, 1, -1).getBlock().setType(Material.getMaterial("GOLD_BLOCK"));
		objectiveBlock.getLocation().subtract(1, 1, -1).getBlock().setType(Material.getMaterial("GOLD_BLOCK"));
		objectiveBlock.getLocation().subtract(-1, 1, 1).getBlock().setType(Material.getMaterial("GOLD_BLOCK"));
		objectiveBlockLocation.add(0, 2, 0).getBlock().setType(Material.getMaterial("STAINED_GLASS"));
	}

	public List<MCLocation> getTeleporters()
	{
		return teleporters;
	}

	public void setTeleporters(List<MCLocation> teleporters)
	{
		this.teleporters = teleporters;
	}

	public MCLocation getObjectiveBlockLocation()
	{
		return objectiveBlockLocation;
	}

	public void setObjectiveBlockLocation(MCLocation objectiveBlockLocation)
	{
		this.objectiveBlockLocation = objectiveBlockLocation;
	}

	public MCLocation getControlBlockLocation()
	{
		return controlBlockLocation;
	}

	public void setControlBlockLocation(MCLocation controlBlockLocation)
	{
		this.controlBlockLocation = controlBlockLocation;
	}

	public MCLocation getOwnerBlockLocation()
	{
		return ownerBlockLocation;
	}

	public void setOwnerBlockLocation(MCLocation ownerBlockLocation)
	{
		this.ownerBlockLocation = ownerBlockLocation;
	}

	public List<Boundary> getBoundaries()
	{
		return boundaries;
	}

	public void setBoundaries(List<Boundary> boundaries)
	{
		this.boundaries = boundaries;
	}

}