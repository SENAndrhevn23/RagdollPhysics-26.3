package com.andre.ragdollphysics.client;

import com.andre.ragdollphysics.config.RagdollMode;
import com.andre.ragdollphysics.physics.PhysicsVec3;
import com.andre.ragdollphysics.physics.RagdollRig;

/** Client-side simulation for the local player's visible ragdoll pose. */
public final class RagdollClientState {
    private static final double FIXED_STEP = 1.0 / 20.0;
    private static RagdollRig rig;
    private static RagdollMode mode = RagdollMode.ACTIVE;

    private RagdollClientState() {
    }

    public static boolean isEnabled() {
        return rig != null;
    }

    public static RagdollMode mode() {
        return mode;
    }

    public static RagdollRig rig() {
        return rig;
    }

    public static boolean toggle(RagdollMode selectedMode) {
        if (rig != null) {
            rig = null;
            return false;
        }

        mode = selectedMode;
        rig = RagdollRig.createHumanoid(PhysicsVec3.ZERO, 1.0);

        // A small initial disturbance makes the body visibly lose its balance.
        double impulse = mode == RagdollMode.COMPLETE ? 2.2 : 0.8;
        rig.addVelocity("chest", new PhysicsVec3(impulse, 0.25, 1.25), FIXED_STEP);
        rig.addVelocity("head", new PhysicsVec3(impulse * 0.7, 0.0, 1.5), FIXED_STEP);
        rig.addVelocity("left_hand", new PhysicsVec3(-0.8, 0.0, 0.2), FIXED_STEP);
        rig.addVelocity("right_hand", new PhysicsVec3(0.8, 0.0, -0.2), FIXED_STEP);
        return true;
    }

    public static void tick() {
        if (rig != null) {
            rig.step(FIXED_STEP, 0.045, mode == RagdollMode.ACTIVE);
        }
    }

    public static void reset() {
        rig = null;
    }
}
