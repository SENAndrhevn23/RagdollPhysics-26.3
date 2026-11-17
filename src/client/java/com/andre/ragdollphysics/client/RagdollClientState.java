package com.andre.ragdollphysics.client;

/** Client-side switch state for the initial R-key prototype. */
public final class RagdollClientState {
    private static boolean enabled;

    private RagdollClientState() {
    }

    public static boolean toggle() {
        enabled = !enabled;
        return enabled;
    }

    public static boolean isEnabled() {
        return enabled;
    }
}
