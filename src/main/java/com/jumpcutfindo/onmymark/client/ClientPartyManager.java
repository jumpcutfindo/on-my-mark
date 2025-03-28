package com.jumpcutfindo.onmymark.client;

import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyInvite;

public class ClientPartyManager {
    private Party party;
    private PartyInvite partyInvite;

    public ClientPartyManager() {

    }

    public void setParty(Party party) {
        this.party = party;
    }

    public Party party() {
        return party;
    }

    public void setPartyInvite(PartyInvite invite) {
        this.partyInvite = invite;
    }

    public PartyInvite partyInvite() {
        return partyInvite;
    }
}
