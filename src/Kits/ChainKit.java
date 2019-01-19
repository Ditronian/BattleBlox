package Kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ChainKit extends Kit
{

    public ChainKit(Player player)
    {
        super(player);
        helmet = new ItemStack(Material.CHAINMAIL_HELMET);
        chestPlate = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        boots = new ItemStack(Material.CHAINMAIL_BOOTS);
        
        getKit(player);
    }
}
