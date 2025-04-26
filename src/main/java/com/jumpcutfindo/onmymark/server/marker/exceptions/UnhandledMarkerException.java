package com.jumpcutfindo.onmymark.server.marker.exceptions;

public class UnhandledMarkerException extends MarkerException {
    public UnhandledMarkerException() {
        super("Marker type was not handled");
    }
}
