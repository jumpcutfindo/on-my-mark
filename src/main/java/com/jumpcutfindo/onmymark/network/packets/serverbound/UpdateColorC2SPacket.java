package com.jumpcutfindo.onmymark.network.packets.serverbound;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class UpdateColorC2SPacket implements CustomPayload {
    public static final CustomPayload.Id<UpdateColorC2SPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "update_color"));
    public static final PacketCodec<RegistryByteBuf, UpdateColorC2SPacket> PACKET_CODEC = PacketCodec.of(UpdateColorC2SPacket::write, UpdateColorC2SPacket::new);

    private final int color;

    public UpdateColorC2SPacket(PacketByteBuf buf) {
        this.color = buf.readInt();
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(color);
    }

    public int color() {
        return color;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
