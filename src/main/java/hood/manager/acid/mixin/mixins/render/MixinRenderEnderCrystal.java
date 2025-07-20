package hood.manager.acid.mixin.mixins.render;

import hood.manager.acid.Main;
import hood.manager.acid.event.events.RenderCrystalModelEvent;
import hood.manager.acid.module.modules.visual.CrystalChams;
import org.spongepowered.asm.mixin.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.model.*;
import net.minecraft.entity.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.client.renderer.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ RenderEnderCrystal.class })
public class MixinRenderEnderCrystal
{
    @Redirect(method = { "doRender" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void doRender(final ModelBase modelBase, final Entity entityIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale) {
        final RenderCrystalModelEvent event = new RenderCrystalModelEvent(modelBase, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        MinecraftForge.EVENT_BUS.post((Event)event);

        if (!Main.INSTANCE.moduleManager.isModuleEnabled("CrystalChams")) {
            modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.scale((float) CrystalChams.INSTANCE.scale.getValDouble(), (float)CrystalChams.INSTANCE.scale.getValDouble(), (float)CrystalChams.INSTANCE.scale.getValDouble());
        }
    }
}
