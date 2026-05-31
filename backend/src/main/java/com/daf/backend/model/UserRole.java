package com.daf.backend.model;

public enum UserRole {
    Admin,
    Viewer;

    @Override
    public String toString() {
        return switch (this) {
            case Admin -> "Admin";
            case Viewer -> "Viewer";
        };
    }
}