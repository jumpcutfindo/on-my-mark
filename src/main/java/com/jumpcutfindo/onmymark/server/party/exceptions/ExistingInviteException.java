package com.jumpcutfindo.onmymark.server.party.exceptions;

public class ExistingInviteException extends PartyException {
    public ExistingInviteException(String playerName) {
        super(String.format("Player \"%s\"already has a pending invite", playerName));
    }
}
