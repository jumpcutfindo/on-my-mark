package com.jumpcutfindo.onmymark.network.packets.serverbound;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class CreatePartyC2SPacket implements CustomPayload {
    public static final Id<CreatePartyC2SPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "create_party"));
    public static final PacketCodec<RegistryByteBuf, CreatePartyC2SPacket> PACKET_CODEC = PacketCodec.of(CreatePartyC2SPacket::write, CreatePartyC2SPacket::new);

    private final String partyName;

    public CreatePartyC2SPacket(PacketByteBuf buf) {
        this.partyName = buf.readString();
    }

    public void write(PacketByteBuf buf) {
        buf.writeString(partyName);
    }

    public String partyName() {
        return partyName;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
