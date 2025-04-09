package com.jumpcutfindo.onmymark.network.packets;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class MarkEntityPacket implements CustomPayload {
    public static final Id<MarkEntityPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "mark_entity"));
    public static final PacketCodec<RegistryByteBuf, MarkEntityPacket> PACKET_CODEC = PacketCodec.of(MarkEntityPacket::write, MarkEntityPacket::new);

    private UUID playerId;
    private UUID entityId;

    public MarkEntityPacket(PacketByteBuf buf) {
        this.playerId = buf.readUuid();
        this.entityId = buf.readUuid();
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.playerId);
        buf.writeUuid(this.entityId);
    }

    public static MarkEntityPacket create(PlayerEntity player, Entity entity) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(player.getUuid());
        buf.writeUuid(entity.getUuid());

        return new MarkEntityPacket(buf);
    }

    public UUID playerId() {
        return playerId;
    }

    public UUID entityId() {
        return entityId;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
