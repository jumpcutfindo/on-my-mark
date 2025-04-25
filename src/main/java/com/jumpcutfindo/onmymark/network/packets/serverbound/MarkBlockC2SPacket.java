package com.jumpcutfindo.onmymark.network.packets.serverbound;

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

public class MarkBlockC2SPacket implements CustomPayload {
    public static final Id<MarkBlockC2SPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "mark_block"));
    public static final PacketCodec<RegistryByteBuf, MarkBlockC2SPacket> PACKET_CODEC = PacketCodec.of(MarkBlockC2SPacket::write, MarkBlockC2SPacket::new);

    private UUID playerId;
    private BlockPos blockPos;

    public MarkBlockC2SPacket(PacketByteBuf buf) {
        this.playerId = buf.readUuid();
        this.blockPos = buf.readBlockPos();
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.playerId);
        buf.writeBlockPos(this.blockPos);
    }

    public static MarkBlockC2SPacket create(PlayerEntity player, BlockPos blockPos) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(player.getUuid());
        buf.writeBlockPos(blockPos);

        return new MarkBlockC2SPacket(buf);
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
