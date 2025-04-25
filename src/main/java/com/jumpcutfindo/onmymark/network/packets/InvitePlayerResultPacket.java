package com.jumpcutfindo.onmymark.network.packets;

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
public class InvitePlayerResultPacket implements CustomPayload {
    public static final Id<InvitePlayerResultPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "invite_player_result"));
    public static final PacketCodec<RegistryByteBuf, InvitePlayerResultPacket> PACKET_CODEC = PacketCodec.of(InvitePlayerResultPacket::write, InvitePlayerResultPacket::new);

    private boolean isSuccessful;

    public InvitePlayerResultPacket(PacketByteBuf buf) {
        this.isSuccessful = buf.readBoolean();
    }

    private void write(PacketByteBuf buf) {
        buf.writeBoolean(isSuccessful);
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public static InvitePlayerResultPacket successful() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(true);

        return new InvitePlayerResultPacket(buf);
    }

    public static InvitePlayerResultPacket unsuccessful() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(false);

        return new InvitePlayerResultPacket(buf);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
