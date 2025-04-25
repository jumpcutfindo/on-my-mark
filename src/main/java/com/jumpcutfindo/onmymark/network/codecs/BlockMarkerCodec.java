package com.jumpcutfindo.onmymark.network.codecs;

import com.jumpcutfindo.onmymark.marker.BlockMarker;
import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.block.Block;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class BlockMarkerCodec implements PacketCodec<PacketByteBuf, BlockMarker> {
    @Override
    public BlockMarker decode(PacketByteBuf buf) {
        PartyMember partyMember = buf.readNullable(OnMyMarkCodecs.PARTY_MEMBER);
        BlockPos blockPos = buf.readBlockPos();

        Identifier identifier = buf.readIdentifier();
        Block block = Registries.BLOCK.get(identifier);

        return new BlockMarker(partyMember, blockPos, block);
    }

    @Override
    public void encode(PacketByteBuf buf, BlockMarker marker) {
        buf.writeNullable(marker.owner(), OnMyMarkCodecs.PARTY_MEMBER);
        buf.writeBlockPos(marker.blockPos());
        buf.writeIdentifier(Registries.BLOCK.getId(marker.block()));
    }
}
