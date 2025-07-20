package hood.manager.acid;

import hood.manager.acid.clickgui.ClickGui;
import hood.manager.acid.event.PrimaryListener;
import hood.manager.acid.font.FontManager;
import hood.manager.acid.module.ModuleManager;
import hood.manager.acid.newui.UI;
import hood.manager.acid.setting.SettingsManager;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.opengl.Display;

import java.awt.*;

@Mod(modid = "acid", name = "Acid", version = "beta")
public class Main {

    @Mod.Instance
    public static Main INSTANCE;

    public static final String ID = "acid";
    public static final String NAME = "Acid";
    public static final String VERSION = "beta";

    public static final char PREFIX = '$';
    public static final String LOG_PREFIX = "[" + ID + "] ";

    public Main() {
        INSTANCE = this;
    }

    private long start;

    public ModuleManager moduleManager;
    public PrimaryListener eventManager;
    public SettingsManager settingManager;
    public FontManager fontManager;
    public ClickGui ui;
    public UI newUI;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        start = System.currentTimeMillis();
        Display.setTitle("acidsense | " + System.getProperty("user.name"));

        fontManager = new FontManager();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        /*
        DO NOT CHANGE THE ORDER OF THESE.
         */
        settingManager = new SettingsManager();
        moduleManager = new ModuleManager();
        eventManager = new PrimaryListener();
        ui = new ClickGui();
        newUI = new UI();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        long finalTime = System.currentTimeMillis() - start;

        System.out.printf(LOG_PREFIX + "time taken: %d\n", finalTime);
    }
}
