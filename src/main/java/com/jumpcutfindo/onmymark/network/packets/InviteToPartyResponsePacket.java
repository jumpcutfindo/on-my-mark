package com.jumpcutfindo.onmymark.network.packets;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class InviteToPartyResponsePacket implements CustomPayload {
    public static final Id<InviteToPartyResponsePacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "invite_to_party_response"));
    public static final PacketCodec<RegistryByteBuf, InviteToPartyResponsePacket> PACKET_CODEC = PacketCodec.of(InviteToPartyResponsePacket::write, InviteToPartyResponsePacket::new);

    private boolean isSuccessful;

    public InviteToPartyResponsePacket(PacketByteBuf buf) {
        this.isSuccessful = buf.readBoolean();
    }

    private void write(PacketByteBuf buf) {
        buf.writeBoolean(isSuccessful);
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public static InviteToPartyResponsePacket successful() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(true);

        return new InviteToPartyResponsePacket(buf);
    }

    public static InviteToPartyResponsePacket unsuccessful() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(false);

        return new InviteToPartyResponsePacket(buf);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
