package com.jumpcutfindo.onmymark.exceptions;

import java.util.UUID;

public class PartyInviteNotFoundException extends OnMyMarkException {
    public PartyInviteNotFoundException(UUID id, String playerName) {
        super(String.format("Party invite with id \"%s\" for player \"%s\" does not exist", id, playerName));
    }
}
