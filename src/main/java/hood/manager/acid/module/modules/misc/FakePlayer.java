package hood.manager.acid.module.modules.misc;

import com.mojang.authlib.GameProfile;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;

public class FakePlayer extends Module {
    private EntityOtherPlayerMP player = null;

    public FakePlayer() {
        super("FakePlayer", "Spawns a fake player.", Category.MISC);
    }

    @Override
    protected void onEnable() {
        GameProfile profile = new GameProfile(mc.player.getUniqueID(), "hood manager");
        player = new EntityOtherPlayerMP(mc.world, profile);

        mc.world.spawnEntity(player);
        player.copyLocationAndAnglesFrom(mc.player);
    }

    @Override
    protected void onDisable() {
        if (player != null) mc.world.removeEntity(player);
    }
}
