package com.jumpcutfindo.onmymark.network.codecs;

import com.jumpcutfindo.onmymark.client.party.ClientPartyMember;
import com.jumpcutfindo.onmymark.party.PartyMember;
import com.jumpcutfindo.onmymark.server.party.ServerPartyMember;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.UUID;

public class PartyMemberCodec implements PacketCodec<PacketByteBuf, PartyMember> {
    public PartyMemberCodec() {
    }

    @Override
    public PartyMember decode(PacketByteBuf buf) {
        UUID playerId = buf.readUuid();
        String playerName = buf.readString();
        boolean isPartyLeader = buf.readBoolean();
        PartyMember.State state = buf.readEnumConstant(PartyMember.State.class);

        GameProfile gameProfile = new GameProfile(playerId, playerName);
        gameProfile.getProperties().putAll(PacketCodecs.PROPERTY_MAP.decode(buf));

        return new ClientPartyMember(playerId, playerName, isPartyLeader, state, gameProfile);
    }

    @Override
    public void encode(PacketByteBuf buf, PartyMember partyMember) {
        buf.writeUuid(partyMember.id());
        buf.writeString(partyMember.displayName());
        buf.writeBoolean(partyMember.isPartyLeader());
        buf.writeEnumConstant(partyMember.state());

        GameProfile gameProfile = ((ServerPartyMember) partyMember).player().getGameProfile();
        PacketCodecs.PROPERTY_MAP.encode(buf, gameProfile.getProperties());
    }
}
