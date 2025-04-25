package com.jumpcutfindo.onmymark.network.packets;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class KickPlayerPacket implements CustomPayload {
    public static final Id<KickPlayerPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "kick_player"));
    public static final PacketCodec<RegistryByteBuf, KickPlayerPacket> PACKET_CODEC = PacketCodec.of(KickPlayerPacket::write, KickPlayerPacket::new);

    private final UUID playerId;

    public KickPlayerPacket(PacketByteBuf buf) {
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
