package Objectives;

import java.io.Serializable;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import Other.MCLocation;

public class Boundary implements Serializable
{
    private static final long serialVersionUID = -850654161339232313L;
    private MCLocation center;

    public Boundary(Location location)
    {
        center = new MCLocation(location);
    }

    //Replaces the glowstone block with an air block
    public void setAir()
    {
        center.getLocationObject().getBlock().setType(Material.AIR);
    }

    //Returns the Chunk this boundary is blocking
    public Chunk getChunk()
    {
        return center.getLocationObject().getChunk();
    }
}
