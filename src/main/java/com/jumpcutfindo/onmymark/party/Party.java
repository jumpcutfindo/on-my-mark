package com.jumpcutfindo.onmymark.party;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Party {
    private final UUID partyId;
    private String partyName;
    private PartyMember partyLeader;
    private List<PartyMember> partyMembers;

    public Party(String partyName, PartyMember partyLeader) {
        this.partyId = UUID.randomUUID();

        this.partyName = partyName;
        this.partyLeader = partyLeader;

        this.partyMembers = new ArrayList<>();
    }

    public UUID partyId() {
        return partyId;
    }

    public String partyName() {
        return partyName;
    }

    public PartyMember partyLeader() {
        return partyLeader;
    }

    public List<PartyMember> partyMembers() {
        return partyMembers;
    }

    public void addPartyMember(PartyMember partyMember) {
        this.partyMembers.add(partyMember);
    }

    public void removePartyMember(PartyMember partyMember) {
        this.partyMembers.remove(partyMember);

        // Hand off party leadership to someone else
        if (this.isPartyLeader(partyMember) && !this.partyMembers.isEmpty()) {
            this.partyLeader = partyMembers.getFirst();

            // TODO: Add handling of member states, e.g. player offline cannot get party leadership
        }
    }

    public boolean isPartyLeader(PartyMember partyMember) {
        return this.partyLeader.equals(partyMember);
    }
}
