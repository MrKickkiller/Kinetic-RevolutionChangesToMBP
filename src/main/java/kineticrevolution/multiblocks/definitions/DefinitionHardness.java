package kineticrevolution.multiblocks.definitions;

import kineticrevolution.multiblocks.interfaces.IBlockDefinition;
import net.minecraft.world.World;

/**
 * Created by big_Xplosion
 */
public class DefinitionHardness implements IBlockDefinition {

	private int hardness;

	public DefinitionHardness(int hardness) {
		this.hardness = hardness;
	}

	@Override
	public boolean matchesUnformed(World world, int x, int y, int z) {
		return world.getBlock(x, y, z).getBlockHardness(world, x, y, z) >= hardness;
	}

	@Override
	public boolean matchesFormed(World world, int x, int y, int z) {
		return matchesFormed(world, x, y, z);
	}
}
