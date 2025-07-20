package hood.manager.acid.mixin.mixins.network;

import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CPacketUseEntity.class)
public interface IMixinCPacketUseEntity {
    @Accessor("entityId")
    void setEntityId(int id);

    @Accessor("action")
    void setAction(CPacketUseEntity.Action action);

    @Accessor("hand")
    void setHand(EnumHand hand);
}
