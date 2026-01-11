package me.zimzaza4.geyserdisplayentity.entity;

import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataTypes;
import org.geysermc.geyser.entity.EntityDefinition;
import org.geysermc.geyser.entity.spawn.EntitySpawnContext;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.mcprotocollib.protocol.data.game.entity.metadata.type.IntEntityMetadata;

import java.util.UUID;

public class BlockDisplayEntity extends SlotDisplayEntity {

    // Do we even need this if we're never gonna use it?? - xSquishyLiam

    // TODO
    // Kastle hasnt finish it
    public BlockDisplayEntity(EntitySpawnContext entitySpawnContext) {
        super(entitySpawnContext);
    }

    public void setDisplayedBlockState(IntEntityMetadata blockState) {
        this.dirtyMetadata.put(EntityDataTypes.DISPLAY_BLOCK_STATE, this.session.getBlockMappings().getBedrockBlock(blockState.getPrimitiveValue()));
    }
}