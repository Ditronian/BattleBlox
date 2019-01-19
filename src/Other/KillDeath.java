package Other;

import java.io.Serializable;

public class KillDeath implements Serializable
{

    private static final long serialVersionUID = 1783173297479127033L;
    private int totalKills = 0;
    private int totalDeaths = 0;
    private int rushAttackerKills = 0;
    private int rushAttackerDeaths = 0;
    private int rushDefenderKills = 0;
    private int rushDefenderDeaths = 0;
    private int conquestKills = 0;
    private int conquestDeaths = 0;

    public double getKD(int kills, int deaths)
    {
        double dKills = (double) kills;
        double dDeaths = (double) deaths;
        if (deaths == 0) return 9999;
        return dKills / dDeaths;
    }

    //Called by command
    public String toString()
    {
        return String.format("%-20s %.2f   (Kills: %s / Deaths: %s)%n"
                + "%-20s %.2f   (Kills: %s / Deaths: %s)%n"
                + "%-20s %.2f   (Kills: %s / Deaths: %s)%n"
                + "%-20s %.2f   (Kills: %s / Deaths: %s)%n",
                "Rush Attacking:",getKD(rushAttackerKills,rushAttackerDeaths),rushAttackerKills,rushAttackerDeaths,
                "Rush Defending:", getKD(rushDefenderKills,rushDefenderDeaths),rushDefenderKills,rushDefenderDeaths,
                "Conquest:", getKD(conquestKills,conquestDeaths),conquestKills,conquestDeaths,
                "Total K/D:",getKD(totalKills,totalDeaths),totalKills,totalDeaths);
    }

    public int getTotalKills()
    {
        return totalKills;
    }

    public void incrementTotalKills()
    {
        this.totalKills++;
    }

    public int getTotalDeaths()
    {
        return totalDeaths;
    }

    public void incrementTotalDeaths()
    {
        this.totalDeaths++;
    }

    public int getRushAttackerKills()
    {
        return rushAttackerKills;
    }

    public void incrementRushAttackerKills()
    {
        this.rushAttackerKills++;
    }

    public int getRushAttackerDeaths()
    {
        return rushAttackerDeaths;
    }

    public void incrementRushAttackerDeaths()
    {
        this.rushAttackerDeaths++;
    }

    public int getRushDefenderKills()
    {
        return rushDefenderKills;
    }

    public void incrementRushDefenderKills()
    {
        this.rushDefenderKills++;
    }

    public int getRushDefenderDeaths()
    {
        return rushDefenderDeaths;
    }

    public void incrementRushDefenderDeaths()
    {
        this.rushDefenderDeaths++;
    }

    public int getConquestKills()
    {
        return conquestKills;
    }

    public void incrementConquestKills()
    {
        this.conquestKills++;
    }

    public int getConquestDeaths()
    {
        return conquestDeaths;
    }

    public void incrementConquestDeaths()
    {
        this.conquestDeaths++;
    }
}
