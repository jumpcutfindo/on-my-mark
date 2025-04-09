package com.jumpcutfindo.onmymark.network.packets;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class MarkBlockPacket implements CustomPayload {
    public static final Id<MarkBlockPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "mark_block"));
    public static final PacketCodec<RegistryByteBuf, MarkBlockPacket> PACKET_CODEC = PacketCodec.of(MarkBlockPacket::write, MarkBlockPacket::new);

    private UUID playerId;
    private BlockPos blockPos;

    public MarkBlockPacket(PacketByteBuf buf) {
        this.playerId = buf.readUuid();
        this.blockPos = buf.readBlockPos();
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.playerId);
        buf.writeBlockPos(this.blockPos);
    }

    public static MarkBlockPacket create(PlayerEntity player, BlockPos blockPos) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(player.getUuid());
        buf.writeBlockPos(blockPos);

        return new MarkBlockPacket(buf);
    }

    public UUID playerId() {
        return playerId;
    }

    public BlockPos blockPos() {
        return blockPos;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
