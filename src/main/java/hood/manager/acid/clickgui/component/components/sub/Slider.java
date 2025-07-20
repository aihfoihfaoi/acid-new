package hood.manager.acid.clickgui.component.components.sub;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

import hood.manager.acid.clickgui.component.Component;
import hood.manager.acid.clickgui.component.components.Button;
import hood.manager.acid.setting.Setting;
import hood.manager.acid.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class Slider extends Component {

	private boolean hovered;

	private Setting set;
	private Button parent;
	private int offset;
	private int x;
	private int y;
	private boolean dragging = false;

	private double renderWidth;
	
	public Slider(Setting value, Button button, int offset) {
		this.set = value;
		this.parent = button;
		this.x = button.parent.getX() + button.parent.getWidth();
		this.y = button.parent.getY() + button.offset;
		this.offset = offset;
	}
	
	@Override
	public void renderComponent() {
		Gui.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset, this.parent.parent.getX() + this.parent.parent.getWidth(), this.parent.parent.getY() + this.offset + 13, this.hovered ? 0xFF222222 : 0xFF111111);

		final int drag = (int)(this.set.getValDouble() / this.set.getMax() * this.parent.parent.getWidth());
		Gui.drawRect(this.parent.parent.getX() + 3, this.parent.parent.getY() + this.offset + 10, this.parent.parent.getX() + (int)this.renderWidth, this.parent.parent.getY() + this.offset + 11, new Color(0, 150, 150).getRGB());
		RenderUtil.drawRoundedRect(this.parent.parent.getX() + (int)this.renderWidth, this.parent.parent.getY() + this.offset + 9, 3.0, 3.0, 2.0, new Color(255, 255, 255));

		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.set.getName() , (parent.parent.getX() + 3), (parent.parent.getY() + offset + 1), -1);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(String.valueOf((int)this.set.getValDouble()), parent.parent.getX() + parent.parent.getWidth() - Minecraft.getMinecraft().fontRenderer.getStringWidth(String.valueOf((int)this.set.getValDouble())) - 3, (parent.parent.getY() + offset + 2), -1);
	}
	
	@Override
	public void setOff(int newOff) {
		offset = newOff;
	}
	
	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.hovered = isMouseOnButtonD(mouseX, mouseY) || isMouseOnButtonI(mouseX, mouseY);
		this.y = parent.parent.getY() + offset;
		this.x = parent.parent.getX();
		
		double diff = Math.min(84, Math.max(0, mouseX - this.x));

		double min = set.getMin();
		double max = set.getMax();
		
		renderWidth = 84 * (set.getValDouble() - min) / (max - min);
		
		if (dragging) {
			if (diff == 0) {
				set.setValDouble(set.getMin());
			}
			else {
				double newValue = roundToPlace(((diff / 84) * (max - min) + min), 2);
				set.setValDouble(newValue);
			}
		}
	}
	
	private static double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if(isMouseOnButtonD(mouseX, mouseY) && button == 0 && this.parent.open) {
			dragging = true;
		}
		if(isMouseOnButtonI(mouseX, mouseY) && button == 0 && this.parent.open) {
			dragging = true;
		}
	}
	
	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		dragging = false;
	}
	
	public boolean isMouseOnButtonD(int x, int y) {
		if(x > this.x && x < this.x + (parent.parent.getWidth() / 2 + 1) && y > this.y && y < this.y + 12) {
			return true;
		}
		return false;
	}
	
	public boolean isMouseOnButtonI(int x, int y) {
		if(x > this.x + parent.parent.getWidth() / 2 && x < this.x + parent.parent.getWidth() && y > this.y && y < this.y + 12) {
			return true;
		}
		return false;
	}
}
