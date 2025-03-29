package com.jumpcutfindo.onmymark.network.packets;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class KickFromPartyPacket implements CustomPayload {
    public static final Id<KickFromPartyPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "kick_from_party"));
    public static final PacketCodec<RegistryByteBuf, KickFromPartyPacket> PACKET_CODEC = PacketCodec.of(KickFromPartyPacket::write, KickFromPartyPacket::new);

    private final UUID playerId;

    public KickFromPartyPacket(PacketByteBuf buf) {
        this.playerId = buf.readUuid();
    }

    private void write(PacketByteBuf buf) {
        buf.writeUuid(this.playerId);
    }

    public UUID playerId() {
        return playerId;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
