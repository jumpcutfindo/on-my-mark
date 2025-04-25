package com.jumpcutfindo.onmymark.network.packets.serverbound;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class KickPlayerC2SPacket implements CustomPayload {
    public static final Id<KickPlayerC2SPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "kick_player"));
    public static final PacketCodec<RegistryByteBuf, KickPlayerC2SPacket> PACKET_CODEC = PacketCodec.of(KickPlayerC2SPacket::write, KickPlayerC2SPacket::new);

    private final UUID playerId;

    public KickPlayerC2SPacket(PacketByteBuf buf) {
        this.playerId = buf.readUuid();
    }

    private void write(PacketByteBuf buf) {
        buf.writeUuid(this.playerId);
    }

    public UUID playerId() {
        return playerId;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
