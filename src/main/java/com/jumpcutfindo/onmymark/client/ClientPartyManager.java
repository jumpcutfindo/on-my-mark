package com.jumpcutfindo.onmymark.client;

import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyInvite;
import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.client.MinecraftClient;

public class ClientPartyManager {
    private Party party;
    private PartyMember partyMember;
    private PartyInvite partyInvite;

    public ClientPartyManager() {

    }

    public void setParty(Party party) {
        this.party = party;

        if (party == null) {
            this.partyMember = null;
            return;
        }

        this.partyMember = party.partyMembers()
                .stream()
                .filter((mem) -> mem.player().getUuid() == MinecraftClient.getInstance().player.getUuid())
                .toList()
                .getFirst();
    }

    public Party party() {
        return party;
    }

    public PartyMember partyMember() {
        return partyMember;
    }

    public void setPartyInvite(PartyInvite invite) {
        this.partyInvite = invite;
    }

    public PartyInvite partyInvite() {
        return partyInvite;
    }
}
