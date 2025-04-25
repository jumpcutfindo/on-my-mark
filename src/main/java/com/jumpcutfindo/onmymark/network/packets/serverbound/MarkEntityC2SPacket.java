package com.jumpcutfindo.onmymark.network.packets.serverbound;

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

public class MarkEntityC2SPacket implements CustomPayload {
    public static final Id<MarkEntityC2SPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "mark_entity"));
    public static final PacketCodec<RegistryByteBuf, MarkEntityC2SPacket> PACKET_CODEC = PacketCodec.of(MarkEntityC2SPacket::write, MarkEntityC2SPacket::new);

    private UUID playerId;
    private UUID entityId;

    public MarkEntityC2SPacket(PacketByteBuf buf) {
        this.playerId = buf.readUuid();
        this.entityId = buf.readUuid();
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.playerId);
        buf.writeUuid(this.entityId);
    }

    public static MarkEntityC2SPacket create(PlayerEntity player, Entity entity) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(player.getUuid());
        buf.writeUuid(entity.getUuid());

        return new MarkEntityC2SPacket(buf);
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
