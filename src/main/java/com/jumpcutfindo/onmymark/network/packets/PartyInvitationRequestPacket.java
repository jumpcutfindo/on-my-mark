package com.jumpcutfindo.onmymark.network.packets;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyInvite;
import com.jumpcutfindo.onmymark.party.PartyMember;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.UUID;

public class PartyInvitationRequestPacket implements CustomPayload {
    public static final Id<PartyInvitationRequestPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "party_invite"));
    public static final PacketCodec<RegistryByteBuf, PartyInvitationRequestPacket> PACKET_CODEC = PacketCodec.of(PartyInvitationRequestPacket::write, PartyInvitationRequestPacket::new);

    private UUID partyId;
    private UUID partyLeader;
    private String partyName;

    public PartyInvitationRequestPacket(PacketByteBuf buf) {
        this.partyId = buf.readUuid();
        this.partyLeader = buf.readUuid();
        this.partyName = buf.readString();
    }

    private void write(PacketByteBuf buf) {
        buf.writeUuid(this.partyId);
        buf.writeUuid(this.partyLeader);
        buf.writeString(this.partyName);
    }

    public UUID partyId() {
        return partyId;
    }

    public UUID partyLeader() {
        return partyLeader;
    }

    public String partyName() {
        return partyName;
    }

    public static PartyInvitationRequestPacket create(Party party) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(party.partyId());
        buf.writeUuid(party.partyLeader().player().getUuid());
        buf.writeString(party.partyName());

        return new PartyInvitationRequestPacket(buf);
    }

    public PartyInvite toPartyInvite(World world, PlayerEntity player) {
        PartyMember partyLeader = new PartyMember(world.getPlayerByUuid(this.partyLeader));
        Party party = Party.withPartyId(partyId, partyName, partyLeader);

        PartyMember invitee = new PartyMember(player);

        return new PartyInvite(party, partyLeader, invitee);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
