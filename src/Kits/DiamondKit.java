package Kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DiamondKit extends Kit
{

    public DiamondKit(Player player)
    {
        super(player);
        helmet = new ItemStack(Material.DIAMOND_HELMET);
        chestPlate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        boots = new ItemStack(Material.DIAMOND_BOOTS);
        
        getKit(player);
    }

}
