package Objectives;

import java.io.Serializable;
import org.bukkit.block.Block;

public class ConquestPoint extends CapturePoint implements Serializable
{
    private static final long serialVersionUID = -2246032998390851935L;
    String name;

    public ConquestPoint(Block objectiveBlock, Block controlBlock, Block ownerBlock, String name)
    {
        super(objectiveBlock, controlBlock, ownerBlock);
        setName(name);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
