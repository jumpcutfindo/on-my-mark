package com.jumpcutfindo.onmymark.network.packets;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.network.codecs.OnMyMarkCodecs;
import com.jumpcutfindo.onmymark.party.ClientPartyMember;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.UUID;

public class PartyInfoPacket implements CustomPayload {
    public static final Id<PartyInfoPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "create_party"));
    public static final PacketCodec<RegistryByteBuf, PartyInfoPacket> PACKET_CODEC = PacketCodec.of(PartyInfoPacket::write, PartyInfoPacket::new);

    private final UUID partyId;
    private final String partyName;
    private final List<PartyMember> partyMembers;

    public PartyInfoPacket(PacketByteBuf buf) {
        this.partyId = buf.readUuid();
        this.partyName = buf.readString();
        this.partyMembers = buf.readList(OnMyMarkCodecs.PARTY_MEMBER);
    }

    private PartyInfoPacket(UUID partyId, String partyName, List<PartyMember> partyMembers) {
        this.partyId = partyId;
        this.partyName = partyName;
        this.partyMembers = partyMembers;
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(partyId);
        buf.writeString(partyName);
        buf.writeCollection(partyMembers, OnMyMarkCodecs.PARTY_MEMBER);
    }

    @SuppressWarnings("unchecked")
    public static <T extends PartyMember> PartyInfoPacket fromParty(Party<T> party) {
        UUID partyId = party.partyId();
        String partyName = party.partyName();
        List<T> partyMembers = party.partyMembers();

        return new PartyInfoPacket(partyId, partyName, (List<PartyMember>) partyMembers);
    }

    public Party<ClientPartyMember> toParty() {
        ClientPartyMember partyLeader = (ClientPartyMember) this.partyMembers.stream()
                .filter(PartyMember::isPartyLeader)
                .findAny()
                .get();

        Party<ClientPartyMember> party = Party.withPartyId(partyId, partyName, partyLeader);

        for (PartyMember member : partyMembers) {
            ((ClientPartyMember) member).setCurrentParty(party);
            party.addPartyMember((ClientPartyMember) member);
        }

        return party;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
