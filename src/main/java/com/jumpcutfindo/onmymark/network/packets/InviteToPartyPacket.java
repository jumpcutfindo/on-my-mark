package com.jumpcutfindo.onmymark.network.packets;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class InviteToPartyPacket implements CustomPayload {
    public static final Id<InviteToPartyPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "invite_to_party"));
    public static final PacketCodec<RegistryByteBuf, InviteToPartyPacket> PACKET_CODEC = PacketCodec.of(InviteToPartyPacket::write, InviteToPartyPacket::new);

    private final String playerName;

    public InviteToPartyPacket(PacketByteBuf buf) {
        this.playerName = buf.readString();
    }

    private void write(PacketByteBuf buf) {
        buf.writeString(this.playerName);
    }

    public String playerName() {
        return playerName;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
