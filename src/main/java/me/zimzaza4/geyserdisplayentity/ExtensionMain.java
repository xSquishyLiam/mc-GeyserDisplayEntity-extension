package me.zimzaza4.geyserdisplayentity;

import me.zimzaza4.geyserdisplayentity.entity.BlockDisplayEntity;
import me.zimzaza4.geyserdisplayentity.entity.ItemDisplayEntity;
import me.zimzaza4.geyserdisplayentity.entity.SlotDisplayEntity;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.event.lifecycle.GeyserPostInitializeEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserPreInitializeEvent;
import org.geysermc.geyser.api.extension.Extension;
import org.geysermc.geyser.entity.EntityDefinition;
import org.geysermc.geyser.entity.properties.GeyserEntityProperties;
import org.geysermc.geyser.entity.type.Entity;
import org.geysermc.geyser.registry.Registries;
import org.geysermc.mcprotocollib.protocol.data.game.entity.metadata.MetadataType;
import org.geysermc.mcprotocollib.protocol.data.game.entity.type.EntityType;

import java.util.ArrayList;
import java.util.List;

public class ExtensionMain implements Extension {
    private static EntityDefinition<ItemDisplayEntity> ITEM_DISPLAY;
    private static EntityDefinition<BlockDisplayEntity> BLOCK_DISPLAY;


    // From Kastle's Geyser branch
    @Subscribe
    public void onLoad(GeyserPostInitializeEvent event) {
        EntityDefinition<Entity> entityBase = EntityDefinition.builder(Entity::new)
                .addTranslator(MetadataType.BYTE, Entity::setFlags)
                .addTranslator(MetadataType.INT, Entity::setAir) // Air/bubbles
                .addTranslator(MetadataType.OPTIONAL_CHAT, Entity::setDisplayName)
                .addTranslator(MetadataType.BOOLEAN, Entity::setDisplayNameVisible)
                .addTranslator(MetadataType.BOOLEAN, Entity::setSilent)
                .addTranslator(MetadataType.BOOLEAN, Entity::setGravity)
                .addTranslator(MetadataType.POSE, (entity, entityMetadata) -> entity.setPose(entityMetadata.getValue()))
                .addTranslator(MetadataType.INT, Entity::setFreezing)
                .build();

        GeyserEntityProperties.Builder displayPropBuilder = new GeyserEntityProperties.Builder()
                .addInt("geyser:s_id")
                .addInt("geyser:s_int")
                .addFloat("geyser:i_dur")
                .addFloat("geyser:r_x", -180F, 180F)
                .addFloat("geyser:r_y", -180F, 180F)
                .addFloat("geyser:r_z", -180F, 180F)
                .addFloat("geyser:t_x")
                .addFloat("geyser:t_y")
                .addFloat("geyser:t_z")
                .addFloat("geyser:s_x")
                .addFloat("geyser:s_y")
                .addFloat("geyser:s_z")
                .addFloat("geyser:s_q");
        EntityDefinition<SlotDisplayEntity> slotDisplayBase = EntityDefinition.inherited(SlotDisplayEntity::new, entityBase)
                .addTranslator(null) // Interpolation start ticks
                .addTranslator(null) // Interpolation duration ID
                .addTranslator(null) // Position/Rotation interpolation duration
                .addTranslator(MetadataType.VECTOR3, SlotDisplayEntity::setTranslation) // Translation
                .addTranslator(MetadataType.VECTOR3, SlotDisplayEntity::setScale) // Scale
                .addTranslator(MetadataType.QUATERNION, SlotDisplayEntity::setLeftRotation) // Left rotation
                .addTranslator(MetadataType.QUATERNION, SlotDisplayEntity::setRightRotation) // Right rotation
                .addTranslator(null) // Billboard render constraints
                .addTranslator(null) // Brightness override
                .addTranslator(null) // View range
                .addTranslator(null) // Shadow radius
                .addTranslator(null) // Shadow strength
                .addTranslator(null) // Width
                .addTranslator(null) // Height
                .addTranslator(null) // Glow color override
                .build();
        BLOCK_DISPLAY = EntityDefinition.inherited(BlockDisplayEntity::new, slotDisplayBase)
                .type(EntityType.BLOCK_DISPLAY)
                .height(1.975f).width(0.2f)
                .registeredProperties(displayPropBuilder.build())
                .identifier("geyser:block_display")
                .addTranslator(MetadataType.BLOCK_STATE, BlockDisplayEntity::setDisplayedBlockState)
                .build();
        ITEM_DISPLAY = EntityDefinition.inherited(ItemDisplayEntity::new, slotDisplayBase)
                .type(EntityType.ITEM_DISPLAY)
                .height(1.975f).width(0.2f)
                .registeredProperties(displayPropBuilder.build())
                .identifier("geyser:item_display")
                .addTranslator(MetadataType.ITEM, ItemDisplayEntity::setDisplayedItem)
                .addTranslator(MetadataType.BYTE, ItemDisplayEntity::setDisplayType)
                .build();

        Registries.ENTITY_DEFINITIONS.register(EntityType.BLOCK_DISPLAY, BLOCK_DISPLAY);
        Registries.ENTITY_DEFINITIONS.register(EntityType.ITEM_DISPLAY, ITEM_DISPLAY);
        registerIdentifier(ITEM_DISPLAY.identifier());
        registerIdentifier(BLOCK_DISPLAY.identifier());
    }

    public void registerIdentifier(String id) {
        NbtMap registry = Registries.BEDROCK_ENTITY_IDENTIFIERS.get();
        List<NbtMap> idList = new ArrayList<>(registry.getList("idlist", NbtType.COMPOUND));
        idList.add(NbtMap.builder()
                .putString("id", id)
                .putString("bid", "")
                .putBoolean("hasspawnegg", false)
                .putInt("rid", idList.size() + 1)
                .putBoolean("summonable", false).build()
        );

        Registries.BEDROCK_ENTITY_IDENTIFIERS.set(NbtMap.builder()
                .putList("idlist", NbtType.COMPOUND, idList).build()
        );
    }

}
