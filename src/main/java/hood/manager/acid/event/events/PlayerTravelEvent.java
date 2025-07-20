package hood.manager.acid.event.events;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class PlayerTravelEvent extends Event {
    public float strafe;
    public float vertical;
    public float forward;

    public PlayerTravelEvent(final float strafe, final float vertical, final float forward) {
        this.strafe = strafe;
        this.vertical = vertical;
        this.forward = forward;
    }
}
