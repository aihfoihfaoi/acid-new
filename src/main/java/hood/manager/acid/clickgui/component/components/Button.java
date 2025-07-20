package hood.manager.acid.clickgui.component.components;

import java.awt.*;
import java.util.ArrayList;

import hood.manager.acid.Main;
import hood.manager.acid.clickgui.ClickGui;
import hood.manager.acid.clickgui.component.Component;
import hood.manager.acid.clickgui.component.Frame;
import hood.manager.acid.clickgui.component.components.sub.Checkbox;
import hood.manager.acid.clickgui.component.components.sub.Keybind;
import hood.manager.acid.clickgui.component.components.sub.ModeButton;
import hood.manager.acid.clickgui.component.components.sub.Slider;
import hood.manager.acid.module.Module;
import hood.manager.acid.setting.Setting;
import hood.manager.acid.util.FontUtil;
import hood.manager.acid.util.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class Button extends Component {

	public Module mod;
	public Frame parent;
	public int offset;
	private boolean isHovered;
	private ArrayList<Component> subcomponents;
	public boolean open;
	private int height;
	
	public Button(Module mod, Frame parent, int offset) {
		this.mod = mod;
		this.parent = parent;
		this.offset = offset;
		this.subcomponents = new ArrayList<Component>();
		this.open = false;
		height = 12;
		int opY = offset + 12;
		if(Main.INSTANCE.settingManager.getSettingsByMod(mod) != null) {
			for(Setting s : Main.INSTANCE.settingManager.getSettingsByMod(mod)){
				if(s.isCombo()){
					this.subcomponents.add(new ModeButton(s, this, mod, opY));
					opY += 12;
				}
				if(s.isSlider()){
					this.subcomponents.add(new Slider(s, this, opY));
					opY += 12;
				}
				if(s.isCheck()){
					this.subcomponents.add(new Checkbox(s, this, opY));
					opY += 12;
				}
			}
		}
		this.subcomponents.add(new Keybind(this, opY));
	}
	
	@Override
	public void setOff(int newOff) {
		offset = newOff;
		int opY = offset + 12;
		for(Component comp : this.subcomponents) {
			comp.setOff(opY);
			opY += 12;
		}
	}
	
	@Override
	public void renderComponent() {
		final long currentTime = System.currentTimeMillis();
		final long hoverDuration = 1000L;
		final int maxAlpha = 255;
		final int minAlpha = 80;
		final int alpha = (int)((Math.sin(currentTime % hoverDuration / (double)hoverDuration * Math.PI) + 1.0) / 2.0 * (maxAlpha - minAlpha) + minAlpha);

		if (isHovered) Gui.drawRect(parent.getX(), this.parent.getY() + this.offset, parent.getX() + parent.getWidth(), this.parent.getY() + 12 + this.offset,  (this.mod.isToggled() ? new Color(0, 150, 150).brighter().getRGB() : new Color(35, 35, 35).brighter().getRGB()));
		else Gui.drawRect(parent.getX(), this.parent.getY() + this.offset, parent.getX() + parent.getWidth(), this.parent.getY() + 12 + this.offset,  (this.mod.isToggled() ? new Color(0, 150, 150, alpha).getRGB() : this.open ? new Color(35, 35, 35).brighter().getRGB() : new Color(35, 35, 35).getRGB()));

		if (isHovered) {
			ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
			RenderUtil.drawRoundedRect(3, sr.getScaledHeight() - 42, Minecraft.getMinecraft().fontRenderer.getStringWidth(mod.getDescription()) + 5, 12, 4, new Color(25, 25, 25, 135));
			Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(mod.getDescription(), 6, sr.getScaledHeight() - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 31, -1);
		}


		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.mod.getName(), (parent.getX() + 2), (parent.getY() + offset + 2), -1);
		if(this.subcomponents.size() >= 1) {
			if (open) {
				GlStateManager.pushMatrix();
				GL11.glTranslated((this.parent.getX() + parent.getWidth() - 4), (parent.getY() + offset + 9), 1);
				GL11.glRotated(180, 0, 0, 1);
				Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("^", 0, 0, -1);
				GlStateManager.popMatrix();
			} else {
				GlStateManager.pushMatrix();
				GL11.glTranslated((parent.getX() + parent.getWidth() - 4), (parent.getY() + offset + 4), 1);
				GL11.glRotated(90, 0, 0, 1);
				Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("^", 0 ,0, -1);
				GlStateManager.popMatrix();
			}
		}
			//Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.open ? "[-]" : "[+]", (parent.getX() + parent.getWidth() - 16), (parent.getY() + offset + 2), -1);
		if(this.open) {
			if(!this.subcomponents.isEmpty()) {
				for(Component comp : this.subcomponents) {
					comp.renderComponent();
				}
				Gui.drawRect(this.parent.getX(), this.parent.getY() + this.offset + 12, this.parent.getX() + 1, this.parent.getY() + this.offset + (this.subcomponents.size() + 1) * 12 + 1, new Color(0, 150, 150, alpha).getRGB());
				Gui.drawRect(this.parent.getX() + this.parent.getWidth() - 1, this.parent.getY() + this.offset + 12, this.parent.getX() + this.parent.getWidth(), this.parent.getY() + this.offset + (this.subcomponents.size() + 1) * 12 + 1, new Color(0, 150, 150, alpha).getRGB());
			}
		}
	}
	
	@Override
	public int getHeight() {
		if(this.open) {
			return (12 * (this.subcomponents.size() + 1));
		}
		return 12;
	}
	
	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.isHovered = isMouseOnButton(mouseX, mouseY);
		if(!this.subcomponents.isEmpty()) {
			for(Component comp : this.subcomponents) {
				comp.updateComponent(mouseX, mouseY);
			}
		}
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if(isMouseOnButton(mouseX, mouseY) && button == 0) {
			this.mod.toggle();
		}
		if(isMouseOnButton(mouseX, mouseY) && button == 1) {
			this.open = !this.open;
			this.parent.refresh();
		}
		for(Component comp : this.subcomponents) {
			comp.mouseClicked(mouseX, mouseY, button);
		}
	}
	
	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		for(Component comp : this.subcomponents) {
			comp.mouseReleased(mouseX, mouseY, mouseButton);
		}
	}
	
	@Override
	public void keyTyped(char typedChar, int key) {
		for(Component comp : this.subcomponents) {
			comp.keyTyped(typedChar, key);
		}
	}
	
	public boolean isMouseOnButton(int x, int y) {
		if(x > parent.getX() && x < parent.getX() + parent.getWidth() && y > this.parent.getY() + this.offset && y < this.parent.getY() + 12 + this.offset) {
			return true;
		}
		return false;
	}
}
