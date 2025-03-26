package com.jumpcutfindo.onmymark.network.packets;

import com.jumpcutfindo.onmymark.OnMyMarkMod;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class PartyInfoPacket implements CustomPayload {
    public static final Id<PartyInfoPacket> PACKET_ID = new Id<>(Identifier.of(OnMyMarkMod.MOD_ID, "create_party"));
    public static final PacketCodec<RegistryByteBuf, PartyInfoPacket> PACKET_CODEC = PacketCodec.of(PartyInfoPacket::write, PartyInfoPacket::new);

    private final UUID partyId;
    private final String partyName;
    private final UUID partyLeader;
    private final List<UUID> partyMembers;

    public PartyInfoPacket(PacketByteBuf buf) {
        this.partyId = buf.readUuid();
        this.partyName = buf.readString();
        this.partyLeader = buf.readUuid();
        this.partyMembers = buf.readList(Uuids.PACKET_CODEC);
    }

    private PartyInfoPacket(UUID partyId, String partyName, UUID partyLeader, List<UUID> partyMembers) {
        this.partyId = partyId;
        this.partyName = partyName;
        this.partyLeader = partyLeader;
        this.partyMembers = partyMembers;
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(partyId);
        buf.writeString(partyName);
        buf.writeUuid(partyLeader);
        buf.writeCollection(partyMembers, Uuids.PACKET_CODEC);
    }

    public static PartyInfoPacket fromParty(Party party) {
        UUID partyId = party.partyId();
        String partyName = party.partyName();
        UUID partyLeader = party.partyLeader().player().getUuid();
        List<UUID> partyMembers = party.partyMembers().stream().map((member) -> member.player().getUuid()).toList();

        return new PartyInfoPacket(partyId, partyName, partyLeader, partyMembers);
    }

    public Party toParty(World world) {
        PartyMember partyLeader = new PartyMember(world.getPlayerByUuid(this.partyLeader));
        Party party = Party.withPartyId(partyId, partyName, partyLeader);

        for (UUID memberId : partyMembers) {
            PartyMember member = new PartyMember(world.getPlayerByUuid(memberId));
            member.setCurrentParty(party);
            party.addPartyMember(member);
        }

        return party;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
