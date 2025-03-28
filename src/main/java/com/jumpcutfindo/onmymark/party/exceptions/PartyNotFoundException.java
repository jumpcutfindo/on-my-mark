package com.jumpcutfindo.onmymark.party.exceptions;

import java.util.UUID;

public class PartyNotFoundException extends OnMyMarkException {
    public PartyNotFoundException(UUID partyId) {
        super(String.format("Party with id \"%s\" not found", partyId.toString()));
    }

    public PartyNotFoundException() {
        super("Party not found");
    }
}
