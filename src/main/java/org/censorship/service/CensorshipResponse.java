package org.censorship.service;

import org.censorship.domain.enumeration.Status;

public class CensorshipResponse {
    private Status status;
    private String message;

    public CensorshipResponse() {
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
