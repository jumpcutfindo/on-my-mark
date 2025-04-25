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
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class MarkBlockResultS2CPacket implements CustomPayload {
    public static final CustomPayload.Id<MarkBlockResultS2CPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "mark_block_result"));
    public static final PacketCodec<RegistryByteBuf, MarkBlockResultS2CPacket> PACKET_CODEC = PacketCodec.of(MarkBlockResultS2CPacket::write, MarkBlockResultS2CPacket::new);

    private UUID playerId;
    private RegistryKey<World> worldRegistryKey;
    private BlockPos blockPos;
    private Identifier blockIdentifier;

    public MarkBlockResultS2CPacket(PacketByteBuf buf) {
        this.playerId = buf.readUuid();
        this.worldRegistryKey = buf.readRegistryKey(RegistryKeys.WORLD);
        this.blockPos = buf.readBlockPos();
        this.blockIdentifier = buf.readIdentifier();
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.playerId);
        buf.writeRegistryKey(this.worldRegistryKey);
        buf.writeBlockPos(this.blockPos);
        buf.writeIdentifier(this.blockIdentifier);
    }

    public static MarkBlockResultS2CPacket create(ServerPartyMember serverPartyMember, BlockPos blockPos, BlockState blockState) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(serverPartyMember.id());
        buf.writeRegistryKey(serverPartyMember.player().getWorld().getRegistryKey());
        buf.writeBlockPos(blockPos);
        buf.writeIdentifier(Registries.BLOCK.getId(blockState.getBlock()));

        return new MarkBlockResultS2CPacket(buf);
    }

    public UUID playerId() {
        return playerId;
    }

    public RegistryKey<World> worldRegistryKey() {
        return worldRegistryKey;
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
