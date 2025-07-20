package hood.manager.acid.clickgui.component.components.sub;

import hood.manager.acid.clickgui.component.Component;
import hood.manager.acid.clickgui.component.components.Button;
import hood.manager.acid.module.Module;
import hood.manager.acid.setting.Setting;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class ModeButton extends Component {

	private boolean hovered;
	private Button parent;
	private Setting set;
	private int offset;
	private int x;
	private int y;
	private Module mod;

	private int modeIndex;
	
	public ModeButton(Setting set, Button button, Module mod, int offset) {
		this.set = set;
		this.parent = button;
		this.mod = mod;
		this.x = button.parent.getX() + button.parent.getWidth();
		this.y = button.parent.getY() + button.offset;
		this.offset = offset;
		this.modeIndex = 0;
	}
	
	@Override
	public void setOff(int newOff) {
		offset = newOff;
	}
	
	@Override
	public void renderComponent() {
		Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth() * 1), parent.parent.getY() + offset + 12, this.hovered ? 0xFF222222 : 0xFF111111);
		Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, 0xFF111111);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(set.getName(), (parent.parent.getX() + 2), (parent.parent.getY() + offset + 2), -1);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(set.getValString(), (parent.parent.getX() + parent.parent.getWidth() - Minecraft.getMinecraft().fontRenderer.getStringWidth(set.getValString())) - 2, (parent.parent.getY() + offset + 2), -1);
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
			int maxIndex = set.getOptions().length;

			if(modeIndex + 1 >= maxIndex)
				modeIndex = 0;
			else
				modeIndex++;

			set.setValString(set.getOptions()[modeIndex]);
		}
	}
	
	public boolean isMouseOnButton(int x, int y) {
		if(x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12) {
			return true;
		}
		return false;
	}
}
