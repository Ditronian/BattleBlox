package Other;

import java.io.Serializable;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

public class SpawnPoint implements Serializable
{

    private static final long serialVersionUID = -5109660591150740724L;
    private MCLocation spawnBlock;

    public SpawnPoint(Block spawnBlock)
    {
        this.spawnBlock = new MCLocation(spawnBlock.getLocation(), "spawn point");
        makeBox(spawnBlock);
    }

    //Make a glass prison around spawn location
    private void makeBox(Block spawnBlock)
    {
        Block[][] blocks = new Block[21][21];
        blocks[11][11] = spawnBlock;

        for (int row = 0; row < blocks.length; row++)
        {
            int z = row - 10;

            for (int column = 0; column < blocks[row].length; column++)
            {
                int x = column - 10;
                blocks[row][column] = spawnBlock.getLocation().add(x, 0, z).getBlock();
                if (x != 0 || z != 0) blocks[row][column].setType(Material.STAINED_GLASS);
                blocks[row][column].getLocation().add(0, 4, 0).getBlock().setType(Material.STAINED_GLASS);
            }
        }
        //Make Kit blocks
        int x = spawnBlock.getX();
        int y = spawnBlock.getY();
        int z = spawnBlock.getZ();
        World world = spawnBlock.getWorld();
        
        //Purpur Pillars
        MCLocation.getBlockLocationObject(x+9, y, z-8, world).getBlock().setType(Material.PURPUR_PILLAR);
        MCLocation.getBlockLocationObject(x+9, y, z-3, world).getBlock().setType(Material.PURPUR_PILLAR);
        MCLocation.getBlockLocationObject(x+9, y, z+3, world).getBlock().setType(Material.PURPUR_PILLAR);
        MCLocation.getBlockLocationObject(x+9, y, z+8, world).getBlock().setType(Material.PURPUR_PILLAR);
        
        //PressurePlate
        MCLocation.getBlockLocationObject(x+9, y+1, z-8, world).getBlock().setType(Material.GOLD_PLATE);
        MCLocation.getBlockLocationObject(x+9, y+1, z-3, world).getBlock().setType(Material.GOLD_PLATE);
        MCLocation.getBlockLocationObject(x+9, y+1, z+3, world).getBlock().setType(Material.GOLD_PLATE);
        MCLocation.getBlockLocationObject(x+9, y+1, z+8, world).getBlock().setType(Material.GOLD_PLATE);
        
        //Stands
        ArmorStand chain = spawnBlock.getWorld().spawn(MCLocation.getBlockLocationObject(x+10, y+1, z-8, world, "west"), ArmorStand.class);
        ArmorStand leather = spawnBlock.getWorld().spawn(MCLocation.getBlockLocationObject(x+10, y+1, z-3, world, "west"), ArmorStand.class);
        ArmorStand iron = spawnBlock.getWorld().spawn(MCLocation.getBlockLocationObject(x+10, y+1, z+3, world, "west"), ArmorStand.class);
        ArmorStand diamond = spawnBlock.getWorld().spawn(MCLocation.getBlockLocationObject(x+10, y+1, z+8, world, "west"), ArmorStand.class);
        chain.setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
        chain.setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        chain.setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
        chain.setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
        chain.setInvulnerable(true);
        
        leather.setHelmet(new ItemStack(Material.LEATHER_HELMET));
        leather.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        leather.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
        leather.setBoots(new ItemStack(Material.LEATHER_BOOTS));
        leather.setInvulnerable(true);
        
        iron.setHelmet(new ItemStack(Material.IRON_HELMET));
        iron.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        iron.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        iron.setBoots(new ItemStack(Material.IRON_BOOTS));
        iron.setInvulnerable(true);
        
        diamond.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        diamond.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        diamond.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        diamond.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
        diamond.setInvulnerable(true);
    }

    public MCLocation getSpawnBlock()
    {
        return spawnBlock;
    }

    public void setSpawnBlock(MCLocation spawnBlock)
    {
        this.spawnBlock = spawnBlock;
    }
}
