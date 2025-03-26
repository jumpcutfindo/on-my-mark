package com.jumpcutfindo.onmymark.network.packets;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.party.Party;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class PartyInvitationRequestPacket implements CustomPayload {
    public static final Id<PartyInvitationRequestPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "party_invite"));
    public static final PacketCodec<RegistryByteBuf, PartyInvitationRequestPacket> PACKET_CODEC = PacketCodec.of(PartyInvitationRequestPacket::write, PartyInvitationRequestPacket::new);

    private UUID partyId;
    private String partyName;
    private String partyLeaderName;

    public PartyInvitationRequestPacket(PacketByteBuf buf) {
        this.partyId = buf.readUuid();
        this.partyName = buf.readString();
        this.partyLeaderName = buf.readString();
    }

    private void write(PacketByteBuf buf) {
        buf.writeUuid(this.partyId);
        buf.writeString(this.partyName);
        buf.writeString(this.partyLeaderName);
    }

    public UUID partyId() {
        return partyId;
    }

    public String partyName() {
        return partyName;
    }

    public String partyLeaderName() {
        return partyLeaderName;
    }

    public static PartyInvitationRequestPacket create(Party party) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(party.partyId());
        buf.writeString(party.partyName());
        buf.writeString(party.partyLeader().displayName());

        return new PartyInvitationRequestPacket(buf);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return null;
    }
}
