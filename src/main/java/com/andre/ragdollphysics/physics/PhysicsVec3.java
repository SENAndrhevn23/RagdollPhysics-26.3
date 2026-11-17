package com.andre.ragdollphysics.physics;

/** Minimal immutable vector used by the standalone ragdoll solver. */
public record PhysicsVec3(double x, double y, double z) {
    public static final PhysicsVec3 ZERO = new PhysicsVec3(0.0, 0.0, 0.0);

    public PhysicsVec3 add(PhysicsVec3 other) {
        return new PhysicsVec3(x + other.x, y + other.y, z + other.z);
    }

    public PhysicsVec3 subtract(PhysicsVec3 other) {
        return new PhysicsVec3(x - other.x, y - other.y, z - other.z);
    }

    public PhysicsVec3 multiply(double scalar) {
        return new PhysicsVec3(x * scalar, y * scalar, z * scalar);
    }

    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public PhysicsVec3 normalized() {
        double magnitude = length();
        return magnitude < 1.0e-9 ? ZERO : multiply(1.0 / magnitude);
    }

    public PhysicsVec3 withY(double newY) {
        return new PhysicsVec3(x, newY, z);
    }
}
