package cyborgcabbage.smoothfix.mixin;

import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
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
    @Shadow private float lightValueOwn;
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

    /**
     * @author CyborgCabbage
     * @reason Some men just want to watch the world burn
     */
    @Overwrite

    public boolean renderStandardBlockWithAmbientOcclusion(Block block, int x, int y, int z, float x1, float y1, float z1) {
        this.enableAO = true;
        this.lightValueOwn = block.getBlockBrightness(this.blockAccess, x, y, z);
        float nooB = block.getBlockBrightness(this.blockAccess, x - 1, y, z);
        float onoB = block.getBlockBrightness(this.blockAccess, x, y - 1, z);
        float oonB = block.getBlockBrightness(this.blockAccess, x, y, z - 1);
        float pooB = block.getBlockBrightness(this.blockAccess, x + 1, y, z);
        float opoB = block.getBlockBrightness(this.blockAccess, x, y + 1, z);
        float oopB = block.getBlockBrightness(this.blockAccess, x, y, z + 1);
        boolean ppo = !Block.opaqueCubeLookup[this.blockAccess.getBlockId(x + 1, y + 1, z)];
        boolean pno = !Block.opaqueCubeLookup[this.blockAccess.getBlockId(x + 1, y - 1, z)];
        boolean pop = !Block.opaqueCubeLookup[this.blockAccess.getBlockId(x + 1, y, z + 1)];
        boolean pon = !Block.opaqueCubeLookup[this.blockAccess.getBlockId(x + 1, y, z - 1)];
        boolean npo = !Block.opaqueCubeLookup[this.blockAccess.getBlockId(x - 1, y + 1, z)];
        boolean nno = !Block.opaqueCubeLookup[this.blockAccess.getBlockId(x - 1, y - 1, z)];
        boolean non = !Block.opaqueCubeLookup[this.blockAccess.getBlockId(x - 1, y, z - 1)];
        boolean nop = !Block.opaqueCubeLookup[this.blockAccess.getBlockId(x - 1, y, z + 1)];
        boolean opp = !Block.opaqueCubeLookup[this.blockAccess.getBlockId(x, y + 1, z + 1)];
        boolean opn = !Block.opaqueCubeLookup[this.blockAccess.getBlockId(x, y + 1, z - 1)];
        boolean onp = !Block.opaqueCubeLookup[this.blockAccess.getBlockId(x, y - 1, z + 1)];
        boolean onn = !Block.opaqueCubeLookup[this.blockAccess.getBlockId(x, y - 1, z - 1)];
        boolean notGrass = block != Block.grass;
        boolean somethingRendered = renderSide(block, x,y,z,x1,y1,z1,notGrass,0,
                0, -1, 0, onoB,
                0, 0, 1, onp, onn,
                -1, 0, 0, nno, pno
        );
        somethingRendered |= renderSide(block, x,y,z,x1,y1,z1,notGrass,1,
                0,1,0, opoB,
                0,0,1, opp, opn,
                1,0,0, ppo, npo
        );
        somethingRendered |= renderSide(block, x,y,z,x1,y1,z1,notGrass,2,
                0,0,-1, oonB,
                -1,0,0, non, pon,
                0,1,0, opn, onn
        );
        somethingRendered |= renderSide(block, x,y,z,x1,y1,z1,notGrass,3,
                0,0,1, oopB,
                0,1,0, opp, onp,
                -1,0,0, nop, pop
        );
        somethingRendered |= renderSide(block, x,y,z,x1,y1,z1,notGrass,4,
                -1,0,0, nooB,
                0,0,1, nop, non,
                0,1,0, npo, nno
        );
        somethingRendered |= renderSide(block, x,y,z,x1,y1,z1,notGrass,5,
                1,0,0, pooB,
                0,0,1, pop, pon,
                0,-1,0, pno, ppo
        );
        this.enableAO = false;
        return somethingRendered;
    }

    private static final float[] SIDE_LIGHT_MULTIPLIER = {0.5f, 1.f, 0.7f, 0.7f, 0.7f, 0.7f};

    boolean renderSide(Block block, int x, int y, int z, float x1, float y1, float z1, boolean notGrass, int side,
                int dirX, int dirY, int dirZ, float dirB,
                int topX, int topY, int topZ, boolean topT, boolean botT,
                int lefX, int lefY, int lefZ, boolean lefT, boolean rigT
    ){
        int botX = -topX;
        int botY = -topY;
        int botZ = -topZ;
        int rigX = -lefX;
        int rigY = -lefY;
        int rigZ = -lefZ;
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
                lightTL = lightBL = lightBR = lightTR = dirB;
            } else {
                float lB = block.getBlockBrightness(this.blockAccess, x+dirX+lefX, y+dirY+lefY, z+dirZ+lefZ);
                float bB = block.getBlockBrightness(this.blockAccess, x+dirX+botX, y+dirY+botY, z+dirZ+botZ);
                float tB = block.getBlockBrightness(this.blockAccess, x+dirX+topX, y+dirY+topY, z+dirZ+topZ);
                float rB = block.getBlockBrightness(this.blockAccess, x+dirX+rigX, y+dirY+rigY, z+dirZ+rigZ);
                float blB;
                if (!botT && !lefT) {//Both opaque
                    blB = lB;
                } else {
                    blB = block.getBlockBrightness(this.blockAccess, x+dirX+lefX+botX, y+dirY+lefY+botY, z+dirZ+lefZ+botZ);
                }
                float tlB;
                if (!topT && !lefT) {
                    tlB = lB;
                } else {
                    tlB = block.getBlockBrightness(this.blockAccess, x+dirX+lefX+topX, y+dirY+lefY+topY, z+dirZ+lefZ+topZ);
                }
                float brB;
                if (!botT && !rigT) {
                    brB = rB;
                } else {
                    brB = block.getBlockBrightness(this.blockAccess, x+dirX+rigX+botX, y+dirY+rigY+botY, z+dirZ+rigZ+botZ);
                }
                float trB;
                if (!topT && !rigT) {
                    trB = rB;
                } else {
                    trB = block.getBlockBrightness(this.blockAccess, x+dirX+rigX+topX, y+dirY+rigY+topY, z+dirZ+rigZ+topZ);
                }

                lightTL = (tlB + lB + tB + dirB) / 4.0F;
                lightTR = (tB + dirB + trB + rB) / 4.0F;
                lightBR = (dirB + bB + rB + brB) / 4.0F;
                lightBL = (lB + blB + dirB + bB) / 4.0F;
            }

            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = (flag ? x1 : 1.0F) * SIDE_LIGHT_MULTIPLIER[side];
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = (flag ? y1 : 1.0F) * SIDE_LIGHT_MULTIPLIER[side];
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = (flag ? z1 : 1.0F) * SIDE_LIGHT_MULTIPLIER[side];
            this.colorRedTopLeft *= lightTL;
            this.colorGreenTopLeft *= lightTL;
            this.colorBlueTopLeft *= lightTL;
            this.colorRedBottomLeft *= lightBL;
            this.colorGreenBottomLeft *= lightBL;
            this.colorBlueBottomLeft *= lightBL;
            this.colorRedBottomRight *= lightBR;
            this.colorGreenBottomRight *= lightBR;
            this.colorBlueBottomRight *= lightBR;
            this.colorRedTopRight *= lightTR;
            this.colorGreenTopRight *= lightTR;
            this.colorBlueTopRight *= lightTR;
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
