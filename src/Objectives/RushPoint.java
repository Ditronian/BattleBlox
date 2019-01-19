package Objectives;

import java.io.Serializable;

import org.bukkit.block.Block;

public class RushPoint extends CapturePoint implements Serializable
{

	private static final long serialVersionUID = -2246032998390851935L;

	public RushPoint(Block objectiveBlock, Block controlBlock, Block ownerBlock)
	{
		super(objectiveBlock, controlBlock, ownerBlock);

	}
}
