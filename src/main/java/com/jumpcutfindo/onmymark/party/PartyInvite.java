package com.jumpcutfindo.onmymark.party;

import java.util.Objects;

public class PartyInvite {
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
    public boolean equals(Object o) {
        if (!(o instanceof PartyInvite that)) return false;
        return Objects.equals(party, that.party) && Objects.equals(from, that.from) && Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(party, from, to);
    }
}
