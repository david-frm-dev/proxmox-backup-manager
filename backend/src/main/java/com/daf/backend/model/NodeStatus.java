package com.daf.backend.model;

public enum NodeStatus {
    ONLINE,
    OFFLINE,
    STOPPED,
    UNKNOWN;

    @Override
    public String toString() {
        return switch (this) {
            case ONLINE -> "ONLINE";
            case OFFLINE -> "OFFLINE";
            case STOPPED -> "STOPPED";
            case UNKNOWN -> "UNKNOWN";
        };
    }

    public static NodeStatus fromString(String value) {
        for (NodeStatus s : values()) {
            if (s.name().equalsIgnoreCase(value)) return s;
        }
        return UNKNOWN;
    }
}
