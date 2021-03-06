package kineticrevolution.multiblocks.patterns;

import kineticrevolution.multiblocks.interfaces.IBlockDefinition;
import kineticrevolution.multiblocks.interfaces.IMultiBlock;
import kineticrevolution.util.RotationUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.security.InvalidParameterException;
import java.util.HashMap;

/**
 * Created by AEnterprise
 */
public class MultiBlockPattern {
    private final char[][][] pattern;
    private final HashMap<Character, IBlockDefinition> definitions;
	private final int xSize, ySize, zSize, masterXoffset, masterYoffset, masterZoffset;

	public MultiBlockPattern(char[][][] pattern, HashMap<Character, IBlockDefinition> definitions, int xSize, int ySize, int zSize, int masterXoffset, int masterYoffset, int masterZoffset) {
		if (pattern == null)
			throw new InvalidParameterException("A pattern is required!");
		if (definitions == null)
			throw new InvalidParameterException("Definitions are required!");
		this.pattern = pattern;
		this.definitions = definitions;
		if (ySize != pattern.length)
			throw new InvalidParameterException("ySize and pattern y length don't match!");
		for (int y = 0; y < ySize; y++) {
			if (xSize != pattern[y].length)
				throw new InvalidParameterException("xSize and pattern x length don't match!");
			for (int x = 0; x < xSize; x++) {
				if (zSize != pattern[y][x].length)
					throw new InvalidParameterException("zSize and pattern z length don't match!");
				for (int z = 0; z < zSize; z++) {
					if (!definitions.containsKey(pattern[y][x][z]))
						throw new InvalidParameterException("Missing block definition for char (" + pattern[y][x][z] + "). Pattern invalid");
				}
			}
		}
		this.xSize = xSize;
		this.ySize = ySize;
		this.zSize = zSize;
		this.masterXoffset = masterXoffset;
		this.masterYoffset = masterYoffset;
		this.masterZoffset = masterZoffset;
	}

	/**
	 * checks if the multiblock is valid
	 *
	 * @param world  the world
	 * @param startX xCoord from where to start
	 * @param startY yCoord from where to start
	 * @param startZ zCoord from where to start
	 * @return the rotation of the multiblock if it's valid, -1 if not
	 */
	public int checkMultiBlock(World world, int startX, int startY, int startZ) {
		for (int i = 0; i < 4; i++) {
			if (isValid(world, startX, startY, startZ, i)) {
				return i;
			}
		}
		return -1;
	}

	private boolean isValid(World world, int startX, int startY, int startZ, int rotation) {
		for (int y = 0; y < ySize; y++) {
			char[][] array = RotationUtils.rotate(pattern[y], rotation);
			for (int x = 0; x < xSize; x++) {
				for (int z = 0; z < zSize; z++) {
					int xc, zc;
					if (rotation == 0 || rotation == 2) {
						xc = x;
						zc = z;
					} else {
						xc = z;
						zc = x;
					}
					if (!definitions.get(array[xc][zc]).matchesUnformed(world, startX + xc, startY + y, startZ + zc))
						return false;
				}
			}
		}
		return true;
	}

	public boolean isStillValid(World world, int startX, int startY, int startZ, int rotation) {
		for (int y = 0; y < ySize; y++) {
			char[][] array = RotationUtils.rotate(pattern[y], rotation);
			for (int x = 0; x < xSize; x++) {
				for (int z = 0; z < zSize; z++) {
					int xc, zc;
					if (rotation == 0 || rotation == 2) {
						xc = x;
						zc = z;
					} else {
						xc = z;
						zc = x;
					}
					if (!definitions.get(array[xc][zc]).matchesFormed(world, startX + xc, startY + y, startZ + zc))
						return false;
				}
			}
		}
		return true;
	}

	public void formMultiblock(World world, int startX, int startY, int startZ, int rotation) {
		for (int y = 0; y < ySize; y++) {
			for (int x = 0; x < xSize; x++) {
				for (int z = 0; z < zSize; z++) {
					int xx, zz, realMasterXoffset, realMasterZoffset;
					if (rotation == 0 || rotation == 2) {
						xx = startX + x;
						zz = startZ + z;
						realMasterXoffset = masterXoffset;
						realMasterZoffset = masterZoffset;
					} else {
						xx = startX + z;
						zz = startZ + x;
						realMasterXoffset = masterZoffset;
						realMasterZoffset = masterXoffset;
					}
					TileEntity entity = world.getTileEntity(xx, startY + y, zz);
					if (entity instanceof IMultiBlock) {
						((IMultiBlock) entity).formMultiBlock(startX - xx + realMasterXoffset, -y + masterYoffset, startZ - zz + realMasterZoffset, rotation);
					}
				}
			}
		}
		System.out.println("formed a multiblock");
	}

	public void deformMultiblock(World world, int startX, int startY, int startZ, int rotation) {
		for (int y = 0; y < ySize; y++) {
			for (int x = 0; x < xSize; x++) {
				for (int z = 0; z < zSize; z++) {
					int xx, zz;
					if (rotation == 0 || rotation == 2) {
						xx = startX + x;
						zz = startZ + z;
					} else {
						xx = startX + z;
						zz = startZ + x;
					}
					TileEntity entity = world.getTileEntity(xx, startY + y, zz);
					if (entity instanceof IMultiBlock) {
						((IMultiBlock) entity).deformMultiBlock();
					}
				}
			}
		}
	}

	public int getMasterXoffset() {
		return masterXoffset;
	}

	public int getMasterYoffset() {
		return masterYoffset;
	}

	public int getMasterZoffset() {
		return masterZoffset;
	}

    /*
    * Allows for one to use == on two patterns to check for their equality.
    * This will avoid the standard comparison for equal object.
    *
    * Warning: Assumes both spaces in a pattern have equal dimensions!
    * Equal shapes, but with different pattern dimensions will not pass!
    *
    * NotNull to make sure no NullPointerException's can be thrown.
    *
    */
    @Override
    public boolean equals(@Nonnull Object o) {

        if (!(o instanceof MultiBlockPattern)) {
            throw new InvalidParameterException("Passed Object is not a MultiBlockPattern");
        }

        MultiBlockPattern mb1 = (MultiBlockPattern) o;

        // Check for the dimensions to be equal to eachother.
        if (this.pattern.length == mb1.pattern.length &&
                this.pattern[0].length == mb1.pattern[0].length &&
                this.pattern[0][0].length == mb1.pattern[0][0].length &&
                this.definitions == mb1.definitions) {

            for (int i = 0; i < this.pattern.length; i++) {
                for (int j = 0; j < this.pattern[i].length; j++) {
                    for (int k = 0; k < this.pattern[i][j].length; k++) {
                        if (this.pattern[i][j][k] != mb1.pattern[i][j][k]) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
}
