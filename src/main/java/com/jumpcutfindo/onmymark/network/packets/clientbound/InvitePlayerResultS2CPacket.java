package com.jumpcutfindo.onmymark.network.packets.clientbound;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Result of the entire party request transaction
 */
public class InvitePlayerResultS2CPacket implements CustomPayload {
    public static final Id<InvitePlayerResultS2CPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "invite_player_result"));
    public static final PacketCodec<RegistryByteBuf, InvitePlayerResultS2CPacket> PACKET_CODEC = PacketCodec.of(InvitePlayerResultS2CPacket::write, InvitePlayerResultS2CPacket::new);

    private boolean isSuccessful;

    public InvitePlayerResultS2CPacket(PacketByteBuf buf) {
        this.isSuccessful = buf.readBoolean();
    }

    private void write(PacketByteBuf buf) {
        buf.writeBoolean(isSuccessful);
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public static InvitePlayerResultS2CPacket successful() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(true);

        return new InvitePlayerResultS2CPacket(buf);
    }

    public static InvitePlayerResultS2CPacket unsuccessful() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(false);

        return new InvitePlayerResultS2CPacket(buf);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
