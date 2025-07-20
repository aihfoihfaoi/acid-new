package hood.manager.acid.clickgui.component.components.sub;

import hood.manager.acid.clickgui.component.Component;
import hood.manager.acid.clickgui.component.components.Button;
import hood.manager.acid.setting.Setting;
import hood.manager.acid.util.RenderUtil;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class Checkbox extends Component {

	private boolean hovered;
	private Setting op;
	private Button parent;
	private int offset;
	private int x;
	private int y;
	
	public Checkbox(Setting option, Button button, int offset) {
		this.op = option;
		this.parent = button;
		this.x = button.parent.getX() + button.parent.getWidth();
		this.y = button.parent.getY() + button.offset;
		this.offset = offset;
	}

	@Override
	public void renderComponent() {
		Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth() * 1), parent.parent.getY() + offset + 12, this.hovered ? 0xFF222222 : 0xFF111111);
		Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + parent.parent.getWidth(), parent.parent.getY() + offset + 16, 0xFF111111);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.op.getName(), (parent.parent.getX() + 2), (parent.parent.getY() + offset + 2), -1);
		RenderUtil.drawRoundedRect(parent.parent.getX() + parent.parent.getWidth() - 13, parent.parent.getY() + offset + 2, 10, 8, 3, op.getValBoolean() ? new Color(0, 150, 150) : new Color(70, 70, 70));
	}
	
	@Override
	public void setOff(int newOff) {
		offset = newOff;
	}
	
	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.hovered = isMouseOnButton(mouseX, mouseY);
		this.y = parent.parent.getY() + offset;
		this.x = parent.parent.getX();
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if(isMouseOnButton(mouseX, mouseY) && button == 0 && this.parent.open) {
			this.op.setValBoolean(!op.getValBoolean());
		}
	}
	
	public boolean isMouseOnButton(int x, int y) {
		if(x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12) {
			return true;
		}
		return false;
	}
}
