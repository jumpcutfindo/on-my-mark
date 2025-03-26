package com.jumpcutfindo.onmymark.party.exceptions;

public class ExistingInviteException extends OnMyMarkException {
    public ExistingInviteException(String playerName) {
        super(String.format("Player \"%s\"already has a pending invite", playerName));
    }
}
