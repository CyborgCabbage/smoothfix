package cyborgcabbage.smoothfix.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderPainting.class, remap = false)
public abstract class RenderPaintingMixin extends Render{
    private float getSmoothLight(float[] lightValues, boolean[] opacityValues, int TILES_Y, int tileX, int tileY, Integer xo, Integer yo) {
        float[] l = {0,0,0,0};
        for(int xx = 0; xx < 2; xx++){
            for(int yy = 0; yy < 2; yy++){
                int side0 = (tileX+xo+(1-xx))*(TILES_Y+2)+(tileY+yo+yy);
                int side1 = (tileX+xo+xx)*(TILES_Y+2)+(tileY+yo+(1-yy));
                if(xo == xx && yo == yy && opacityValues[side0] && opacityValues[side1]){
                    l[xx*2+yy] = lightValues[side0];
                }else{
                    l[xx*2+yy] = lightValues[(tileX+xo+xx)*(TILES_Y+2)+(tileY+yo+yy)];
                }
            }
        }
        return (l[0]+l[1]+l[2]+l[3])/4.f;
    }

    @Inject(method="func_159_a", at=@At("HEAD"),cancellable = true)
    private void renderModel(EntityPainting painting, int sizeX, int sizeY, int textureX, int textureY, CallbackInfo ci){
        if(Minecraft.isAmbientOcclusionEnabled()) {
            int shadeModel = GL11.glGetInteger(GL11.GL_SHADE_MODEL);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            float originOffsetX = (float)(-sizeX) / 2.0F;
            float originOffsetY = (float)(-sizeY) / 2.0F;
            float nz = -0.5F;
            float pz = 0.5F;
            int TILES_X = sizeX / 16;
            int TILES_Y = sizeY / 16;
            float[] lightValues = new float[(TILES_X+2)*(TILES_Y+2)];
            boolean[] opacityValues = new boolean[(TILES_X+2)*(TILES_Y+2)];
            for(int tileX = -1; tileX < (TILES_X+1); ++tileX) {
                for(int tileY = -1; tileY < (TILES_Y+1); ++tileY) {
                    float x1 = originOffsetX + (float)(tileX * 16) + 8;
                    float y1 = originOffsetY + (float)(tileY * 16) + 8;
                    lightValues[(tileX+1)*(TILES_Y+2)+(tileY+1)] = this.getLighting(painting, x1, y1);
                    opacityValues[(tileX+1)*(TILES_Y+2)+(tileY+1)] = this.getOpacity(painting, x1, y1);
                }
            }
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawing(GL11.GL_TRIANGLES);
            for(int tileX = 0; tileX < TILES_X; ++tileX) {
                for(int tileY = 0; tileY < TILES_Y; ++tileY) {
                    float x1 = originOffsetX + (float)((tileX + 1) * 16);
                    float x0 = originOffsetX + (float)(tileX * 16);
                    float y1 = originOffsetY + (float)((tileY + 1) * 16);
                    float y0 = originOffsetY + (float)(tileY * 16);
                    float tx0 = (float)(textureX + sizeX - tileX * 16) / 256.0F;
                    float tx1 = (float)(textureX + sizeX - (tileX + 1) * 16) / 256.0F;
                    float ty0 = (float)(textureY + sizeY - tileY * 16) / 256.0F;
                    float ty1 = (float)(textureY + sizeY - (tileY + 1) * 16) / 256.0F;
                    float bx0 = 0.75F;
                    float bx1 = 0.8125F;
                    float by0 = 0.0F;
                    float by1 = 0.0625F;

                    float l00 = getSmoothLight(lightValues, opacityValues, TILES_Y, tileX, tileY, 0, 0);
                    float l10 = getSmoothLight(lightValues, opacityValues, TILES_Y, tileX, tileY, 1, 0);
                    float l11 = getSmoothLight(lightValues, opacityValues, TILES_Y, tileX, tileY, 1, 1);
                    float l01 = getSmoothLight(lightValues, opacityValues, TILES_Y, tileX, tileY, 0, 1);
                    //Front
                    tessellator.setNormal(0.0F, 0.0F, -1.0F);
                    tessellator.setColorOpaque_F(l01,l01,l01);
                    tessellator.addVertexWithUV(x0, y1, nz, tx0, ty1);
                    tessellator.setColorOpaque_F(l11,l11,l11);
                    tessellator.addVertexWithUV(x1, y1, nz, tx1, ty1);
                    tessellator.setColorOpaque_F(l10,l10,l10);
                    tessellator.addVertexWithUV(x1, y0, nz, tx1, ty0);

                    tessellator.setColorOpaque_F(l01,l01,l01);
                    tessellator.addVertexWithUV(x0, y1, nz, tx0, ty1);
                    tessellator.setColorOpaque_F(l10,l10,l10);
                    tessellator.addVertexWithUV(x1, y0, nz, tx1, ty0);
                    tessellator.setColorOpaque_F(l00,l00,l00);
                    tessellator.addVertexWithUV(x0, y0, nz, tx0, ty0);
                    //Back
                    tessellator.setNormal(0.0F, 0.0F, 1.0F);
                    tessellator.setColorOpaque_F(l00,l00,l00);
                    tessellator.addVertexWithUV(x0, y0, pz, bx0, by0);
                    tessellator.setColorOpaque_F(l10,l10,l10);
                    tessellator.addVertexWithUV(x1, y0, pz, bx1, by0);
                    tessellator.setColorOpaque_F(l11,l11,l11);
                    tessellator.addVertexWithUV(x1, y1, pz, bx1, by1);

                    tessellator.setColorOpaque_F(l00,l00,l00);
                    tessellator.addVertexWithUV(x0, y0, pz, bx0, by0);
                    tessellator.setColorOpaque_F(l11,l11,l11);
                    tessellator.addVertexWithUV(x1, y1, pz, bx1, by1);
                    tessellator.setColorOpaque_F(l01,l01,l01);
                    tessellator.addVertexWithUV(x0, y1, pz, bx0, by1);
                }
            }
            tessellator.draw();
            GL11.glShadeModel(shadeModel);
            tessellator.startDrawingQuads();
            for(int tileX = 0; tileX < TILES_X; ++tileX) {
                for(int tileY = 0; tileY < TILES_Y; ++tileY) {
                    float x1 = originOffsetX + (float)((tileX + 1) * 16);
                    float x0 = originOffsetX + (float)(tileX * 16);
                    float y1 = originOffsetY + (float)((tileY + 1) * 16);
                    float y0 = originOffsetY + (float)(tileY * 16);
                    float light = lightValues[(tileX+1)*(TILES_Y+2)+(tileY+1)];
                    tessellator.setColorOpaque_F(light,light,light);
                    float f16 = 0.75F;
                    float f17 = 0.8125F;
                    float f18 = 1/512.f;
                    float f19 = 1/512.f;
                    float f20 = 0.7519531F;
                    float f21 = 0.7519531F;
                    float f22 = 0.0F;
                    float f23 = 0.0625F;
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
                }
            }
            tessellator.draw();
            ci.cancel();
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

    private boolean getOpacity(EntityPainting painting, float relX, float relY) {
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
        return Block.opaqueCubeLookup[this.renderManager.worldObj.getBlockId(x,y,z)];
    }
}
