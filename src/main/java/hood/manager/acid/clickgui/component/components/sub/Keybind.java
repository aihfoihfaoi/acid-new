package hood.manager.acid.clickgui.component.components.sub;

import hood.manager.acid.clickgui.component.Component;
import hood.manager.acid.clickgui.component.components.Button;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class Keybind extends Component {

	private boolean hovered;
	private boolean binding;
	private Button parent;
	private int offset;
	private int x;
	private int y;
	
	public Keybind(Button button, int offset) {
		this.parent = button;
		this.x = button.parent.getX() + button.parent.getWidth();
		this.y = button.parent.getY() + button.offset;
		this.offset = offset;
	}
	
	@Override
	public void setOff(int newOff) {
		offset = newOff;
	}
	
	@Override
	public void renderComponent() {
		Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth()), parent.parent.getY() + offset + 13, this.hovered ? 0xFF222222 : 0xFF111111);
		Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX(), parent.parent.getY() + offset + 12, 0xFF111111);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(binding ? "Listening..." : "Key", parent.parent.getX() + 2, (parent.parent.getY() + offset + 2), -1);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(binding ? "" : (Keyboard.getKeyName(this.parent.mod.getKey())), (parent.parent.getX() + parent.parent.getWidth() - Minecraft.getMinecraft().fontRenderer.getStringWidth(Keyboard.getKeyName(this.parent.mod.getKey()))) - 2, (parent.parent.getY() + offset + 2), -1);
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
			this.binding = !this.binding;
		}
	}
	
	@Override
	public void keyTyped(char typedChar, int key) {
		if(this.binding) {
			if (key == Keyboard.KEY_BACK) {
				this.parent.mod.setKey(0);
				this.binding = false;
			} else {
			this.parent.mod.setKey(key);
				this.binding = false;
			}
		}
	}
	
	public boolean isMouseOnButton(int x, int y) {
		if(x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12) {
			return true;
		}
		return false;
	}
}
