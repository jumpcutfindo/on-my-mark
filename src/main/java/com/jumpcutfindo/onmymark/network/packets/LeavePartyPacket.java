package com.jumpcutfindo.onmymark.network.packets;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class LeavePartyPacket implements CustomPayload {
    public static final CustomPayload.Id<LeavePartyPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "leave_party"));
    public static final PacketCodec<RegistryByteBuf, LeavePartyPacket> PACKET_CODEC = PacketCodec.of(LeavePartyPacket::write, LeavePartyPacket::new);

    public LeavePartyPacket(PacketByteBuf buf) {
    }

    private void write(PacketByteBuf buf) {
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
