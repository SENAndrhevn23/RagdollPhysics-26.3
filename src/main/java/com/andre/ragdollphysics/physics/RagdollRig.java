package com.andre.ragdollphysics.physics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A small 15-node articulated-body simulation used to drive the player model.
 * World collision is currently simplified to a horizontal ground plane.
 */
public final class RagdollRig {
    private static final PhysicsVec3 GRAVITY = new PhysicsVec3(0.0, -18.0, 0.0);
    private static final int SOLVER_ITERATIONS = 8;
    private static final double DAMPING = 0.995;
    private static final double GROUND_RESTITUTION = 0.08;

    private final Map<String, PhysicsNode> nodes = new LinkedHashMap<>();
    private final Map<String, PhysicsVec3> restPositions = new LinkedHashMap<>();
    private final List<DistanceConstraint> constraints = new ArrayList<>();

    public static RagdollRig createHumanoid(PhysicsVec3 origin, double scale) {
        if (scale <= 0.0 || !Double.isFinite(scale)) {
            throw new IllegalArgumentException("Scale must be positive and finite.");
        }

        RagdollRig rig = new RagdollRig();
        rig.addNode("head", origin.add(new PhysicsVec3(0.0, 1.62 * scale, 0.0)), 1.0);
        rig.addNode("chest", origin.add(new PhysicsVec3(0.0, 1.22 * scale, 0.0)), 2.0);
        rig.addNode("pelvis", origin.add(new PhysicsVec3(0.0, 0.88 * scale, 0.0)), 2.0);

        rig.addNode("left_shoulder", origin.add(new PhysicsVec3(-0.25 * scale, 1.34 * scale, 0.0)), 0.8);
        rig.addNode("left_elbow", origin.add(new PhysicsVec3(-0.25 * scale, 1.05 * scale, 0.0)), 0.6);
        rig.addNode("left_hand", origin.add(new PhysicsVec3(-0.25 * scale, 0.78 * scale, 0.0)), 0.4);
        rig.addNode("right_shoulder", origin.add(new PhysicsVec3(0.25 * scale, 1.34 * scale, 0.0)), 0.8);
        rig.addNode("right_elbow", origin.add(new PhysicsVec3(0.25 * scale, 1.05 * scale, 0.0)), 0.6);
        rig.addNode("right_hand", origin.add(new PhysicsVec3(0.25 * scale, 0.78 * scale, 0.0)), 0.4);

        rig.addNode("left_hip", origin.add(new PhysicsVec3(-0.13 * scale, 0.82 * scale, 0.0)), 1.0);
        rig.addNode("left_knee", origin.add(new PhysicsVec3(-0.14 * scale, 0.43 * scale, 0.0)), 0.8);
        rig.addNode("left_ankle", origin.add(new PhysicsVec3(-0.14 * scale, 0.08 * scale, 0.02 * scale)), 0.5);
        rig.addNode("right_hip", origin.add(new PhysicsVec3(0.13 * scale, 0.82 * scale, 0.0)), 1.0);
        rig.addNode("right_knee", origin.add(new PhysicsVec3(0.14 * scale, 0.43 * scale, 0.0)), 0.8);
        rig.addNode("right_ankle", origin.add(new PhysicsVec3(0.14 * scale, 0.08 * scale, 0.02 * scale)), 0.5);

        rig.connect("head", "chest");
        rig.connect("chest", "pelvis");
        rig.connect("chest", "left_shoulder");
        rig.connect("left_shoulder", "left_elbow");
        rig.connect("left_elbow", "left_hand");
        rig.connect("chest", "right_shoulder");
        rig.connect("right_shoulder", "right_elbow");
        rig.connect("right_elbow", "right_hand");
        rig.connect("pelvis", "left_hip");
        rig.connect("left_hip", "left_knee");
        rig.connect("left_knee", "left_ankle");
        rig.connect("pelvis", "right_hip");
        rig.connect("right_hip", "right_knee");
        rig.connect("right_knee", "right_ankle");
        rig.connect("left_shoulder", "right_shoulder");
        rig.connect("left_hip", "right_hip");

        return rig;
    }

    public void step(double deltaTime, double groundY) {
        step(deltaTime, groundY, false);
    }

    /**
     * Advances the articulated body. Active mode applies a soft righting force
     * toward the standing pose instead of pinning the body in place.
     */
    public void step(double deltaTime, double groundY, boolean active) {
        double safeDeltaTime = Math.max(0.0, Math.min(deltaTime, 0.05));
        if (safeDeltaTime == 0.0) {
            return;
        }

        for (Map.Entry<String, PhysicsNode> entry : nodes.entrySet()) {
            PhysicsNode node = entry.getValue();
            node.integrate(GRAVITY, safeDeltaTime, active ? 0.985 : DAMPING);

            if (active) {
                PhysicsVec3 rest = restPositions.get(entry.getKey());
                PhysicsVec3 displacement = rest.subtract(node.position());
                double strength = entry.getKey().equals("pelvis") || entry.getKey().equals("chest") ? 7.0 : 3.0;
                node.addAcceleration(displacement.multiply(strength), safeDeltaTime);
            }
        }

        for (int iteration = 0; iteration < SOLVER_ITERATIONS; iteration++) {
            for (DistanceConstraint constraint : constraints) {
                constraint.solve();
            }
            for (PhysicsNode node : nodes.values()) {
                node.resolveGround(groundY, GROUND_RESTITUTION);
            }
        }
    }

    public PhysicsNode node(String name) {
        PhysicsNode node = nodes.get(name);
        if (node == null) {
            throw new IllegalArgumentException("Unknown ragdoll node: " + name);
        }
        return node;
    }

    public PhysicsVec3 interpolatedPosition(String name, double partialTick) {
        return node(name).interpolatedPosition(partialTick);
    }

    public void addVelocity(String name, PhysicsVec3 velocity, double deltaTime) {
        node(name).addVelocity(velocity, deltaTime);
    }

    public Map<String, PhysicsNode> nodes() {
        return Collections.unmodifiableMap(nodes);
    }

    private void addNode(String name, PhysicsVec3 position, double mass) {
        nodes.put(name, new PhysicsNode(position, mass));
        restPositions.put(name, position);
    }

    private void connect(String first, String second) {
        PhysicsNode firstNode = nodes.get(first);
        PhysicsNode secondNode = nodes.get(second);
        if (firstNode == null || secondNode == null) {
            throw new IllegalStateException("Unknown ragdoll node in constraint: " + first + " / " + second);
        }
        constraints.add(new DistanceConstraint(firstNode, secondNode, 0.85));
    }
}
