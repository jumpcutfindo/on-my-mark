package com.jumpcutfindo.onmymark.server.party.exceptions;

public class PartyUnavailableException extends PartyException {
    public PartyUnavailableException() {
        super("The requested party is unavailable");
    }
}
