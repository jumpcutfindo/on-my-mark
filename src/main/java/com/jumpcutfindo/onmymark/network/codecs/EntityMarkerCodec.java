package com.jumpcutfindo.onmymark.network.codecs;

import com.jumpcutfindo.onmymark.marker.EntityMarker;
import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityMarkerCodec implements PacketCodec<PacketByteBuf, EntityMarker> {
    @Override
    public EntityMarker decode(PacketByteBuf buf) {
        PartyMember partyMember = buf.readNullable(OnMyMarkCodecs.PARTY_MEMBER);
        RegistryKey<World> worldRegistryKey = buf.readRegistryKey(RegistryKeys.WORLD);
        UUID entityId = buf.readUuid();
        String entityName = buf.readString();
        Vec3d lastPos = buf.readVec3d();

        return new EntityMarker(partyMember, worldRegistryKey, entityId, entityName, lastPos);
    }

    @Override
    public void encode(PacketByteBuf buf, EntityMarker marker) {
        buf.writeNullable(marker.owner(), OnMyMarkCodecs.PARTY_MEMBER);
        buf.writeRegistryKey(marker.worldRegistryKey());
        buf.writeUuid(marker.entityId());
        buf.writeString(marker.entityName());
        buf.writeVec3d(marker.lastPos());
    }
}
