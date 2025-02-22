package me.zimzaza4.geyserdisplayentity;

import net.elytrium.commons.config.YamlConfig;

import java.util.List;

public class Settings extends YamlConfig {
    @Ignore
    public static final Settings IMP = new Settings();

    @Comment("The y-offset of display entity")
    public double Y_OFFSET = -0.5;

    @Comment("Invisible item types")
    public List<String> HIDE_TYPES = List.of("minecraft:leather_horse_armor");
}
