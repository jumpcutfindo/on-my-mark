package com.jumpcutfindo.onmymark.network.packets.clientbound;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class RemovePartyInfoS2CPacket implements CustomPayload {
    public static final Id<RemovePartyInfoS2CPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "remove_party_info"));
    public static final PacketCodec<RegistryByteBuf, RemovePartyInfoS2CPacket> PACKET_CODEC = PacketCodec.of(RemovePartyInfoS2CPacket::write, RemovePartyInfoS2CPacket::new);

    public RemovePartyInfoS2CPacket(PacketByteBuf buf) {}

    private void write(PacketByteBuf buf) {}

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
