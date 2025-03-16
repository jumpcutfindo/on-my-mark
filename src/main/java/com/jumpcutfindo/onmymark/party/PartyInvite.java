package com.jumpcutfindo.onmymark.party;

import java.util.UUID;

public class PartyInvite {
    private UUID id;
    private Party party;
    private PartyMember from, to;

    public PartyInvite(Party party, PartyMember from, PartyMember to) {
        this.party = party;
        this.from = from;
        this.to = to;
    }

    public Party party() {
        return party;
    }

    public PartyMember to() {
        return to;
    }

    public PartyMember from() {
        return from;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof PartyInvite pi) && this.id == pi.id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
