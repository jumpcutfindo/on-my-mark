package com.jumpcutfindo.onmymark.network.packets.clientbound;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.network.codecs.OnMyMarkCodecs;
import com.jumpcutfindo.onmymark.client.party.ClientPartyMember;
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

/**
 * Outgoing packet for the invitation details to the player that is being invited
 */
public class InvitePlayerInvitationS2CPacket implements CustomPayload {
    public static final Id<InvitePlayerInvitationS2CPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "invite_player_invitation"));
    public static final PacketCodec<RegistryByteBuf, InvitePlayerInvitationS2CPacket> PACKET_CODEC = PacketCodec.of(InvitePlayerInvitationS2CPacket::write, InvitePlayerInvitationS2CPacket::new);

    private UUID partyId;
    private String partyName;
    private PartyMember partyLeader;

    public InvitePlayerInvitationS2CPacket(PacketByteBuf buf) {
        this.partyId = buf.readUuid();
        this.partyName = buf.readString();
        this.partyLeader = buf.readNullable(OnMyMarkCodecs.PARTY_MEMBER);
    }

    private void write(PacketByteBuf buf) {
        buf.writeUuid(this.partyId);
        buf.writeString(this.partyName);
        buf.writeNullable(this.partyLeader, OnMyMarkCodecs.PARTY_MEMBER);
    }

    public UUID partyId() {
        return partyId;
    }

    public PartyMember partyLeader() {
        return partyLeader;
    }

    public String partyName() {
        return partyName;
    }

    public static <T extends PartyMember> InvitePlayerInvitationS2CPacket create(Party<T> party) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(party.partyId());
        buf.writeString(party.partyName());
        buf.writeNullable(party.partyLeader(), OnMyMarkCodecs.PARTY_MEMBER);

        return new InvitePlayerInvitationS2CPacket(buf);
    }

    public PartyInvite<ClientPartyMember> toPartyInvite(World world, PlayerEntity player) {
        Party<ClientPartyMember> party = Party.withPartyId(partyId, partyName, (ClientPartyMember) partyLeader);

        ClientPartyMember invitee = new ClientPartyMember(player.getUuid(), player.getDisplayName().getString(), false, PartyMember.State.AVAILABLE);

        return new PartyInvite<>(party, (ClientPartyMember) partyLeader, invitee);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
