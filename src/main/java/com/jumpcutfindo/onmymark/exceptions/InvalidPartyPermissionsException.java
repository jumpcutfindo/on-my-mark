package com.jumpcutfindo.onmymark.exceptions;

public class InvalidPartyPermissionsException extends OnMyMarkException {
    public InvalidPartyPermissionsException(String playerName) {
        super(String.format("Player \"%s\" does not have enough permissions to perform this action", playerName));
    }
}
