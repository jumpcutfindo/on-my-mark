package com.jumpcutfindo.onmymark.network.packets.clientbound;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.server.party.ServerPartyMember;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class MarkBlockResultS2CPacket implements CustomPayload {
    public static final CustomPayload.Id<MarkBlockResultS2CPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "mark_block_result"));
    public static final PacketCodec<RegistryByteBuf, MarkBlockResultS2CPacket> PACKET_CODEC = PacketCodec.of(MarkBlockResultS2CPacket::write, MarkBlockResultS2CPacket::new);

    private UUID playerId;
    private BlockPos blockPos;
    private Identifier blockIdentifier;

    public MarkBlockResultS2CPacket(PacketByteBuf buf) {
        this.playerId = buf.readUuid();
        this.blockPos = buf.readBlockPos();
        this.blockIdentifier = buf.readIdentifier();
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.playerId);
        buf.writeBlockPos(this.blockPos);
        buf.writeIdentifier(this.blockIdentifier);
    }

    public static MarkBlockResultS2CPacket create(ServerPartyMember serverPartyMember, BlockPos blockPos, BlockState blockState) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(serverPartyMember.id());
        buf.writeBlockPos(blockPos);
        buf.writeIdentifier(Registries.BLOCK.getId(blockState.getBlock()));

        return new MarkBlockResultS2CPacket(buf);
    }

    public UUID playerId() {
        return playerId;
    }

    public BlockPos blockPos() {
        return blockPos;
    }

    public Identifier blockIdentifier() {
        return blockIdentifier;
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
