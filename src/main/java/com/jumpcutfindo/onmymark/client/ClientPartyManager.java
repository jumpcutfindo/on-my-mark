package com.jumpcutfindo.onmymark.client;

import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyInvite;
import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.client.MinecraftClient;

public class ClientPartyManager {
    private Party party;
    private PartyMember self;
    private PartyInvite partyInvite;

    public ClientPartyManager() {

    }

    public void setParty(Party party) {
        this.party = party;

        if (party == null) {
            this.self = null;
            return;
        }

        this.self = party.partyMembers()
                .stream()
                .filter((mem) -> mem.player().getUuid() == MinecraftClient.getInstance().player.getUuid())
                .toList()
                .getFirst();
    }

    public Party party() {
        return party;
    }

    public PartyMember self() {
        return self;
    }

    public boolean isPartyLeader() {
        return self != null && self.isPartyLeader();
    }

    public boolean isInParty() {
        return self != null;
    }

    public void setPartyInvite(PartyInvite invite) {
        this.partyInvite = invite;
    }

    public PartyInvite partyInvite() {
        return partyInvite;
    }
}
