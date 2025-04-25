package com.jumpcutfindo.onmymark.network.packets.serverbound;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Incoming packet from a party leader to invite a player
 */
public class InvitePlayerRequestC2SPacket implements CustomPayload {
    public static final Id<InvitePlayerRequestC2SPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "invite_player_request"));
    public static final PacketCodec<RegistryByteBuf, InvitePlayerRequestC2SPacket> PACKET_CODEC = PacketCodec.of(InvitePlayerRequestC2SPacket::write, InvitePlayerRequestC2SPacket::new);

    private final String playerName;

    public InvitePlayerRequestC2SPacket(PacketByteBuf buf) {
        this.playerName = buf.readString();
    }

    private void write(PacketByteBuf buf) {
        buf.writeString(this.playerName);
    }

    public String playerName() {
        return playerName;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
