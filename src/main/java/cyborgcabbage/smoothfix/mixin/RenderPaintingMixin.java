package cyborgcabbage.smoothfix.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.function.BiConsumer;

@Mixin(value = RenderPainting.class, remap = false)
public abstract class RenderPaintingMixin extends Render{


    public void renderPainting(EntityPainting painting, double d, double d1, double d2, float f, float f1) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d, (float)d1, (float)d2);
        GL11.glRotatef(f, 0.0F, 1.0F, 0.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        int sm = GL11.glGetInteger(GL11.GL_SHADE_MODEL);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        this.loadTexture("/art/kz.png");
        EnumArt enumart = painting.art;
        float f2 = 0.0625F;
        GL11.glScalef(f2, f2, f2);
        this.renderPaintingModel(painting, enumart.sizeX, enumart.sizeY, enumart.offsetX, enumart.offsetY);
        GL11.glShadeModel(sm);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    private void renderPaintingModel(EntityPainting painting, int sizeX, int sizeY, int textureX, int textureY) {
        float originOffsetX = (float)(-sizeX) / 2.0F;
        float originOffsetY = (float)(-sizeY) / 2.0F;
        float nz = -0.5F;
        float pz = 0.5F;
        int TILES_X = sizeX / 16;
        int TILES_Y = sizeY / 16;
        float[] lightValues = new float[(TILES_X+2)*(TILES_Y+2)];
        for(int tileX = -1; tileX < (TILES_X+1); ++tileX) {
            for(int tileY = -1; tileY < (TILES_Y+1); ++tileY) {
                float x1 = originOffsetX + (float)(tileX * 16) + 8;
                float y1 = originOffsetY + (float)(tileY * 16) + 8;
                lightValues[(tileX+1)*(TILES_Y+2)+(tileY+1)] = this.getLighting(painting, x1, y1);
            }
        }

        for(int tileX = 0; tileX < TILES_X; ++tileX) {
            for(int tileY = 0; tileY < TILES_Y; ++tileY) {
                int finalTileX = tileX;
                int finalTileY = tileY;
                BiConsumer<Integer, Integer> consumer = (Integer xo, Integer yo) -> {
                    float light = 0;
                    light += lightValues[(finalTileX+xo)*(TILES_Y+2)+(finalTileY+yo)];
                    light += lightValues[(finalTileX +1+xo)*(TILES_Y+2)+(finalTileY+yo)];
                    light += lightValues[(finalTileX +1+xo)*(TILES_Y+2)+(finalTileY +1+yo)];
                    light += lightValues[(finalTileX+xo)*(TILES_Y+2)+(finalTileY +1+yo)];
                    light /= 4;
                    Tessellator.instance.setColorOpaque_F(light, light, light);
                };
                float x1 = originOffsetX + (float)((tileX + 1) * 16);
                float x0 = originOffsetX + (float)(tileX * 16);
                float y1 = originOffsetY + (float)((tileY + 1) * 16);
                float y0 = originOffsetY + (float)(tileY * 16);
                //this.updateLightingForPosition(painting, (x0 + x1) / 2.0F, (y0 + y1) / 2.0F);
                float tx0 = (float)(textureX + sizeX - tileX * 16) / 256.0F;
                float tx1 = (float)(textureX + sizeX - (tileX + 1) * 16) / 256.0F;
                float ty0 = (float)(textureY + sizeY - tileY * 16) / 256.0F;
                float ty1 = (float)(textureY + sizeY - (tileY + 1) * 16) / 256.0F;
                float f12 = 0.75F;
                float f13 = 0.8125F;
                float f14 = 0.0F;
                float f15 = 0.0625F;
                float f16 = 0.75F;
                float f17 = 0.8125F;
                float f18 = 1/512.f;
                float f19 = 1/512.f;
                float f20 = 0.7519531F;
                float f21 = 0.7519531F;
                float f22 = 0.0F;
                float f23 = 0.0625F;
                Tessellator tessellator = Tessellator.instance;
                tessellator.startDrawing(GL11.GL_TRIANGLES);
                //Front
                tessellator.setNormal(0.0F, 0.0F, -1.0F);
                consumer.accept(0,1);
                tessellator.addVertexWithUV(x0, y1, nz, tx0, ty1);
                consumer.accept(1,1);
                tessellator.addVertexWithUV(x1, y1, nz, tx1, ty1);
                consumer.accept(1,0);
                tessellator.addVertexWithUV(x1, y0, nz, tx1, ty0);

                consumer.accept(0,1);
                tessellator.addVertexWithUV(x0, y1, nz, tx0, ty1);
                consumer.accept(1,0);
                tessellator.addVertexWithUV(x1, y0, nz, tx1, ty0);
                consumer.accept(0,0);
                tessellator.addVertexWithUV(x0, y0, nz, tx0, ty0);

                /*
                //Back
                tessellator.setNormal(0.0F, 0.0F, 1.0F);
                tessellator.addVertexWithUV(x0, y0, pz, f12, f14);
                tessellator.addVertexWithUV(x1, y0, pz, f13, f14);
                tessellator.addVertexWithUV(x1, y1, pz, f13, f15);
                tessellator.addVertexWithUV(x0, y1, pz, f12, f15);
                //Bottom
                tessellator.setNormal(0.0F, -1.0F, 0.0F);
                tessellator.addVertexWithUV(x0, y0, nz, f16, f18);
                tessellator.addVertexWithUV(x1, y0, nz, f17, f18);
                tessellator.addVertexWithUV(x1, y0, pz, f17, f19);
                tessellator.addVertexWithUV(x0, y0, pz, f16, f19);
                //Top
                tessellator.setNormal(0.0F, 1.0F, 0.0F);
                tessellator.addVertexWithUV(x0, y1, pz, f16, f18);
                tessellator.addVertexWithUV(x1, y1, pz, f17, f18);
                tessellator.addVertexWithUV(x1, y1, nz, f17, f19);
                tessellator.addVertexWithUV(x0, y1, nz, f16, f19);
                //Left
                tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                tessellator.addVertexWithUV(x0, y0, pz, f21, f22);
                tessellator.addVertexWithUV(x0, y1, pz, f21, f23);
                tessellator.addVertexWithUV(x0, y1, nz, f20, f23);
                tessellator.addVertexWithUV(x0, y0, nz, f20, f22);
                //Right
                tessellator.setNormal(1.0F, 0.0F, 0.0F);
                tessellator.addVertexWithUV(x1, y0, nz, f21, f22);
                tessellator.addVertexWithUV(x1, y1, nz, f21, f23);
                tessellator.addVertexWithUV(x1, y1, pz, f20, f23);
                tessellator.addVertexWithUV(x1, y0, pz, f20, f22);
                */
                tessellator.draw();

            }
        }

    }

    private float getLighting(EntityPainting painting, float relX, float relY) {
        int x = MathHelper.floor_double(painting.posX);
        int y = MathHelper.floor_double(painting.posY + (double)(relY / 16.0F));
        int z = MathHelper.floor_double(painting.posZ);
        if (painting.direction == 0) {
            x = MathHelper.floor_double(painting.posX + (double)(relX / 16.0F));
        }

        if (painting.direction == 1) {
            z = MathHelper.floor_double(painting.posZ - (double)(relX / 16.0F));
        }

        if (painting.direction == 2) {
            x = MathHelper.floor_double(painting.posX - (double)(relX / 16.0F));
        }

        if (painting.direction == 3) {
            z = MathHelper.floor_double(painting.posZ + (double)(relX / 16.0F));
        }

        return this.renderManager.worldObj.getLightBrightness(x, y, z);
    }

    private void updateLightingForPosition(EntityPainting painting, float relX, float relY) {
        int i = MathHelper.floor_double(painting.posX);
        int j = MathHelper.floor_double(painting.posY + (double)(relY / 16.0F));
        int k = MathHelper.floor_double(painting.posZ);
        if (painting.direction == 0) {
            i = MathHelper.floor_double(painting.posX + (double)(relX / 16.0F));
        }

        if (painting.direction == 1) {
            k = MathHelper.floor_double(painting.posZ - (double)(relX / 16.0F));
        }

        if (painting.direction == 2) {
            i = MathHelper.floor_double(painting.posX - (double)(relX / 16.0F));
        }

        if (painting.direction == 3) {
            k = MathHelper.floor_double(painting.posZ + (double)(relX / 16.0F));
        }

        float light = this.renderManager.worldObj.getLightBrightness(i, j, k);
        if (Minecraft.getMinecraft().fullbright) {
            light = 1.0F;
        }

        GL11.glColor3f(light, light, light);
    }
    
    /**
     * @author CyborgCabbage
     * @reason This is just for testing
     */
    @Overwrite
    public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
        this.renderPainting((EntityPainting)entity, d, d1, d2, f, f1);
    }
}
