package me.zimzaza4.geyserdisplayentity.entity;

import me.zimzaza4.geyserdisplayentity.type.DisplayType;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataTypes;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerId;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.packet.MobArmorEquipmentPacket;
import org.cloudburstmc.protocol.bedrock.packet.MobEquipmentPacket;
import org.geysermc.geyser.entity.EntityDefinition;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.translator.item.ItemTranslator;
import org.geysermc.mcprotocollib.protocol.data.game.entity.metadata.EntityMetadata;
import org.geysermc.mcprotocollib.protocol.data.game.entity.metadata.type.ByteEntityMetadata;
import org.geysermc.mcprotocollib.protocol.data.game.item.ItemStack;

import java.util.UUID;

public class ItemDisplayEntity extends SlotDisplayEntity {
    private DisplayType displayType = DisplayType.NONE;
    private boolean custom = false;
    public ItemDisplayEntity(GeyserSession session, int entityId, long geyserId, UUID uuid,
                             EntityDefinition<?> definition,
                             Vector3f position, Vector3f motion, float yaw, float pitch, float headYaw) {
        super(session, entityId, geyserId, uuid, definition, position, motion, yaw, pitch, headYaw);
    }

    public void setDisplayedItem(EntityMetadata<ItemStack, ?> entityMetadata) {
        ItemData item = ItemTranslator.translateToBedrock(session, entityMetadata.getValue());
        this.item = item;
        if (!item.getDefinition().getIdentifier().startsWith("minecraft:")) {
            custom = true;
        } else {
            custom = false;
        }
        if (session.getItemMappings().getMapping(entityMetadata.getValue()).getJavaItem().javaIdentifier().contains("horse_armor")) {
            setInvisible(true);
            this.dirtyMetadata.put(EntityDataTypes.SCALE, 0f);
        }
        updateMainHand(session);
    }

    @Override
    public void updateMainHand(GeyserSession session) {

        if (!valid)
            return;

        ItemData helmet = ItemData.AIR; // TODO
        ItemData chest = item;

        if (custom) {
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
            handPacket.setRuntimeEntityId(geyserId);
            handPacket.setItem(item);
            handPacket.setHotbarSlot(-1);
            handPacket.setInventorySlot(0);
            handPacket.setContainerId(ContainerId.INVENTORY);

            session.sendUpstreamPacket(handPacket);
        }
    }

    public void setDisplayType(ByteEntityMetadata entityMetadata) {
        int i = entityMetadata.getPrimitiveValue();
        displayType = DisplayType.values()[i];
    }
}