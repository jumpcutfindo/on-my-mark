package com.jumpcutfindo.onmymark.exceptions;

public class PlayerAlreadyInPartyException extends OnMyMarkException {
    public PlayerAlreadyInPartyException(String playerName) {
        super(String.format("Player \"%s\" is already in a party", playerName));
    }
}
