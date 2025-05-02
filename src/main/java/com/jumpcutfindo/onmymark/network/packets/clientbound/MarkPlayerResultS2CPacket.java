package com.jumpcutfindo.onmymark.network.packets.clientbound;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.marker.PlayerMarker;
import com.jumpcutfindo.onmymark.server.party.ServerPartyMember;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public class MarkPlayerResultS2CPacket implements CustomPayload {
    public static final Id<MarkPlayerResultS2CPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "mark_player_result"));
    public static final PacketCodec<RegistryByteBuf, MarkPlayerResultS2CPacket> PACKET_CODEC = PacketCodec.of(MarkPlayerResultS2CPacket::write, MarkPlayerResultS2CPacket::new);

    private UUID playerId;
    private RegistryKey<World> worldRegistryKey;
    private UUID markerPlayerId;
    private String markerPlayerName;
    private Vec3d lastPos;

    public MarkPlayerResultS2CPacket(PacketByteBuf buf) {
        this.playerId = buf.readUuid();
        this.worldRegistryKey = buf.readRegistryKey(RegistryKeys.WORLD);
        this.markerPlayerId = buf.readUuid();
        this.markerPlayerName = buf.readString();
        this.lastPos = buf.readVec3d();
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.playerId);
        buf.writeRegistryKey(this.worldRegistryKey);
        buf.writeUuid(this.markerPlayerId);
        buf.writeString(this.markerPlayerName);
        buf.writeVec3d(this.lastPos);
    }

    public static MarkPlayerResultS2CPacket create(ServerPartyMember markerPartyMember, PlayerMarker playerMarker) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(markerPartyMember.player().getUuid());
        buf.writeRegistryKey(playerMarker.worldRegistryKey());
        buf.writeUuid(playerMarker.playerId());
        buf.writeString(playerMarker.playerName());
        buf.writeVec3d(playerMarker.lastPos());

        return new MarkPlayerResultS2CPacket(buf);
    }

    public UUID playerId() {
        return playerId;
    }

    public RegistryKey<World> worldRegistryKey() {
        return worldRegistryKey;
    }

    public UUID markerPlayerId() {
        return markerPlayerId;
    }

    public String markerPlayerName() {
        return markerPlayerName;
    }

    public Vec3d lastPos() {
        return lastPos;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
