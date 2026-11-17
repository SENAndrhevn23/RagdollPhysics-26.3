package com.andre.ragdollphysics.physics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A small articulated-body physics prototype.
 * It is not yet connected to Minecraft's player renderer or collision world.
 */
public final class RagdollRig {
    private static final PhysicsVec3 GRAVITY = new PhysicsVec3(0.0, -18.0, 0.0);
    private static final int SOLVER_ITERATIONS = 8;
    private static final double DAMPING = 0.995;
    private static final double GROUND_RESTITUTION = 0.08;

    private final Map<String, PhysicsNode> nodes = new LinkedHashMap<>();
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
        rig.addNode("left_elbow", origin.add(new PhysicsVec3(-0.39 * scale, 1.05 * scale, 0.0)), 0.6);
        rig.addNode("left_hand", origin.add(new PhysicsVec3(-0.48 * scale, 0.78 * scale, 0.0)), 0.4);
        rig.addNode("right_shoulder", origin.add(new PhysicsVec3(0.25 * scale, 1.34 * scale, 0.0)), 0.8);
        rig.addNode("right_elbow", origin.add(new PhysicsVec3(0.39 * scale, 1.05 * scale, 0.0)), 0.6);
        rig.addNode("right_hand", origin.add(new PhysicsVec3(0.48 * scale, 0.78 * scale, 0.0)), 0.4);

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
        double safeDeltaTime = Math.max(0.0, Math.min(deltaTime, 0.05));
        if (safeDeltaTime == 0.0) {
            return;
        }

        for (PhysicsNode node : nodes.values()) {
            node.integrate(GRAVITY, safeDeltaTime, DAMPING);
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

    public Map<String, PhysicsNode> nodes() {
        return Collections.unmodifiableMap(nodes);
    }

    private void addNode(String name, PhysicsVec3 position, double mass) {
        nodes.put(name, new PhysicsNode(position, mass));
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
