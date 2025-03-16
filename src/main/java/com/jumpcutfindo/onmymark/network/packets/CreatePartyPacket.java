package com.jumpcutfindo.onmymark.network.packets;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class CreatePartyPacket implements CustomPayload {
    public static final Id<CreatePartyPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "create_party"));
    public static final PacketCodec<RegistryByteBuf, CreatePartyPacket> PACKET_CODEC = PacketCodec.of(CreatePartyPacket::write, CreatePartyPacket::new);

    private final String partyName;

    public CreatePartyPacket(PacketByteBuf buf) {
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
