package com.jumpcutfindo.onmymark.network.packets;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class RemovePartyInfoPacket implements CustomPayload {
    public static final Id<RemovePartyInfoPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "remove_party_info"));
    public static final PacketCodec<RegistryByteBuf, RemovePartyInfoPacket> PACKET_CODEC = PacketCodec.of(RemovePartyInfoPacket::write, RemovePartyInfoPacket::new);

    public RemovePartyInfoPacket(PacketByteBuf buf) {}

    private void write(PacketByteBuf buf) {}

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
