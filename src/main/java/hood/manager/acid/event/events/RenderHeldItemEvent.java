package hood.manager.acid.event.events;

import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class RenderHeldItemEvent extends Event {
    private EnumHandSide side;

    public RenderHeldItemEvent(EnumHandSide side) {
        this.side = side;
    }

    public EnumHandSide getSide() {
        return side;
    }
}
