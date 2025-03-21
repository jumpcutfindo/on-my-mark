package com.jumpcutfindo.onmymark.party.exceptions;

public class PartyInviteNotFoundException extends OnMyMarkException {
    public PartyInviteNotFoundException(String playerName) {
        super(String.format("Party invite for player \"%s\" does not exist", playerName));
    }
}
