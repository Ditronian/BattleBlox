package Kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LeatherKit extends Kit
{

    public LeatherKit(Player player)
    {
        super(player);
        helmet = new ItemStack(Material.LEATHER_HELMET);
        chestPlate = new ItemStack(Material.LEATHER_CHESTPLATE);
        leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        boots = new ItemStack(Material.LEATHER_BOOTS);
        
        getKit(player);
    }

}
