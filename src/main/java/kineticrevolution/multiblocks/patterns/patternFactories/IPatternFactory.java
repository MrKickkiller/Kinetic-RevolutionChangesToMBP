package kineticrevolution.multiblocks.patterns.patternFactories;

import kineticrevolution.multiblocks.patterns.Pattern;

/**
 * Created by MrKickkiller on 3/07/2015.
 */
public interface IPatternFactory {

	Pattern createPattern(char a, int... size);
}
