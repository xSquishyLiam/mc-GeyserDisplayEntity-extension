package me.zimzaza4.geyserdisplayentity.entity;

import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataTypes;
import org.geysermc.geyser.entity.EntityDefinition;
import org.geysermc.geyser.entity.type.FallingBlockEntity;
import org.geysermc.geyser.entity.type.MinecartEntity;
import org.geysermc.geyser.item.type.Item;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.mcprotocollib.protocol.data.game.entity.metadata.type.IntEntityMetadata;

import java.util.UUID;

public class BlockDisplayEntity extends SlotDisplayEntity {

    // TODO
    // Kastle hasnt finish it
    public BlockDisplayEntity(GeyserSession session, int entityId, long geyserId, UUID uuid, EntityDefinition<?> definition,
                              Vector3f position, Vector3f motion, float yaw, float pitch, float headYaw) {
        super(session, entityId, geyserId, uuid, definition, position, motion, yaw, pitch, headYaw);
    }

    public void setDisplayedBlockState(IntEntityMetadata blockState) {
        this.dirtyMetadata.put(EntityDataTypes.DISPLAY_BLOCK_STATE, this.session.getBlockMappings().getBedrockBlock(blockState.getPrimitiveValue()));
    }
}