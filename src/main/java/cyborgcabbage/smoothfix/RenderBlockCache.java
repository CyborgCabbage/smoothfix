package cyborgcabbage.smoothfix;

import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;

import java.util.Arrays;

final public class RenderBlockCache {
    private static final int CACHE_RANGE = 1;
    private static final int CACHE_SIZE = CACHE_RANGE*2+1;
    private final boolean[] brightnessCached = new boolean[CACHE_SIZE*CACHE_SIZE*CACHE_SIZE];
    private final float[] brightnessValue = new float[CACHE_SIZE*CACHE_SIZE*CACHE_SIZE];
    private final boolean[] opacityCached = new boolean[CACHE_SIZE*CACHE_SIZE*CACHE_SIZE];
    private final boolean[] opacityValue = new boolean[CACHE_SIZE*CACHE_SIZE*CACHE_SIZE];
    private int offsetX = Integer.MAX_VALUE;
    private int offsetY = Integer.MAX_VALUE;
    private int offsetZ = Integer.MAX_VALUE;
    private Block block;
    private IBlockAccess access;
    //Test
    public void setupCache(Block block, IBlockAccess access, int x, int y, int z){
        if(x != offsetX || y != offsetY || z != offsetZ || this.block != block || this.access != access) {
            this.block = block;
            this.access = access;
            Arrays.fill(brightnessCached, false);
            Arrays.fill(opacityCached, false);
            offsetX = x;
            offsetY = y;
            offsetZ = z;
        }
    }
    public float getBrightness(int relX, int relY, int relZ) {
        int index = (relX+CACHE_RANGE)*CACHE_SIZE*CACHE_SIZE+(relY+CACHE_RANGE)*CACHE_SIZE+(relZ+CACHE_RANGE);
        if (!brightnessCached[index]) {
            brightnessValue[index] = block.getBlockBrightness(access, relX + offsetX, relY + offsetY, relZ + offsetZ);
            brightnessCached[index] = true;
        }
        return brightnessValue[index];
    }
    public boolean getOpacity(int relX, int relY, int relZ) {
        int index = (relX+CACHE_RANGE)*CACHE_SIZE*CACHE_SIZE+(relY+CACHE_RANGE)*CACHE_SIZE+(relZ+CACHE_RANGE);
        if(!opacityCached[index]){
            opacityValue[index] = Block.opaqueCubeLookup[access.getBlockId(relX+ offsetX, relY+ offsetY, relZ+ offsetZ)];
            opacityCached[index] = true;
        }
        return opacityValue[index];
    }
}
