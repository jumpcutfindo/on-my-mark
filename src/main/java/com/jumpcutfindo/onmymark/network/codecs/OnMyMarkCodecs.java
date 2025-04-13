package com.jumpcutfindo.onmymark.network.codecs;

import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class OnMyMarkCodecs {
    public static final PacketCodec<PacketByteBuf, PartyMember> PARTY_MEMBER = new PartyMemberCodec();
}
