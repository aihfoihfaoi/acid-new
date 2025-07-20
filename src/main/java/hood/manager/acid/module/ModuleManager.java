package hood.manager.acid.module;

import hood.manager.acid.module.modules.combat.Aura;
import hood.manager.acid.module.modules.combat.Surround;
import hood.manager.acid.module.modules.combat.autocrystal.AutoCrystal;
import hood.manager.acid.module.modules.combat.HoleFill;
import hood.manager.acid.module.modules.misc.*;
import hood.manager.acid.module.modules.movement.*;
import hood.manager.acid.module.modules.visual.*;
import hood.manager.acid.module.modules.visual.path.Path;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private static ArrayList<Module> modules = new ArrayList<>();

    public ModuleManager() {
        MinecraftForge.EVENT_BUS.register(this);

        modules.add(new Sprint());
        modules.add(new Watermark());
        modules.add(new Aura());
        modules.add(new ClickGUI());
        modules.add(new HoleFill());
        modules.add(new AutoCrystal());
        modules.add(new Velocity());
        modules.add(new FakePlayer());
        modules.add(new hood.manager.acid.module.modules.misc.ArrayList());
        modules.add(new Model());
        modules.add(new FastFall());
        modules.add(new NoSlow());
        modules.add(new HoleESP());
        modules.add(new Aspect());
        modules.add(new ViewModel());
        modules.add(new Skybox());
        modules.add(new Path());
        modules.add(new Cosmetics());
        modules.add(new ElytraFly());
        modules.add(new Surround());
        modules.add(new Strafe());
        modules.add(new CustomFont());
        modules.add(new TileESP());
        modules.add(new CrystalChams());
        modules.add(new PacketFly());
        modules.add(new NewUI());
    }

    public void onUpdate() {
        modules.stream().filter(Module::isToggled).forEach(Module::onUpdate);
    }

    @SubscribeEvent
    public void key(InputEvent.KeyInputEvent e) {
        if(Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null)
            return;

        try {
            if (Keyboard.isCreated() && Keyboard.getEventKeyState()) {
                int keyCode = Keyboard.getEventKey();
                if (keyCode <= 0)
                    return;
                for(Module m : modules) {
                    if (m.getKey() == keyCode)
                        m.toggle();
                }
            }
        } catch (Exception c) { c.printStackTrace(); }
    }

    public boolean isModuleEnabled(String name) {
        Module m = modules.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        assert m != null;
        return m.isToggled();
    }

    public ArrayList<Module> getModules() {
        return modules;
    }

    public static List<Module> getModulesInCategory(Category c){
        List<Module> modules = new ArrayList<Module>();

        for(Module m : ModuleManager.modules) {
            if(m.getCategory() == c)
                modules.add(m);
        }
        return modules;
    }

    public Module getModuleByName(String name) {
        for(Module m : ModuleManager.modules) {
            if(m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }
}
