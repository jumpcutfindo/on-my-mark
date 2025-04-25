package com.jumpcutfindo.onmymark.network.packets.serverbound;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Decision of the player invited whether to join the party or not
 */
public class InvitePlayerDecisionC2SPacket implements CustomPayload {
    public static final Id<InvitePlayerDecisionC2SPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "invite_player_decision"));
    public static final PacketCodec<RegistryByteBuf, InvitePlayerDecisionC2SPacket> PACKET_CODEC = PacketCodec.of(InvitePlayerDecisionC2SPacket::write, InvitePlayerDecisionC2SPacket::new);

    private final boolean isAccept;

    public InvitePlayerDecisionC2SPacket(PacketByteBuf buf) {
        this.isAccept = buf.readBoolean();
    }

    private void write(PacketByteBuf buf) {
        buf.writeBoolean(isAccept);
    }

    public boolean isAccept() {
        return isAccept;
    }

    public static InvitePlayerDecisionC2SPacket create(boolean isAccept) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(isAccept);

        return new InvitePlayerDecisionC2SPacket(buf);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
