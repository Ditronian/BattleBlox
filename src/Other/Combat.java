package Other;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import Events.GameHandler;
import net.md_5.bungee.api.ChatColor;

public class Combat
{
    public static void checkHeadShot(Arrow arrow, Player damagee, Player damager)
    {
        double arrowHitHeight = arrow.getLocation().getY();
        double headHeight = damagee.getLocation().getY() + damagee.getEyeHeight();
        double footHeight = damagee.getLocation().getY() + 0.45;

        if (arrowHitHeight >= headHeight)
        {
            GameHandler.sendActionBarMessage(damagee, "Headshot!", ChatColor.RED);
            GameHandler.sendActionBarMessage(damager, "Headshot!", ChatColor.RED);
            damagee.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
            /*Bukkit.broadcastMessage("Arrow Height: " + arrowHitHeight);
            Bukkit.broadcastMessage("Head Height: " + headHeight);*/
        }
        else if (!damagee.hasPotionEffect(PotionEffectType.SLOW) && arrowHitHeight <= footHeight)
        {
            GameHandler.sendActionBarMessage(damagee, "FootShot!", ChatColor.RED);
            GameHandler.sendActionBarMessage(damager, "FootShot!", ChatColor.RED);
            damagee.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 0));

            /*Bukkit.broadcastMessage("Arrow Height: " + arrowHitHeight);
            Bukkit.broadcastMessage("Foot Height: " + footHeight);*/
        }
        else
        {
            /*Bukkit.broadcastMessage("Miss");
            Bukkit.broadcastMessage("Arrow Height: " + arrowHitHeight);
            Bukkit.broadcastMessage("Head Height: " + headHeight);*/
        }
    }
}
