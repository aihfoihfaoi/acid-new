package hood.manager.acid.clickgui.component;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import hood.manager.acid.Main;
import hood.manager.acid.clickgui.ClickGui;
import hood.manager.acid.clickgui.component.components.Button;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import hood.manager.acid.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

public class Frame {

	public ArrayList<Component> components;
	public Category category;
	private boolean open;
	private int width;
	private int y;
	private int x;
	private int barHeight;
	private boolean isDragging;
	public int dragX;
	public int dragY;
	
	public Frame(Category cat) {
		this.components = new ArrayList<Component>();
		this.category = cat;
		this.width = 88;
		this.x = 5;
		this.y = 5;
		this.barHeight = 13;
		this.dragX = 0;
		this.open = false;
		this.isDragging = false;
		int tY = this.barHeight;
		
		/**
		 * 		public ArrayList<Module> getModulesInCategory(Category categoryIn){
		 * 			ArrayList<Module> mods = new ArrayList<Module>();
		 * 			for(Module m : this.modules){
		 * 				if(m.getCategory() == categoryIn)
		 * 					mods.add(m);
		 * 			}
		 * 			return mods;
		 * 		}
		 */
		
		for(Module mod : Main.INSTANCE.moduleManager.getModulesInCategory(category)) {
			Button modButton = new Button(mod, this, tY);
			this.components.add(modButton);
			tY += 12;
		}
	}
	
	public ArrayList<Component> getComponents() {
		return components;
	}
	
	public void setX(int newX) {
		this.x = newX;
	}
	
	public void setY(int newY) {
		this.y = newY;
	}
	
	public void setDrag(boolean drag) {
		this.isDragging = drag;
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public void setOpen(boolean open) {
		this.open = open;
	}
	
	public void renderFrame(FontRenderer fontRenderer) {
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution sr = new ScaledResolution(mc);

		RenderUtil.drawRoundedRect(this.x, this.y, this.width, this.barHeight, 4, new Color(25, 25, 25));
		RenderUtil.drawRoundedRect(this.x, this.y + 12, this.width, 1, 0, new Color(0, 150, 150));

		List<Module> modules = Main.INSTANCE.moduleManager.getModules().parallelStream()
				.filter(m -> m.getKey() != 0).collect(Collectors.toList());

		Optional<Module> max = modules.parallelStream().max(Comparator.comparing(m -> mc.fontRenderer.getStringWidth(m.getName() + " | " + Keyboard.getKeyName(m.getKey()))));
		float maxWidth = mc.fontRenderer.getStringWidth(max.get().getName() + " [" + Keyboard.getKeyName(max.get().getKey()) + "]");

		int x = (sr.getScaledWidth() / 2) + 120;
		int y = 5;

		RenderUtil.drawRoundedRect(x, y, maxWidth + 6, 25 + modules.size() * mc.fontRenderer.FONT_HEIGHT, 8, new Color(25, 25, 25));
		mc.fontRenderer.drawStringWithShadow("Keybinds", x + (maxWidth / 2) - (float) mc.fontRenderer.getStringWidth("Keybinds") / 2, y + 5, -1);
		RenderUtil.drawRoundedRect(x, y + 15, maxWidth + 6, 2, 0, new Color(0, 150, 150));

		mc.fontRenderer.drawStringWithShadow("Using acid " + TextFormatting.GRAY + "[beta]", 3, sr.getScaledHeight() - 21, -1);
		mc.fontRenderer.drawStringWithShadow("Logged in as " + TextFormatting.AQUA + Minecraft.getMinecraft().getSession().getUsername(), 3, sr.getScaledHeight() - 11, -1);

		int offset = 2;

		for (Module m : modules) {
			mc.fontRenderer.drawStringWithShadow(m.getName() + " [" + Keyboard.getKeyName(m.getKey()) + "]", x + 3, 20 + y + offset, m.isToggled() ? new Color(0, 150, 150).getRGB() : -1);
			offset += mc.fontRenderer.FONT_HEIGHT;
		}

		// jank bullshit, so I don't have to change the actual category entry
		char[] newName = this.category.name().toCharArray();

		for (int i = 0; i < newName.length; i++) {
			newName[i] = Character.toLowerCase(newName[i]);
		}
		newName[0] = Character.toUpperCase(newName[0]);

		fontRenderer.drawStringWithShadow(new String(newName), (this.x + 20), (this.y + 2.5f), -1);
		fontRenderer.drawStringWithShadow("[" + this.components.size() + "]", (this.x + 2), (this.y + 2.5f), -1);

		if(this.open) {
			if(!this.components.isEmpty()) {
				for(Component component : components) {
					component.renderComponent();
				}
			}
		}
	}
	
	public void refresh() {
		int off = this.barHeight;
		for(Component comp : components) {
			comp.setOff(off);
			off += comp.getHeight();
		}
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void updatePosition(int mouseX, int mouseY) {
		if(this.isDragging) {
			this.setX(mouseX - dragX);
			this.setY(mouseY - dragY);
		}
	}
	
	public boolean isWithinHeader(int x, int y) {
		if(x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.barHeight) {
			return true;
		}
		return false;
	}
}
