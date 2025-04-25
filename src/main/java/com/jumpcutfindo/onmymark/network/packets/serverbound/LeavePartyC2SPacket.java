package com.jumpcutfindo.onmymark.network.packets.serverbound;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class LeavePartyC2SPacket implements CustomPayload {
    public static final CustomPayload.Id<LeavePartyC2SPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "leave_party"));
    public static final PacketCodec<RegistryByteBuf, LeavePartyC2SPacket> PACKET_CODEC = PacketCodec.of(LeavePartyC2SPacket::write, LeavePartyC2SPacket::new);

    public LeavePartyC2SPacket(PacketByteBuf buf) {
    }

    private void write(PacketByteBuf buf) {
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
