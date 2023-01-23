package cyborgcabbage.smoothfix.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityRenderer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value= EntityRenderer.class, remap = false)
public class EntityRendererMixin {
    @Shadow private Minecraft mc;

    @Inject(method="renderWorld", at=@At(value="INVOKE", target = "Lnet/minecraft/src/RenderGlobal;sortAndRender(Lnet/minecraft/src/EntityLiving;ID)I", ordinal = 2))
    private void fixWater(float renderPartialTicks, long l, CallbackInfo ci){
        if(this.mc.gameSettings.ambientOcclusion.value) {
            GL11.glShadeModel(GL11.GL_SMOOTH);
        }
    }

    @Inject(method="renderWorld", at=@At(value="INVOKE_ASSIGN", target = "Lnet/minecraft/src/RenderGlobal;sortAndRender(Lnet/minecraft/src/EntityLiving;ID)I", ordinal = 2))
    private void fixWater2(float renderPartialTicks, long l, CallbackInfo ci){
        GL11.glShadeModel(GL11.GL_FLAT);
    }
}
