package com.jumpcutfindo.onmymark.server.party.exceptions;

public class RemovePartyLeaderException extends PartyException {
    public RemovePartyLeaderException(String playerName) {
        super(String.format("Cannot remove player \"%s\" as they are the party leader", playerName));
    }
}
