package com.jumpcutfindo.onmymark.party.exceptions;

public class RemovePartyLeaderException extends OnMyMarkException {
    public RemovePartyLeaderException(String playerName) {
        super(String.format("Cannot remove player \"%s\" as they are the party leader", playerName));
    }
}
