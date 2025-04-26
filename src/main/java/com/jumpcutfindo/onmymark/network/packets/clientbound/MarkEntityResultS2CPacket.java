package com.jumpcutfindo.onmymark.network.packets.clientbound;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.marker.EntityMarker;
import com.jumpcutfindo.onmymark.server.party.ServerPartyMember;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.UUID;

public class MarkEntityResultS2CPacket implements CustomPayload {
    public static final Id<MarkEntityResultS2CPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "mark_entity_result"));
    public static final PacketCodec<RegistryByteBuf, MarkEntityResultS2CPacket> PACKET_CODEC = PacketCodec.of(MarkEntityResultS2CPacket::write, MarkEntityResultS2CPacket::new);

    private UUID playerId;
    private RegistryKey<World> worldRegistryKey;
    private UUID entityId;
    private String entityName;

    public MarkEntityResultS2CPacket(PacketByteBuf buf) {
        this.playerId = buf.readUuid();
        this.worldRegistryKey = buf.readRegistryKey(RegistryKeys.WORLD);
        this.entityId = buf.readUuid();
        this.entityName = buf.readString();
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.playerId);
        buf.writeRegistryKey(this.worldRegistryKey);
        buf.writeUuid(this.entityId);
        buf.writeString(this.entityName);
    }

    public static MarkEntityResultS2CPacket create(ServerPartyMember markerPartyMember, EntityMarker entityMarker) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(markerPartyMember.player().getUuid());
        buf.writeRegistryKey(entityMarker.worldRegistryKey());
        buf.writeUuid(entityMarker.entityId());
        buf.writeString(entityMarker.entityName());

        return new MarkEntityResultS2CPacket(buf);
    }

    public UUID playerId() {
        return playerId;
    }

    public RegistryKey<World> worldRegistryKey() {
        return worldRegistryKey;
    }

    public UUID entityId() {
        return entityId;
    }

    public String entityName() {
        return entityName;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}