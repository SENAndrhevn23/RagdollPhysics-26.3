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

    public PhysicsVec3 interpolatedPosition(double partialTick) {
        double alpha = Math.max(0.0, Math.min(partialTick, 1.0));
        return previousPosition.add(position.subtract(previousPosition).multiply(alpha));
    }

    /** Adds a velocity in metres per second to this Verlet node. */
    public void addVelocity(PhysicsVec3 velocity, double deltaTime) {
        if (inverseMass != 0.0) {
            previousPosition = previousPosition.subtract(velocity.multiply(deltaTime));
        }
    }

    /** Adds an acceleration in metres per second squared. */
    public void addAcceleration(PhysicsVec3 acceleration, double deltaTime) {
        if (inverseMass != 0.0) {
            previousPosition = previousPosition.subtract(acceleration.multiply(deltaTime * deltaTime));
        }
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
        double horizontalFriction = 0.72;
        double velocityX = position.x() - previousPosition.x();
        double velocityZ = position.z() - previousPosition.z();
        position = position.withY(groundY);
        previousPosition = new PhysicsVec3(
                position.x() - velocityX * horizontalFriction,
                groundY + impactVelocityY * restitution,
                position.z() - velocityZ * horizontalFriction
        );
    }
}
