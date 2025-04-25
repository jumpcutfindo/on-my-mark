package com.jumpcutfindo.onmymark.network.codecs;

import com.jumpcutfindo.onmymark.marker.EntityMarker;
import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.UUID;

public class EntityMarkerCodec implements PacketCodec<PacketByteBuf, EntityMarker> {
    @Override
    public EntityMarker decode(PacketByteBuf buf) {
        PartyMember partyMember = buf.readNullable(OnMyMarkCodecs.PARTY_MEMBER);
        UUID entityId = buf.readUuid();
        String entityName = buf.readString();

        return new EntityMarker(partyMember, entityId, entityName);
    }

    @Override
    public void encode(PacketByteBuf buf, EntityMarker marker) {
        buf.writeNullable(marker.owner(), OnMyMarkCodecs.PARTY_MEMBER);
        buf.writeUuid(marker.entityId());
        buf.writeString(marker.entityName());
    }
}
