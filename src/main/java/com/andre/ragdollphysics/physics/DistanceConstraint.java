package com.andre.ragdollphysics.physics;

/** Maintains the rest distance between two body points. */
public final class DistanceConstraint {
    private final PhysicsNode first;
    private final PhysicsNode second;
    private final double restLength;
    private final double stiffness;

    public DistanceConstraint(PhysicsNode first, PhysicsNode second, double stiffness) {
        if (stiffness < 0.0 || stiffness > 1.0) {
            throw new IllegalArgumentException("Stiffness must be between 0 and 1.");
        }
        this.first = first;
        this.second = second;
        this.restLength = second.position().subtract(first.position()).length();
        this.stiffness = stiffness;
    }

    public void solve() {
        PhysicsVec3 delta = second.position().subtract(first.position());
        double distance = delta.length();
        double inverseMassTotal = first.inverseMass() + second.inverseMass();

        if (distance < 1.0e-9 || inverseMassTotal == 0.0) {
            return;
        }

        double errorRatio = (distance - restLength) / distance;
        PhysicsVec3 correction = delta.multiply(errorRatio * stiffness);

        first.moveBy(correction.multiply(first.inverseMass() / inverseMassTotal));
        second.moveBy(correction.multiply(-second.inverseMass() / inverseMassTotal));
    }
}
