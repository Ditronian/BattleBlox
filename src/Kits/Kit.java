package Kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class Kit
{
    protected Player player;
    protected ItemStack helmet;
    protected ItemStack chestPlate;
    protected ItemStack leggings;
    protected ItemStack boots;
    protected ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
    protected ItemStack axe = new ItemStack(Material.DIAMOND_AXE);
    protected ItemStack shield = new ItemStack(Material.SHIELD);
    protected ItemStack bow = new ItemStack(Material.BOW);
    protected ItemStack pick = new ItemStack(Material.DIAMOND_PICKAXE);
    protected ItemStack arrows = new ItemStack(Material.ARROW, 64);
    protected ItemStack beef = new ItemStack(Material.COOKED_BEEF, 64);
    protected ItemStack cobblestone = new ItemStack(Material.COBBLESTONE, 64);
    protected ItemStack boat = new ItemStack(Material.BOAT);

    public Kit(Player player)
    {
        this.player = player;
    }

    //Gives the provided player the corresponding kit
  //Gives the provided player the corresponding kit
    public void getKit(Player player)
    {
        player.getInventory().clear();
        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestPlate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
        player.getInventory().setItemInOffHand(shield);

        player.getInventory().setItem(0, sword);
        player.getInventory().setItem(1, bow);
        player.getInventory().setItem(2, axe);
        player.getInventory().setItem(3, pick);
        player.getInventory().setItem(4, arrows);
        player.getInventory().setItem(5, arrows);
        player.getInventory().setItem(6, beef);
        player.getInventory().setItem(7, cobblestone);
        player.getInventory().setItem(8, boat);

        getArmorEffects(player);
    }

    //Gives the provided player the appropriate armor effects
    public void getArmorEffects(Player player)
    {

        clearArmorEffects(player);
        
        helmet = player.getInventory().getHelmet();
        chestPlate = player.getInventory().getChestplate();
        leggings = player.getInventory().getLeggings();
        boots = player.getInventory().getBoots();

        if (helmet == null || chestPlate == null || leggings == null || boots == null) return;

        if (helmet.getType() == Material.IRON_HELMET && chestPlate.getType() == Material.IRON_CHESTPLATE
                && leggings.getType() == Material.IRON_LEGGINGS && boots.getType() == Material.IRON_BOOTS)
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 9999999, 0));
        }

        else if (helmet.getType() == Material.CHAINMAIL_HELMET && chestPlate.getType() == Material.CHAINMAIL_CHESTPLATE
                && leggings.getType() == Material.CHAINMAIL_LEGGINGS && boots.getType() == Material.CHAINMAIL_BOOTS)
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 9999999, 0));
        }

        else if (helmet.getType() == Material.DIAMOND_HELMET && chestPlate.getType() == Material.DIAMOND_CHESTPLATE
                && leggings.getType() == Material.DIAMOND_LEGGINGS && boots.getType() == Material.DIAMOND_BOOTS)
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 9999999, 0));
        }

        else if (helmet.getType() == Material.LEATHER_HELMET && chestPlate.getType() == Material.LEATHER_CHESTPLATE
                && leggings.getType() == Material.LEATHER_LEGGINGS && boots.getType() == Material.LEATHER_BOOTS)
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 9999999, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 9999999, 0));
        }
        else; //not wearing a full kit of a single armor
    }

    //Clears any potion effects for the given player
    public static void clearArmorEffects(Player player)
    {
        for (PotionEffect effect : player.getActivePotionEffects())
            player.removePotionEffect(effect.getType());
    }
}
