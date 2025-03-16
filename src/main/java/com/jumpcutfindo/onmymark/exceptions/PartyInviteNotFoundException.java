package com.jumpcutfindo.onmymark.exceptions;

public class PartyInviteNotFoundException extends OnMyMarkException {
    public PartyInviteNotFoundException(String playerName) {
        super(String.format("Party invite for player \"%s\" does not exist", playerName));
    }
}
