package Other;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class MCLocation implements Serializable
{
	private static final long serialVersionUID = -5635164887208499425L;
	private String world;
	private int x;
	private int y;
	private int z;
	private String typeOfObject;

	//Used for non-important object locations
	public MCLocation(Location location)
	{
		this.world = location.getWorld().getName();
		this.x = location.getBlockX();
		this.y = location.getBlockY();
		this.z = location.getBlockZ();
	}
	
	//Used for important objects, ie teleporters/objectives/spawn points
	public MCLocation(Location location, String typeOfObject)
	{
		this.world = location.getWorld().getName();
		this.x = location.getBlockX();
		this.y = location.getBlockY();
		this.z = location.getBlockZ();
		this.typeOfObject = typeOfObject;
	}

	//Returns to shitty Spigot Location object using the information stored in this object.
	public Location getLocationObject()
	{
		Location location = new Location(Bukkit.getWorld(world), x, y, z);
		return location;
	}
	
	//Returns to shitty Spigot Location object statically using arguments.
    public static Location getBlockLocationObject(double x, double y, double z, World world)
    {
        Location location = new Location(world, x+0.5, y, z+0.5);
        location = location.getBlock().getLocation();
        return location;
    }
    
  //Returns to shitty Spigot Location object statically using arguments, includes a direction to face the object.
    public static Location getBlockLocationObject(double x, double y, double z, World world, String direction)
    {
        Location location = new Location(world, x+0.5, y, z+0.5);
        if(direction.equalsIgnoreCase("west"))location.setYaw(90);
        else if(direction.equalsIgnoreCase("east"))location.setYaw(-90);
        else if(direction.equalsIgnoreCase("north"))location.setYaw(180);
        else if(direction.equalsIgnoreCase("south"))location.setYaw(0);
        return location;
    }

	public String getWorld()
	{
		return world;
	}

	public void setWorld(String world)
	{
		this.world = world;
	}

	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public int getZ()
	{
		return z;
	}

	public void setZ(int z)
	{
		this.z = z;
	}

	public String getTypeOfObject()
	{
		return typeOfObject;
	}

	public void setTypeOfObject(String typeOfObject)
	{
		this.typeOfObject = typeOfObject;
	}
}
