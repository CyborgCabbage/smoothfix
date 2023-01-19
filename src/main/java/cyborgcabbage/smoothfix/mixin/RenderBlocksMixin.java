package cyborgcabbage.smoothfix.mixin;

import cyborgcabbage.smoothfix.RenderBlockCache;
import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.MathHelper;
import net.minecraft.src.RenderBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = RenderBlocks.class, remap = false)
public abstract class RenderBlocksMixin {
    @Shadow private IBlockAccess blockAccess;
    @Shadow private int overrideBlockTexture = -1;
    @Shadow private boolean renderAllFaces = false;
    @Shadow public static boolean fancyGrass = true;
    @Shadow private boolean enableAO;
    @Shadow private int field_22352_G = 1;
    @Shadow private float colorRedTopLeft;
    @Shadow private float colorRedBottomLeft;
    @Shadow private float colorRedBottomRight;
    @Shadow private float colorRedTopRight;
    @Shadow private float colorGreenTopLeft;
    @Shadow private float colorGreenBottomLeft;
    @Shadow private float colorGreenBottomRight;
    @Shadow private float colorGreenTopRight;
    @Shadow private float colorBlueTopLeft;
    @Shadow private float colorBlueBottomLeft;
    @Shadow private float colorBlueBottomRight;
    @Shadow private float colorBlueTopRight;
    @Shadow public boolean overbright;

    @Shadow public abstract void renderBottomFace(Block block, double d, double d1, double d2, int i);
    @Shadow public abstract void renderTopFace(Block block, double d, double d1, double d2, int i);
    @Shadow public abstract void renderEastFace(Block block, double d, double d1, double d2, int i);
    @Shadow public abstract void renderNorthFace(Block block, double d, double d1, double d2, int i);
    @Shadow public abstract void renderWestFace(Block block, double d, double d1, double d2, int i);
    @Shadow public abstract void renderSouthFace(Block block, double d, double d1, double d2, int i);

    RenderBlockCache cache = new RenderBlockCache();
    /**
     * @author CyborgCabbage
     * @reason Some men just want to watch the world burn
     */
    @Overwrite
    public boolean renderStandardBlockWithAmbientOcclusion(Block block, int x, int y, int z, float x1, float y1, float z1) {
        this.enableAO = true;
        cache.setupCache(block, blockAccess, x, y, z);
        boolean notGrass = block != Block.grass;
        boolean somethingRendered = renderSide(block, x, y, z, x1, y1, z1, notGrass,0,
                0, -1, 0, (float)block.minY,
                0, 0, 1, (float)block.maxZ, (float)block.minZ,
                -1, 0, 0, 1-(float)block.minX, 1-(float)block.maxX
        );
        somethingRendered |= renderSide(block, x, y, z, x1, y1, z1, notGrass,1,
                0,1,0, 1-(float)block.maxY,
                0,0,1, (float)block.maxZ, (float)block.minZ,
                1,0,0, (float)block.maxX, (float)block.minX
        );
        somethingRendered |= renderSide(block, x, y, z, x1, y1, z1, notGrass,2,
                0,0,-1, (float)block.minZ,
                -1,0,0, 1-(float)block.minX, 1-(float)block.maxX,
                0,1,0, (float)block.maxY, (float)block.minY
        );
        somethingRendered |= renderSide(block, x, y, z, x1, y1, z1, notGrass,3,
                0,0,1, 1-(float)block.maxZ,
                0,1,0, (float)block.maxY, (float)block.minY,
                -1,0,0, 1-(float)block.minX, 1-(float)block.maxX
        );
        somethingRendered |= renderSide(block, x, y, z, x1, y1, z1, notGrass,4,
                -1,0,0, (float)block.minX,
                0,0,1, (float)block.maxZ, (float)block.minZ,
                0,1,0, (float)block.maxY, (float)block.minY
        );
        somethingRendered |= renderSide(block, x, y, z, x1, y1, z1, notGrass,5,
                1,0,0, 1-(float)block.maxX,
                0,0,1, (float)block.maxZ, (float)block.minZ,
                0,-1,0, 1-(float)block.minY, 1-(float)block.maxY
        );
        this.enableAO = false;
        return somethingRendered;
    }

    private static final float[] SIDE_LIGHT_MULTIPLIER = {0.5f, 1.f, 0.8f, 0.8f, 0.6f, 0.6f};

    final boolean renderSide(Block block, int x, int y, int z, float x1, float y1, float z1, boolean notGrass, int side,
                int dirX, int dirY, int dirZ, float depth,
                int topX, int topY, int topZ, float topP, float botP,
                int lefX, int lefY, int lefZ, float lefP, float rigP
    ){
        boolean rendered = false;
        boolean flag = (side == 1) || notGrass;
        if (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, x+dirX, y+dirY, z+dirZ, side)) {
            float lightTR;
            float lightBR;
            float lightBL;
            float lightTL;
           
            if (this.overbright) {
                lightTR = 1.0F;
                lightBR = 1.0F;
                lightBL = 1.0F;
                lightTL = 1.0F;
            } else if (this.field_22352_G <= 0) {
                lightTL = lightBL = lightBR = lightTR = cache.getBrightness(dirX, dirY, dirZ);
            } else {
                {
                    float dirB = cache.getBrightness(dirX, dirY, dirZ);
                    boolean lefT = cache.getOpacity(dirX + lefX, dirY + lefY, dirZ + lefZ);
                    boolean botT = cache.getOpacity(dirX - topX, dirY - topY, dirZ - topZ);
                    boolean topT = cache.getOpacity(dirX + topX, dirY + topY, dirZ + topZ);
                    boolean rigT = cache.getOpacity(dirX - lefX, dirY - lefY, dirZ - lefZ);
                    float lB = cache.getBrightness(dirX + lefX, dirY + lefY, dirZ + lefZ);
                    float bB = cache.getBrightness(dirX - topX, dirY - topY, dirZ - topZ);
                    float tB = cache.getBrightness(dirX + topX, dirY + topY, dirZ + topZ);
                    float rB = cache.getBrightness(dirX - lefX, dirY - lefY, dirZ - lefZ);
                    float blB = botT && lefT ? lB : cache.getBrightness(dirX + lefX - topX, dirY + lefY - topY, dirZ + lefZ - topZ);
                    float tlB = topT && lefT ? lB : cache.getBrightness(dirX + lefX + topX, dirY + lefY + topY, dirZ + lefZ + topZ);
                    float brB = botT && rigT ? rB : cache.getBrightness(dirX - lefX - topX, dirY - lefY - topY, dirZ - lefZ - topZ);
                    float trB = topT && rigT ? rB : cache.getBrightness(dirX - lefX + topX, dirY - lefY + topY, dirZ - lefZ + topZ);
                    lightTL = (tlB + lB + tB + dirB) / 4.0F;
                    lightTR = (tB + dirB + trB + rB) / 4.0F;
                    lightBR = (dirB + bB + rB + brB) / 4.0F;
                    lightBL = (lB + blB + dirB + bB) / 4.0F;
                }
                if(depth > 0.01){
                    float dirB = cache.getBrightness(0, 0, 0);
                    boolean lefT = cache.getOpacity(lefX, lefY, lefZ);
                    boolean botT = cache.getOpacity(-topX, -topY, -topZ);
                    boolean topT = cache.getOpacity(topX, topY, topZ);
                    boolean rigT = cache.getOpacity(-lefX, -lefY, -lefZ);
                    float lB = cache.getBrightness(lefX, lefY, lefZ);
                    float bB = cache.getBrightness(-topX, -topY, -topZ);
                    float tB = cache.getBrightness(topX, topY, topZ);
                    float rB = cache.getBrightness(-lefX, -lefY, -lefZ);
                    float blB = botT && lefT ? lB : cache.getBrightness(lefX - topX, lefY - topY, lefZ - topZ);
                    float tlB = topT && lefT ? lB : cache.getBrightness(lefX + topX, lefY + topY, lefZ + topZ);
                    float brB = botT && rigT ? rB : cache.getBrightness(-lefX - topX, -lefY - topY, -lefZ - topZ);
                    float trB = topT && rigT ? rB : cache.getBrightness(-lefX + topX, -lefY + topY, -lefZ + topZ);
                    lightTL = (tlB + lB + tB + dirB) / 4.0F * depth + lightTL*(1-depth);
                    lightTR = (tB + dirB + trB + rB) / 4.0F * depth + lightTR*(1-depth);
                    lightBR = (dirB + bB + rB + brB) / 4.0F * depth + lightBR*(1-depth);
                    lightBL = (lB + blB + dirB + bB) / 4.0F * depth + lightBL*(1-depth);
                }
            }

            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = (flag ? x1 : 1.0F) * SIDE_LIGHT_MULTIPLIER[side];
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = (flag ? y1 : 1.0F) * SIDE_LIGHT_MULTIPLIER[side];
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = (flag ? z1 : 1.0F) * SIDE_LIGHT_MULTIPLIER[side];
            float tl = topP * lightTL + (1 - topP) * lightBL;
            float tr = topP * lightTR + (1 - topP) * lightBR;
            float bl = botP * lightTL + (1 - botP) * lightBL;
            float br = botP * lightTR + (1 - botP) * lightBR;
            float ltl = lefP * tl + (1 - lefP) * tr;
            float lbl = lefP * bl + (1 - lefP) * br;
            float lbr = rigP * bl + (1 - rigP) * br;
            float ltr = rigP * tl + (1 - rigP) * tr;
            this.colorRedTopLeft *= ltl;
            this.colorGreenTopLeft *= ltl;
            this.colorBlueTopLeft *= ltl;
            this.colorRedBottomLeft *= lbl;
            this.colorGreenBottomLeft *= lbl;
            this.colorBlueBottomLeft *= lbl;
            this.colorRedBottomRight *= lbr;
            this.colorGreenBottomRight *= lbr;
            this.colorBlueBottomRight *= lbr;
            this.colorRedTopRight *= ltr;
            this.colorGreenTopRight *= ltr;
            this.colorBlueTopRight *= ltr;

            int tex;
            if (this.overbright) {
                tex = block.getBlockOverbright(this.blockAccess, x, y, z, side);
            } else {
                tex = block.getBlockTexture(this.blockAccess, x, y, z, side);
            }

            if (tex >= 0) {
                if(side == 0) {
                    this.renderBottomFace(block, x, y, z, tex);
                }else if(side == 1){
                    this.renderTopFace(block, x, y, z, tex);
                }else if(side == 2){
                    this.renderEastFace(block, x, y, z, tex);
                }else if(side == 3){
                    this.renderWestFace(block, x, y, z, tex);
                }else if(side == 4){
                    this.renderNorthFace(block, x, y, z, tex);
                }else if(side == 5){
                    this.renderSouthFace(block, x, y, z, tex);
                }
                rendered = true;
            }
            if (fancyGrass && tex == 3 && this.overrideBlockTexture < 0) {
                this.colorRedTopLeft *= x1;
                this.colorRedBottomLeft *= x1;
                this.colorRedBottomRight *= x1;
                this.colorRedTopRight *= x1;
                this.colorGreenTopLeft *= y1;
                this.colorGreenBottomLeft *= y1;
                this.colorGreenBottomRight *= y1;
                this.colorGreenTopRight *= y1;
                this.colorBlueTopLeft *= z1;
                this.colorBlueBottomLeft *= z1;
                this.colorBlueBottomRight *= z1;
                this.colorBlueTopRight *= z1;
                if(side == 2){
                    this.renderEastFace(block, x, y, z, Block.texCoordToIndex(6, 2));
                }else if(side == 3){
                    this.renderWestFace(block, x, y, z, Block.texCoordToIndex(6, 2));
                }else if(side == 4){
                    this.renderNorthFace(block, x, y, z, Block.texCoordToIndex(6, 2));
                }else if(side == 5){
                    this.renderSouthFace(block, x, y, z, Block.texCoordToIndex(6, 2));
                }
            }
        }
        return rendered;
    }
}