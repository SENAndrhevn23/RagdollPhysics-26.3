package com.andre.ragdollphysics.config;

public enum RagdollMode {
    COMPLETE("Complete ragdoll"),
    ACTIVE("Active ragdoll");

    private final String displayName;

    RagdollMode(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public static RagdollMode fromConfig(String value) {
        if (value == null) {
            return ACTIVE;
        }

        try {
            return RagdollMode.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            return ACTIVE;
        }
    }
}
