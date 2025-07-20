package hood.manager.acid.mixin.mixins.render;

import hood.manager.acid.event.events.PerspectiveEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    @Redirect(method = { "setupCameraTransform" }, at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onSetupCameraTransform(final float fovY, final float aspect, final float zNear, final float zFar) {
        final PerspectiveEvent event = new PerspectiveEvent(Minecraft.getMinecraft().displayWidth / (float)Minecraft.getMinecraft().displayHeight);
        MinecraftForge.EVENT_BUS.post((Event)event);
        Project.gluPerspective(fovY, event.getAspect(), zNear, zFar);
    }

    @Redirect(method = { "renderWorldPass" }, at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderWorldPass(final float fovY, final float aspect, final float zNear, final float zFar) {
        final PerspectiveEvent event = new PerspectiveEvent(Minecraft.getMinecraft().displayWidth / (float)Minecraft.getMinecraft().displayHeight);
        MinecraftForge.EVENT_BUS.post((Event)event);
        Project.gluPerspective(fovY, event.getAspect(), zNear, zFar);
    }

    @Redirect(method = { "renderCloudsCheck" }, at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderCloudsCheck(final float fovY, final float aspect, final float zNear, final float zFar) {
        final PerspectiveEvent event = new PerspectiveEvent(Minecraft.getMinecraft().displayWidth / (float)Minecraft.getMinecraft().displayHeight);
        MinecraftForge.EVENT_BUS.post((Event)event);
        Project.gluPerspective(fovY, event.getAspect(), zNear, zFar);
    }
}
