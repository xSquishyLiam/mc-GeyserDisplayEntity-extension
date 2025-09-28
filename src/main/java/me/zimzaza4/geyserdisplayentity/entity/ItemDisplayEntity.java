package me.zimzaza4.geyserdisplayentity.entity;

import me.zimzaza4.geyserdisplayentity.GeyserDisplayEntity;
import me.zimzaza4.geyserdisplayentity.type.DisplayType;
import me.zimzaza4.geyserdisplayentity.util.DeltaUtils;
import me.zimzaza4.geyserdisplayentity.util.FileConfiguration;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataTypes;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerId;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.packet.MobArmorEquipmentPacket;
import org.cloudburstmc.protocol.bedrock.packet.MobEquipmentPacket;
import org.cloudburstmc.protocol.bedrock.packet.MoveEntityAbsolutePacket;
import org.geysermc.geyser.entity.EntityDefinition;
import org.geysermc.geyser.item.type.DyeableArmorItem;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.translator.item.ItemTranslator;
import org.geysermc.mcprotocollib.protocol.data.game.entity.metadata.EntityMetadata;
import org.geysermc.mcprotocollib.protocol.data.game.entity.metadata.type.ByteEntityMetadata;
import org.geysermc.mcprotocollib.protocol.data.game.item.ItemStack;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.CustomModelData;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponentTypes;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponents;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemDisplayEntity extends SlotDisplayEntity {

    private DisplayType displayType = DisplayType.NONE;
    private Byte color;
    private boolean custom = false;
    private boolean needHide = false;
    private double lastOffset = 0;

    public ItemDisplayEntity(GeyserSession session, int entityId, long geyserId, UUID uuid, EntityDefinition<?> definition, Vector3f position, Vector3f motion, float yaw, float pitch, float headYaw) {
        super(session, entityId, geyserId, uuid, definition, position, motion, yaw, pitch, headYaw);
    }

    public void setOffset(double offset) {
        moveRelative(0, offset - this.lastOffset, 0, this.pitch, this.yaw, false);
        this.lastOffset = offset;
    }

    public void setDisplayedItem(EntityMetadata<ItemStack, ?> entityMetadata) {
        ItemStack stack = entityMetadata.getValue();
        if (stack == null) {
            this.item = ItemData.AIR;
            setInvisible(true);
            return;
        }
    
        ItemData item = ItemTranslator.translateToBedrock(this.session, stack);
        this.item = item;
    
        if (item instanceof DyeableArmorItem) {
            DataComponents data = stack.getDataComponentsPatch();
            if (data != null) {
                Integer dyed = data.get(DataComponentTypes.DYED_COLOR);
                if (dyed != null) {
                    this.color = Byte.parseByte(String.valueOf(getColor(dyed)));
                }
            }
        }
    
        String type = this.session.getItemMappings().getMapping(stack.getId()).getJavaItem().javaIdentifier();
    
        CustomModelData modelData = null;
        DataComponents components = stack.getDataComponentsPatch();
        if (components != null) modelData = components.get(DataComponentTypes.CUSTOM_MODEL_DATA);

        FileConfiguration mappingsConfig = GeyserDisplayEntity.getExtension().getConfigManager().getConfig().getConfigurationSection("mappings");

        for (Object mappingKey : mappingsConfig.getRootNode().childrenMap().keySet()) {
            String mappingString = mappingKey.toString();
            FileConfiguration mappingConfig = mappingsConfig.getConfigurationSection(mappingString);
            if (mappingConfig == null) continue;
            if (!mappingConfig.getString("type").equals(type)) continue;

            if (mappingConfig.getInt("model-data") == -1) {
                this.config = mappingConfig.getConfigurationSection("displayentityoptions");
                setOffset(this.config.getDouble("y-offset"));
                break;
            }

            if (modelData != null && Math.abs(mappingConfig.getInt("model-data") - modelData.floats().get(0)) < 0.5) {
                this.config = mappingConfig.getConfigurationSection("displayentityoptions");
                setOffset(this.config.getDouble("y-offset"));
                break;
            }

            GeyserDisplayEntity.getExtension().logger().info("loaded " + mappingString + " Mapping");
        }
    
        if (!item.getDefinition().getIdentifier().startsWith("minecraft:")) {
            this.custom = true;
            if (this.color != null) {
                getDirtyMetadata().put(EntityDataTypes.COLOR, this.color);
            }
        } else {
            this.custom = false;
        }
    
        // HIDE_TYPES check only if stack is present (it is)
        String javaID = this.session.getItemMappings().getMapping(stack).getJavaItem().javaIdentifier();

        if (GeyserDisplayEntity.getExtension().getConfigManager().getConfig().getConfigurationSection("general").getStringList("hide-types").contains(javaID)) {
            setInvisible(true);
            this.needHide = true;
            this.dirtyMetadata.put(EntityDataTypes.SCALE, 0f);
        } else {
            this.needHide = false;
        }
    
        updateMainHand(this.session);
    }

    @Override
    protected void applyScale() {
        if (this.needHide) {
            this.dirtyMetadata.put(EntityDataTypes.SCALE, 0f);
        } else {
            super.applyScale();
        }
    }

    @Override
    public void updateMainHand(GeyserSession session) {
        if (!this.valid) return;

        ItemData helmet = ItemData.AIR; // TODO
        ItemData chest = this.item;

        if (this.custom && !config.getBoolean("hand")) {
            MobArmorEquipmentPacket armorEquipmentPacket = new MobArmorEquipmentPacket();
            armorEquipmentPacket.setRuntimeEntityId(this.geyserId);
            armorEquipmentPacket.setHelmet(helmet);
            armorEquipmentPacket.setBoots(ItemData.AIR);
            armorEquipmentPacket.setChestplate(chest);
            armorEquipmentPacket.setLeggings(ItemData.AIR);
            armorEquipmentPacket.setBody(chest); // WHY

            session.sendUpstreamPacket(armorEquipmentPacket);
        } else {
            MobEquipmentPacket handPacket = new MobEquipmentPacket();
            handPacket.setRuntimeEntityId(this.geyserId);
            handPacket.setItem(this.item);
            handPacket.setHotbarSlot(-1);
            handPacket.setInventorySlot(0);
            handPacket.setContainerId(ContainerId.INVENTORY);

            session.sendUpstreamPacket(handPacket);
        }
    }

    public void setDisplayType(ByteEntityMetadata entityMetadata) {
        int i = entityMetadata.getPrimitiveValue();
        this.displayType = DisplayType.values()[i];
    }

    private static int getColor(int argb) {
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;

        double[] colorLab = DeltaUtils.rgbToLab(r, g, b);

        List<int[]> colors = Arrays.asList(
                new int[]{249, 255, 254},    // 0: White
                new int[]{249, 128, 29},     // 1: Orange
                new int[]{199, 78, 189},     // 2: Magenta
                new int[]{58, 179, 218},     // 3: Light Blue
                new int[]{254, 216, 61},     // 4: Yellow
                new int[]{128, 199, 31},     // 5: Lime
                new int[]{243, 139, 170},    // 6: Pink
                new int[]{71, 79, 82},       // 7: Gray
                new int[]{159, 157, 151},    // 8: Light Gray
                new int[]{22, 156, 156},     // 9: Cyan
                new int[]{137, 50, 184},     // 10: Purple
                new int[]{60, 68, 170},      // 11: Blue
                new int[]{131, 84, 50},      // 12: Brown
                new int[]{94, 124, 22},      // 13: Green
                new int[]{176, 46, 38},      // 14: Red
                new int[]{29, 29, 33}        // 15: Black
        );

        int closestColorIndex = -1;
        double minDeltaE = Double.MAX_VALUE;

        for (int i = 0; i < colors.size(); i++) {
            int[] rgb = colors.get(i);
            double[] lab = DeltaUtils.rgbToLab(rgb[0], rgb[1], rgb[2]);
            double deltaE = DeltaUtils.calculateDeltaE(colorLab, lab);
            if (deltaE < minDeltaE) {
                minDeltaE = deltaE;
                closestColorIndex = i;
            }
        }

        return closestColorIndex;
    }

    public void moveAbsolute(Vector3f position, float yaw, float pitch, float headYaw, boolean isOnGround, boolean teleported) {
        double yOffset = this.config.getDouble("y-offset");
    
        position = position.clone().add(0, yOffset, 0);
    
        setPosition(position);
        setOnGround(isOnGround);
    
        MoveEntityAbsolutePacket moveEntityPacket = new MoveEntityAbsolutePacket();
        moveEntityPacket.setRuntimeEntityId(this.geyserId);
        moveEntityPacket.setPosition(position);
        moveEntityPacket.setRotation(getBedrockRotation());
        moveEntityPacket.setOnGround(isOnGround);
        moveEntityPacket.setTeleported(teleported);

        this.session.sendUpstreamPacket(moveEntityPacket);
    }
}
