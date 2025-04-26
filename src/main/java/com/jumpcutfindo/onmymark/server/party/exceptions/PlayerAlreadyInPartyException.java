package com.jumpcutfindo.onmymark.server.party.exceptions;

public class PlayerAlreadyInPartyException extends PartyException {
    public PlayerAlreadyInPartyException(String playerName) {
        super(String.format("Player \"%s\" is already in a party", playerName));
    }
}
