package com.jumpcutfindo.onmymark.server.party.exceptions;

public class InvalidPartyPermissionsException extends PartyException {
    public InvalidPartyPermissionsException(String playerName) {
        super(String.format("Player \"%s\" does not have enough permissions to perform this action", playerName));
    }
}
