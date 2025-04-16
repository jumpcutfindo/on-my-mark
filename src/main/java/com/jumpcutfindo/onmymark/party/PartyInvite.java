package com.jumpcutfindo.onmymark.party;

import java.util.Objects;

public class PartyInvite<T extends PartyMember> {
    private Party<T> party;
    private T from, to;

    public PartyInvite(Party<T> party, T from, T to) {
        this.party = party;
        this.from = from;
        this.to = to;
    }

    public Party<T> party() {
        return party;
    }

    public T to() {
        return to;
    }

    public T from() {
        return from;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PartyInvite<?> that)) return false;
        return Objects.equals(party, that.party) && Objects.equals(from, that.from) && Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(party, from, to);
    }
}
