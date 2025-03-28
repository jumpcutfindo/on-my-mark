package com.jumpcutfindo.onmymark.network.packets;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class PartyInvitationDecisionPacket implements CustomPayload {
    public static final Id<PartyInvitationDecisionPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "party_invitation_decision"));
    public static final PacketCodec<RegistryByteBuf, PartyInvitationDecisionPacket> PACKET_CODEC = PacketCodec.of(PartyInvitationDecisionPacket::write, PartyInvitationDecisionPacket::new);

    private final boolean isAccept;

    public PartyInvitationDecisionPacket(PacketByteBuf buf) {
        this.isAccept = buf.readBoolean();
    }

    private void write(PacketByteBuf buf) {
        buf.writeBoolean(isAccept);
    }

    public boolean isAccept() {
        return isAccept;
    }

    public static PartyInvitationDecisionPacket create(boolean isAccept) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(isAccept);

        return new PartyInvitationDecisionPacket(buf);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
