package hood.manager.acid.module.modules.combat;

import com.google.common.collect.Lists;
import hood.manager.acid.Main;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import hood.manager.acid.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Keyboard;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hood manager
 * @since 2024/02/10
 */

public class Aura extends Module {
    Setting onlyWeapons = new Setting("Only weapons", this, true);
    Setting range = new Setting("Range", this, 5, 1, 6, true);
    Setting swingHand = new Setting("Swing hand", this, "Main", "Main", "Off");

    public Aura() {
        super("Aura", "Automatically hits players and mobs that are close enough.", Category.COMBAT);

        Main.INSTANCE.settingManager.addSetting(onlyWeapons);
        Main.INSTANCE.settingManager.addSetting(range);
        Main.INSTANCE.settingManager.addSetting(swingHand);
    }

    @Override
    public void onUpdate() {
        if (mc.player != null && mc.world != null) {
            Map<Entity, Integer> orderMap = orderTargets(mc.world.loadedEntityList.stream()
                    .filter(entity -> entity != mc.player)
                    .filter(entity -> !entity.isDead)
                    .filter(entity -> mc.player.getDistance(entity) <= range.getValDouble())
                    .collect(Collectors.toList()));

            traverseMap(orderMap);
        }
    }

    private void traverseMap(Map<Entity, Integer> map) {
        List<Entity> defaultTargets = new ArrayList<>();
        List<Entity> elevatedTargets = new ArrayList<>();
        List<Entity> maximumTargets = new ArrayList<>();

        map.keySet().forEach(target -> {
            map.values().forEach(priority -> {
                switch (priority) {
                    case 0: defaultTargets.add(target); break;
                    case 1: elevatedTargets.add(target); break;
                    case 2: maximumTargets.add(target); break;
                    default: break;
                }
            });
        });

        attack(maximumTargets);
        attack(elevatedTargets);
        attack(defaultTargets);
    }

    public boolean isWeapon(Item item) {
        return (item instanceof ItemAxe || item instanceof ItemSword);
    }

    public void attack(List<Entity> targets) {
        targets.stream().sorted(Comparator.comparing(s -> mc.player.getDistance(s))).forEach(target -> {
            if (mc.player.getCooledAttackStrength(0) >= 1) {
                if (onlyWeapons.getValBoolean() && !isWeapon(mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem())) return;

                mc.playerController.attackEntity(mc.player, target);
                mc.player.swingArm(swingHand.getValString().equals("Main") ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
            }
        });
    }

    // <entity, priority>
    // 0 - default priority (animals by default)
    // 1 - elevated priority (mobs & players by default)
    // 2 - maximum priority (only players with more than 2 pieces of enchanted armour, iron, diamond or gold armour, more than 20f health)
    private static Map<Entity, Integer> orderTargets(List<Entity> targets) {
        Map<Entity, Integer> map = new HashMap<>();

        targets.stream().filter(Entity::isEntityAlive).forEach(target -> {
            if (target instanceof EntityAnimal) map.put(target, 0);
            if (target instanceof EntityMob) map.put(target, 1);
            if (target instanceof EntityPlayer) {
                float hp = ((EntityPlayer) target).getHealth();

                int armourMaterial = getArmourMaterial((EntityPlayer) target);
                boolean mixed = isMixedArmourSet((EntityPlayer) target); // mixed armour set

                int enchantedCount = 0;

                for (ItemStack item : target.getArmorInventoryList()) {
                    if (item.isItemEnchanted()) enchantedCount++;
                }

                if (armourMaterial <= 1) map.put(target, 1);
                if (mixed) map.put(target, 1);
                if (enchantedCount <= 2) map.put(target, 1);

                if (armourMaterial > 1) map.put(target, 2);
                if (enchantedCount > 2) map.put(target, 2);
                if (hp > 20.0f) map.put(target, 2);
            }
        });
        return map;
    }

    private static int getArmourMaterial(EntityPlayer player) {
        int armorMaterial = -1;

        for (ItemStack item : player.getArmorInventoryList()) {
            if (!(item.getItem() instanceof ItemArmor)) {
                return armorMaterial;
            }

            ItemArmor armorItem = (ItemArmor) item.getItem();

            switch (armorItem.getArmorMaterial()) {
                case LEATHER:
                    armorMaterial = 0;
                    break;
                case IRON:
                    armorMaterial = 1;
                    break;
                case DIAMOND:
                    armorMaterial = 2;
                    break;
                case GOLD:
                    armorMaterial = 3;
                    break;
                default:
                    return armorMaterial;
            }
        }

        return armorMaterial;
    }

    private static boolean isMixedArmourSet(EntityPlayer player) {
        Set<Integer> uniqueMaterials = new HashSet<>();

        for (ItemStack item : player.getArmorInventoryList()) {
            if (item.getItem() instanceof ItemArmor) {
                ItemArmor armorItem = (ItemArmor) item.getItem();
                uniqueMaterials.add(armorItem.getArmorMaterial().ordinal());
            }
        }

        return uniqueMaterials.size() > 1;
    }
}
