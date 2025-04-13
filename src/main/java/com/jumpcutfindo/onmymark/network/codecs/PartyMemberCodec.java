package com.jumpcutfindo.onmymark.network.codecs;

import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.UUID;

public class PartyMemberCodec implements PacketCodec<PacketByteBuf, PartyMember> {
    public PartyMemberCodec() {
    }

    @Override
    public PartyMember decode(PacketByteBuf buf) {
        UUID playerId = buf.readUuid();
        String playerName = buf.readString();

        return new PartyMember(playerId, playerName);
    }

    @Override
    public void encode(PacketByteBuf buf, PartyMember partyMember) {
        buf.writeUuid(partyMember.player().getUuid());
        buf.writeString(partyMember.displayName());
    }
}
