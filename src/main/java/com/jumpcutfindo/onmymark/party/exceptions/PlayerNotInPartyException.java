package com.jumpcutfindo.onmymark.party.exceptions;

public class PlayerNotInPartyException extends OnMyMarkException {
    public PlayerNotInPartyException(String playerName) {
        super(String.format("%s is not in the specified party", playerName));
    }
}
