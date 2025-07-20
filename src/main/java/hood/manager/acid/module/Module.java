package hood.manager.acid.module;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class Module {
    protected static final Minecraft mc = Minecraft.getMinecraft();

    private String name;
    private String description;
    private Category category;
    private int key;
    private boolean toggled;

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getKey() {
        return key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean isToggled() {
        return toggled;
    }

    protected void enable() {
        onEnable();
        setToggled(true);
    }

    protected void disable() {
        onDisable();
        setToggled(false);
    }

    protected void onEnable() {}
    protected void onDisable() {}
    public void onUpdate() {}

    public void toggle() {
        if (!toggled) enable();
        else disable();
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;

        if (toggled) {
            MinecraftForge.EVENT_BUS.register(this);
        } else {
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }
}
