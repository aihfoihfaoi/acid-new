package hood.manager.acid.event.events;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.awt.*;

@Cancelable
public class RenderSkyEvent extends Event {
    private Color color;

    public void setColor(final Color sColor) {
        this.color = sColor;
    }

    public Color getColor() {
        return this.color;
    }
}
