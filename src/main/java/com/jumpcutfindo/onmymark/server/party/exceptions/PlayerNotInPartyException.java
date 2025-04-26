package com.jumpcutfindo.onmymark.server.party.exceptions;

public class PlayerNotInPartyException extends PartyException {
    public PlayerNotInPartyException(String playerName) {
        super(String.format("%s is not in the specified party", playerName));
    }
}
