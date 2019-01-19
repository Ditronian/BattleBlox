package Maps;

import java.io.Serializable;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import Objectives.Boundary;
import Objectives.RushPoint;
import Other.MCLocation;
import Other.SpawnPoint;

public class RushMap extends GameMap implements Listener, Serializable
{

    private static final long serialVersionUID = 225530129399077641L;

    public RushMap(String mapName)
    {
        super(mapName);
    }

    //Listens for block placement and creates a capture point when a beacon is placed
    @EventHandler
    @Override
    public void onBlockPlacement(BlockPlaceEvent e)
    {
        Block block = e.getBlock();

        if (block.getType().name().equalsIgnoreCase("beacon"))	//if placed block is an objective
        {

            //Set each boundary block in previous objective to air
            if (capturePoints.size() >= 1)
            {
                for (Boundary boundary : capturePoints.get(capturePoints.size() - 1).getBoundaries())
                {
                    boundary.setAir();
                }
            }

            if (capturePoints.size() >= 1 && capturePoints.get(capturePoints.size() - 1).getTeleporters().size() < 1)
            {
                e.setCancelled(true);
                e.getPlayer().sendMessage("You must place at least one teleport location for the previous capture point.");
                return;
            }

            Block objectiveBlock = block;
            Block controlBlock = objectiveBlock.getLocation().subtract(0, 1, 0).getBlock();		//find the block below the placed beacon
            Block ownerBlock = objectiveBlock.getLocation().add(0, 1, 0).getBlock();		//find the block above the placed beacon
            RushPoint rushPoint = new RushPoint(objectiveBlock, controlBlock, ownerBlock);	//Creates the rush point
            if (editPointIndex == null)
            {
                capturePoints.add(rushPoint);
                e.getPlayer().sendMessage("Capture Point #" + capturePoints.size() + " created.");
            }
            else
            {
                capturePoints.set(editPointIndex.intValue(), rushPoint);
                e.getPlayer().sendMessage("Capture Point #" + editPointIndex.intValue() + 1 + " created.");
            }

        }

        else if (block.getType().name().equalsIgnoreCase("sea_lantern"))	//if placed block is a teleporter
        {
            if (capturePoints.size() >= 1)
            {
                if (editPointIndex == null)
                {
                    RushPoint selectedRushPoint = (RushPoint) capturePoints.get(capturePoints.size() - 1);
                    selectedRushPoint.getTeleporters().add(new MCLocation(e.getBlock().getLocation(), "teleporter"));
                    e.getPlayer().sendMessage("Teleport location #" + selectedRushPoint.getTeleporters().size() + " placed.");
                }
                else
                {
                    RushPoint selectedRushPoint = (RushPoint) capturePoints.get(editPointIndex.intValue());
                    selectedRushPoint.getTeleporters().add(new MCLocation(e.getBlock().getLocation(), "teleporter"));
                    e.getPlayer().sendMessage("Teleport location #" + selectedRushPoint.getTeleporters().size() + " placed.");
                }
            }
            else
            {
                e.setCancelled(true);
                e.getPlayer().sendMessage("You must first place a Rush Point.");
                return;
            }
        }

        else if (block.getType().name().equalsIgnoreCase("purpur_pillar"))	//if placed block is the spawn point
        {
            this.spawnPoint = new SpawnPoint(e.getBlock());
            e.getPlayer().sendMessage("Spawn point set.");
        }

        else if (block.getType().name().equalsIgnoreCase("glowstone"))	//if placed block is the spawn point
        {
            if (capturePoints.size() >= 1)
            {

                if (editPointIndex == null)
                {
                    RushPoint selectedRushPoint = (RushPoint) capturePoints.get(capturePoints.size() - 1);
                    selectedRushPoint.getBoundaries().add(new Boundary(e.getBlock().getLocation()));

                    e.getPlayer().sendMessage("Boundary Chunk #" + selectedRushPoint.getBoundaries().size() + " placed.");
                }
                else
                {
                    RushPoint selectedRushPoint = (RushPoint) capturePoints.get(editPointIndex.intValue());
                    selectedRushPoint.getBoundaries().add(new Boundary(e.getBlock().getLocation()));

                    e.getPlayer().sendMessage("Boundary Chunk #" + selectedRushPoint.getBoundaries().size() + " placed.");
                }

            }
            else
            {
                e.setCancelled(true);
                e.getPlayer().sendMessage("You must first place a Rush Point.");
                return;
            }

        }
    }

}
