package com.jumpcutfindo.onmymark.server.party.exceptions;

public class PartyInviteNotFoundException extends PartyException {
    public PartyInviteNotFoundException(String playerName) {
        super(String.format("Party invite for player \"%s\" does not exist", playerName));
    }
}
