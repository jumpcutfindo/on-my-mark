package com.jumpcutfindo.onmymark.client;

import com.jumpcutfindo.onmymark.party.ClientPartyMember;
import com.jumpcutfindo.onmymark.party.Party;
import com.jumpcutfindo.onmymark.party.PartyInvite;
import com.jumpcutfindo.onmymark.party.PartyMember;
import net.minecraft.client.MinecraftClient;

public class ClientPartyManager {
    private Party<ClientPartyMember> party;
    private ClientPartyMember self;
    private PartyInvite<ClientPartyMember> partyInvite;

    public ClientPartyManager() {

    }

    public void reset() {
        this.party = null;
        this.self = null;
        this.partyInvite = null;
    }

    public void setParty(Party<ClientPartyMember> party) {
        this.party = party;

        if (party == null) {
            this.self = null;
            return;
        }

        this.self = party.partyMembers()
                .stream()
                .filter((mem) -> mem.id().equals(MinecraftClient.getInstance().player.getUuid()))
                .toList()
                .getFirst();
    }

    public Party<ClientPartyMember> party() {
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

    public void setPartyInvite(PartyInvite<ClientPartyMember> invite) {
        this.partyInvite = invite;
    }

    public PartyInvite<ClientPartyMember> partyInvite() {
        return partyInvite;
    }
}
