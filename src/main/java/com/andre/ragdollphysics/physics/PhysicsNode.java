package com.andre.ragdollphysics.physics;

/** A Verlet-integrated point in the articulated body. */
public final class PhysicsNode {
    private PhysicsVec3 position;
    private PhysicsVec3 previousPosition;
    private final double inverseMass;

    public PhysicsNode(PhysicsVec3 position, double mass) {
        if (!Double.isFinite(mass) || mass < 0.0) {
            throw new IllegalArgumentException("Mass must be finite and non-negative.");
        }
        this.position = position;
        this.previousPosition = position;
        this.inverseMass = mass == 0.0 ? 0.0 : 1.0 / mass;
    }

    public PhysicsVec3 position() {
        return position;
    }

    public double inverseMass() {
        return inverseMass;
    }

    public void integrate(PhysicsVec3 gravity, double deltaTime, double damping) {
        if (inverseMass == 0.0) {
            return;
        }

        PhysicsVec3 velocity = position.subtract(previousPosition).multiply(damping);
        PhysicsVec3 nextPosition = position
                .add(velocity)
                .add(gravity.multiply(deltaTime * deltaTime));
        previousPosition = position;
        position = nextPosition;
    }

    public void moveBy(PhysicsVec3 offset) {
        if (inverseMass != 0.0) {
            position = position.add(offset);
        }
    }

    public void resolveGround(double groundY, double restitution) {
        if (inverseMass == 0.0 || position.y() >= groundY) {
            return;
        }

        double impactVelocityY = position.y() - previousPosition.y();
        position = position.withY(groundY);
        previousPosition = previousPosition.withY(groundY + impactVelocityY * restitution);
    }
}
