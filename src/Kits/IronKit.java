package Kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class IronKit extends Kit
{

    public IronKit(Player player)
    {
        super(player);
        helmet = new ItemStack(Material.IRON_HELMET);
        chestPlate = new ItemStack(Material.IRON_CHESTPLATE);
        leggings = new ItemStack(Material.IRON_LEGGINGS);
        boots = new ItemStack(Material.IRON_BOOTS);
        
        getKit(player);
    }
}
