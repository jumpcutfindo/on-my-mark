package com.jumpcutfindo.onmymark.network.packets;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class RemoveMarkerPacket implements CustomPayload {
    public static final CustomPayload.Id<RemoveMarkerPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "remove_marker"));
    public static final PacketCodec<RegistryByteBuf, RemoveMarkerPacket> PACKET_CODEC = PacketCodec.of(RemoveMarkerPacket::write, RemoveMarkerPacket::new);

    private final UUID markerPlayerId;

    public RemoveMarkerPacket(PacketByteBuf buf) {
        this.markerPlayerId = buf.readUuid();
    }

    private void write(PacketByteBuf buf) {
        buf.writeUuid(this.markerPlayerId);
    }

    public UUID markerPlayerId() {
        return markerPlayerId;
    }

    public static RemoveMarkerPacket create(PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(player.getUuid());

        return new RemoveMarkerPacket(buf);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}