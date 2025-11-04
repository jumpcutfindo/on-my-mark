package com.jumpcutfindo.onmymark.network.codecs;

import com.jumpcutfindo.onmymark.client.party.ClientPartyMember;
import com.jumpcutfindo.onmymark.party.PartyMember;
import com.jumpcutfindo.onmymark.server.party.ServerPartyMember;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
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
        int color = buf.readInt();
        boolean isPartyLeader = buf.readBoolean();
        PartyMember.State state = buf.readEnumConstant(PartyMember.State.class);

        // Convert to mutable property map
        var propertyMap = new PropertyMap(PacketCodecs.PROPERTY_MAP.decode(buf));
        GameProfile gameProfile = new GameProfile(playerId, playerName, propertyMap);

        return new ClientPartyMember(playerId, playerName, isPartyLeader, color, state, gameProfile);
    }

    @Override
    public void encode(PacketByteBuf buf, PartyMember partyMember) {
        buf.writeUuid(partyMember.id());
        buf.writeString(partyMember.displayName());
        buf.writeInt(partyMember.color());
        buf.writeBoolean(partyMember.isPartyLeader());
        buf.writeEnumConstant(partyMember.state());

        GameProfile gameProfile = new GameProfile(partyMember.id(), partyMember.displayName());
        if (partyMember instanceof ServerPartyMember) {
            gameProfile = ((ServerPartyMember) partyMember).player().getGameProfile();
        }

        PacketCodecs.PROPERTY_MAP.encode(buf, gameProfile.properties());
    }
}
