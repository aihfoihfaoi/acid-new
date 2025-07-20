package hood.manager.acid.module.modules.movement;

import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import net.minecraft.util.MovementInput;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoSlow extends Module {
    public NoSlow() {
        super("NoSlow", "Removes slowdown during different actions.", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event) {
        if (mc.player.isHandActive() && !mc.player.isRiding()) {
            MovementInput input = event.getMovementInput();
            input.moveStrafe *= 5.0f;

            MovementInput input2 = event.getMovementInput();
            input2.moveForward *= 5.0f;
        }
    }
}
