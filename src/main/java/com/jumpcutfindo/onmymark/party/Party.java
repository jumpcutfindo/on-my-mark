package com.jumpcutfindo.onmymark.party;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class Party<T extends PartyMember> {
    private UUID partyId;
    private String partyName;
    private T partyLeader;
    private List<T> partyMembers;

    private Party.State state;

    public Party(String partyName, T partyLeader) {
        this.partyId = UUID.randomUUID();

        this.partyName = partyName;
        this.partyLeader = partyLeader;

        this.partyMembers = new ArrayList<>();

        this.state = State.ACTIVE;
    }

    public static <T extends PartyMember> Party<T> withPartyId(UUID partyId, String partyName, T partyLeader) {
        Party<T> party = new Party<>(partyName, partyLeader);
        party.partyId = partyId;

        return party;
    }

    public UUID partyId() {
        return partyId;
    }

    public String partyName() {
        return partyName;
    }

    public T partyLeader() {
        return partyLeader;
    }

    public List<T> partyMembers() {
        return partyMembers;
    }

    public List<T> partyMembers(Predicate<T> filter) {
        return partyMembers.stream().filter(filter).toList();
    }

    public State state() {
        return state;
    }

    public void addPartyMember(T partyMember) {
        this.partyMembers.add(partyMember);
    }

    public void removePartyMember(T partyMember) {
        this.partyMembers.remove(partyMember);
    }

    public boolean isPartyLeader(T partyMember) {
        return this.partyLeader.equals(partyMember);
    }

    public boolean hasMember(T partyMember) {
        return this.partyMembers.contains(partyMember);
    }

    public boolean hasMemberWithId(UUID playerId) {
        return this.partyMembers.stream().anyMatch((pm) -> pm.id().equals(playerId));
    }

    public T getMemberWithId(UUID playerId) {
        Optional<T> pmOpt = this.partyMembers.stream().filter((pm) -> pm.id().equals(playerId)).findFirst();

        return pmOpt.orElse(null);
    }

    public void setState(State state) {
        this.state = state;
    }

    public enum State {
        ACTIVE, DISBANDED
    }
}
