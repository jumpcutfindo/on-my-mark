package com.jumpcutfindo.onmymark.party.exceptions;

public class AlreadyInPartyException extends OnMyMarkException {
    public AlreadyInPartyException(String playerName) {
        super(String.format("%s is already in the specified party"));
    }
}
