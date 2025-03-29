package com.jumpcutfindo.onmymark.party;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Party {
    private UUID partyId;
    private String partyName;
    private PartyMember partyLeader;
    private List<PartyMember> partyMembers;

    private Party.State state;

    public Party(String partyName, PartyMember partyLeader) {
        this.partyId = UUID.randomUUID();

        this.partyName = partyName;
        this.partyLeader = partyLeader;

        this.partyMembers = new ArrayList<>();

        this.state = State.ACTIVE;
    }

    public static Party withPartyId(UUID partyId, String partyName, PartyMember partyLeader) {
        Party party = new Party(partyName, partyLeader);
        party.partyId = partyId;

        return party;
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

    public State state() {
        return state;
    }

    public void addPartyMember(PartyMember partyMember) {
        this.partyMembers.add(partyMember);
    }

    public void removePartyMember(PartyMember partyMember) {
        this.partyMembers.remove(partyMember);
    }

    public boolean isPartyLeader(PartyMember partyMember) {
        return this.partyLeader.equals(partyMember);
    }

    public void setState(State state) {
        this.state = state;
    }

    public enum State {
        ACTIVE, DISBANDED
    }
}
