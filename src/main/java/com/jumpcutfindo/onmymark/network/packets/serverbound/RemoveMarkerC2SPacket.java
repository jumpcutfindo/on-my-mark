package com.jumpcutfindo.onmymark.network.packets.serverbound;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class RemoveMarkerC2SPacket implements CustomPayload {
    public static final CustomPayload.Id<RemoveMarkerC2SPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "remove_marker"));
    public static final PacketCodec<RegistryByteBuf, RemoveMarkerC2SPacket> PACKET_CODEC = PacketCodec.of(RemoveMarkerC2SPacket::write, RemoveMarkerC2SPacket::new);

    private final UUID markerPlayerId;

    public RemoveMarkerC2SPacket(PacketByteBuf buf) {
        this.markerPlayerId = buf.readUuid();
    }

    private void write(PacketByteBuf buf) {
        buf.writeUuid(this.markerPlayerId);
    }

    public UUID markerPlayerId() {
        return markerPlayerId;
    }

    public static RemoveMarkerC2SPacket create(PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(player.getUuid());

        return new RemoveMarkerC2SPacket(buf);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}