package com.jumpcutfindo.onmymark.network.packets.serverbound;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class PlayerReportC2SPacket implements CustomPayload {
    public static final CustomPayload.Id<PlayerReportC2SPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "player_report"));
    public static final PacketCodec<RegistryByteBuf, PlayerReportC2SPacket> PACKET_CODEC = PacketCodec.of(PlayerReportC2SPacket::write, PlayerReportC2SPacket::new);

    public PlayerReportC2SPacket(PacketByteBuf buf) {
    }

    private void write(PacketByteBuf buf) {
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
