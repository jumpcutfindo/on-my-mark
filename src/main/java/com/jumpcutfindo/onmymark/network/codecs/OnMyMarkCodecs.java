package com.jumpcutfindo.onmymark.network.codecs;

import com.jumpcutfindo.onmymark.marker.BlockMarker;
import com.jumpcutfindo.onmymark.marker.EntityMarker;
import com.jumpcutfindo.onmymark.marker.Marker;
import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class OnMyMarkCodecs {
    public static final PacketCodec<PacketByteBuf, PartyMember> PARTY_MEMBER = new PartyMemberCodec();

    public static final PacketCodec<PacketByteBuf, Marker> MARKER = new MarkerCodec();
    public static final PacketCodec<PacketByteBuf, BlockMarker> BLOCK_MARKER = new BlockMarkerCodec();
    public static final PacketCodec<PacketByteBuf, EntityMarker> ENTITY_MARKER = new EntityMarkerCodec();
}
