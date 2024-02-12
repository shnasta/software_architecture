package org.ucu.apps;

import java.util.UUID;

public class Message {
    private UUID id;
    private String message;

    public Message(UUID id, String message) {
        this.id = id;
        this.message = message;
    }

    public UUID getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
