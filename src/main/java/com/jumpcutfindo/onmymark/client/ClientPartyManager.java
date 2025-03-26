package com.jumpcutfindo.onmymark.client;

import com.jumpcutfindo.onmymark.party.Party;

public class ClientPartyManager {
    private Party party;

    public ClientPartyManager() {

    }

    public void setParty(Party party) {
        this.party = party;
    }

    public Party party() {
        return party;
    }
}
