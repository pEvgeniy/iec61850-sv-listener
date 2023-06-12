package ru.mpei.iec61850svlistener.api.model;

import lombok.Getter;

public class ErrorResponse {
    @Getter
    private final String error;

    public ErrorResponse(String error) {
        this.error = error;
    }
}

