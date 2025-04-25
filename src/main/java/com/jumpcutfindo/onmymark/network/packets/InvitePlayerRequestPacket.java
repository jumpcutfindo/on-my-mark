package com.jumpcutfindo.onmymark.network.packets;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Incoming packet from a party leader to invite a player
 */
public class InvitePlayerRequestPacket implements CustomPayload {
    public static final Id<InvitePlayerRequestPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "invite_player_request"));
    public static final PacketCodec<RegistryByteBuf, InvitePlayerRequestPacket> PACKET_CODEC = PacketCodec.of(InvitePlayerRequestPacket::write, InvitePlayerRequestPacket::new);

    private final String playerName;

    public InvitePlayerRequestPacket(PacketByteBuf buf) {
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
