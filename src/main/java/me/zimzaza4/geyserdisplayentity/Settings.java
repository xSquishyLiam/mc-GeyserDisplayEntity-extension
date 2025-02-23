package me.zimzaza4.geyserdisplayentity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.elytrium.commons.config.YamlConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Settings extends YamlConfig {
    @Ignore
    public static final Settings IMP = new Settings();

    @Comment("General display options")
    public DisplayEntityOptions GENERAL = new DisplayEntityOptions();

    @Comment("Invisible item types")
    public List<String> HIDE_TYPES = List.of("minecraft:leather_horse_armor");

    @Comment("Mappings")
    public Map<String, DisplayEntityMapping> MAPPINGS = new HashMap<>(Map.of("example", new DisplayEntityMapping()));

    public static class DisplayEntityMapping {
        @Comment("Item type")
        public String TYPE = "minecraft:iron_sword";
        @Comment("Custom model data, -1 represent ignore custom model data")
        public int MODEL_DATA = -1;
        @Comment("options")
        public DisplayEntityOptions OPTIONS = new DisplayEntityOptions();
    }


    public static class DisplayEntityOptions {
        @Comment("The y-offset of display entity")
        public double Y_OFFSET = -0.5;
        @Comment("Use entity scale, instead of entity properties")
        public boolean VANILLA_SCALE = false;
        @Comment("Equip custom items (items with custom mapping) to hand")
        public boolean HAND = false;
    }

}
