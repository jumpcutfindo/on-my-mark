package com.jumpcutfindo.onmymark.server.party.exceptions;

public class AlreadyInPartyException extends PartyException {
    public AlreadyInPartyException(String playerName) {
        super(String.format("%s is already in a party", playerName));
    }
}
