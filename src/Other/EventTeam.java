//A group of allied players participating in an active game event

package Other;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import Events.GameHandler;
import Kits.Kit;
import net.md_5.bungee.api.ChatColor;

public class EventTeam
{

    private List<Player> players = new ArrayList<Player>();
    private Color color;
    private String name;
    private GameHandler gameHandler;
    private Team sbTeam;
    private boolean isDefeated;

    public EventTeam(Color color, GameHandler gameHandler, Team sbTeam)
    {
        this.color = color;
        this.gameHandler = gameHandler;
        this.sbTeam = sbTeam;
        this.isDefeated = false;
    }

    //Adds player to the team
    public void addPlayer(Player player)
    {
        players.add(player);
        player.saveData();
        player.getInventory().clear();
        
        sbTeam.addPlayer(player);
        player.setDisplayName(getChatColor() + player.getName());
        player.teleport((gameHandler.getMap().getSpawnPoint().getSpawnBlock().getLocationObject().add(0, 2, 0)));
        player.setScoreboard(gameHandler.getScoreBoard());
        EventPlayer eventPlayer = new EventPlayer(player,this);
        Kit.clearArmorEffects(player);
        gameHandler.getPlayers().put(player,eventPlayer);
        gameHandler.getDeadPlayers().put(player,eventPlayer);
        player.sendMessage("You have joined the event!");
        player.sendMessage("Type '/kit [type]' when in the respawn zone to get a kit!");
        player.sendMessage("Types: iron/chain/leather/diamond");
    }
    
    //Removes player from the team
    public void removePlayer(Player player)
    {
        
        players.remove(player);
        sbTeam.removePlayer(player);
    }

    //Returns the Chat Color corresponding with this team's color
    public ChatColor getChatColor()
    {
        if (color == Color.RED) return ChatColor.RED;
        else if (color == Color.BLUE) return ChatColor.BLUE;
        else if (color == Color.GREEN) return ChatColor.GREEN;
        else if (color == Color.GRAY) return ChatColor.GRAY;
        else if (color == Color.PURPLE) return ChatColor.LIGHT_PURPLE;
        else if (color == Color.ORANGE) return ChatColor.GOLD;
        else if (color == Color.YELLOW) return ChatColor.YELLOW;
        else return ChatColor.WHITE;
    }
    
  //Returns the Chat Color corresponding with this string color
    public static ChatColor getChatColor(String color)
    {
        if (color.equalsIgnoreCase("red")) return ChatColor.RED;
        else if (color.equalsIgnoreCase("blue")) return ChatColor.BLUE;
        else if (color.equalsIgnoreCase("green")) return ChatColor.GREEN;
        else if (color.equalsIgnoreCase("gray")) return ChatColor.GRAY;
        else if (color.equalsIgnoreCase("purple")) return ChatColor.LIGHT_PURPLE;
        else if (color.equalsIgnoreCase("orange")) return ChatColor.GOLD;
        else if (color.equalsIgnoreCase("yellow")) return ChatColor.YELLOW;
        else return ChatColor.WHITE;
    }
    
  //Returns the Chat Color corresponding with this string color
    public static Color getColor(String color)
    {
        if (color.equalsIgnoreCase("red")) return Color.RED;
        else if (color.equalsIgnoreCase("blue")) return Color.BLUE;
        else if (color.equalsIgnoreCase("green")) return Color.GREEN;
        else if (color.equalsIgnoreCase("gray")) return Color.GRAY;
        else if (color.equalsIgnoreCase("purple")) return Color.PURPLE;
        else if (color.equalsIgnoreCase("orange")) return Color.ORANGE;
        else if (color.equalsIgnoreCase("yellow")) return Color.YELLOW;
        else return Color.WHITE;
    }
    
    public List<Player> getPlayers()
    {
        return players;
    }

    public void setPlayers(List<Player> players)
    {
        this.players = players;
    }

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isDefeated()
    {
        return isDefeated;
    }

    public void setDefeated(boolean isDefeated)
    {
        this.isDefeated = isDefeated;
    }

}
