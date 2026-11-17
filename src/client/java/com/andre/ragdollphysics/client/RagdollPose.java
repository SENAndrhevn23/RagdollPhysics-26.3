package com.andre.ragdollphysics.client;

import com.andre.ragdollphysics.physics.PhysicsVec3;
import com.andre.ragdollphysics.physics.RagdollRig;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;

/** Maps the simulated joint positions onto the vanilla player model's bones. */
public final class RagdollPose {
    private RagdollPose() {
    }

    public static void apply(PlayerModel model, RagdollRig rig, float partialTick) {
        if (rig == null) {
            return;
        }

        PhysicsVec3 pelvis = rig.interpolatedPosition("pelvis", partialTick);
        PhysicsVec3 chest = rig.interpolatedPosition("chest", partialTick);
        PhysicsVec3 head = rig.interpolatedPosition("head", partialTick);

        PhysicsVec3 leftShoulder = rig.interpolatedPosition("left_shoulder", partialTick);
        PhysicsVec3 leftHand = rig.interpolatedPosition("left_hand", partialTick);
        PhysicsVec3 rightShoulder = rig.interpolatedPosition("right_shoulder", partialTick);
        PhysicsVec3 rightHand = rig.interpolatedPosition("right_hand", partialTick);

        PhysicsVec3 leftHip = rig.interpolatedPosition("left_hip", partialTick);
        PhysicsVec3 leftAnkle = rig.interpolatedPosition("left_ankle", partialTick);
        PhysicsVec3 rightHip = rig.interpolatedPosition("right_hip", partialTick);
        PhysicsVec3 rightAnkle = rig.interpolatedPosition("right_ankle", partialTick);

        float bodyPitch = pitchUp(pelvis, chest);
        float bodyRoll = rollUp(pelvis, chest);
        setRotation(model.body, bodyPitch, model.body.yRot, bodyRoll);

        float headPitch = pitchUp(chest, head);
        float headRoll = rollUp(chest, head);
        setRotation(model.head, headPitch, 0.0F, headRoll);
        setRotation(model.hat, headPitch, 0.0F, headRoll);

        float leftArmPitch = pitchDown(leftShoulder, leftHand);
        float leftArmRoll = rollDown(leftShoulder, leftHand);
        setRotation(model.leftArm, leftArmPitch, model.leftArm.yRot, leftArmRoll);
        setRotation(model.leftSleeve, leftArmPitch, model.leftSleeve.yRot, leftArmRoll);

        float rightArmPitch = pitchDown(rightShoulder, rightHand);
        float rightArmRoll = rollDown(rightShoulder, rightHand);
        setRotation(model.rightArm, rightArmPitch, model.rightArm.yRot, rightArmRoll);
        setRotation(model.rightSleeve, rightArmPitch, model.rightSleeve.yRot, rightArmRoll);

        float leftLegPitch = pitchDown(leftHip, leftAnkle);
        float leftLegRoll = rollDown(leftHip, leftAnkle);
        setRotation(model.leftLeg, leftLegPitch, 0.0F, leftLegRoll);
        setRotation(model.leftPants, leftLegPitch, 0.0F, leftLegRoll);

        float rightLegPitch = pitchDown(rightHip, rightAnkle);
        float rightLegRoll = rollDown(rightHip, rightAnkle);
        setRotation(model.rightLeg, rightLegPitch, 0.0F, rightLegRoll);
        setRotation(model.rightPants, rightLegPitch, 0.0F, rightLegRoll);

        setRotation(model.jacket, bodyPitch, model.jacket.yRot, bodyRoll);
    }

    private static void setRotation(net.minecraft.client.model.geom.ModelPart part,
                                    float x, float y, float z) {
        part.xRot = x;
        part.yRot = y;
        part.zRot = z;
    }

    private static float pitchUp(PhysicsVec3 from, PhysicsVec3 to) {
        PhysicsVec3 d = to.subtract(from);
        return (float) Math.atan2(d.z(), Math.max(1.0e-5, d.y()));
    }

    private static float rollUp(PhysicsVec3 from, PhysicsVec3 to) {
        PhysicsVec3 d = to.subtract(from);
        return (float) -Math.atan2(d.x(), Math.max(1.0e-5, d.y()));
    }

    private static float pitchDown(PhysicsVec3 from, PhysicsVec3 to) {
        PhysicsVec3 d = to.subtract(from);
        return (float) Math.atan2(d.z(), Math.max(1.0e-5, -d.y()));
    }

    private static float rollDown(PhysicsVec3 from, PhysicsVec3 to) {
        PhysicsVec3 d = to.subtract(from);
        return (float) Math.atan2(d.x(), Math.max(1.0e-5, -d.y()));
    }
}
