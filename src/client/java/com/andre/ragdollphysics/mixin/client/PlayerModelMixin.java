package com.andre.ragdollphysics.mixin.client;

import com.andre.ragdollphysics.client.RagdollClientState;
import com.andre.ragdollphysics.client.RagdollPose;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin extends HumanoidModel<AvatarRenderState> {
    protected PlayerModelMixin(ModelPart root) {
        super(root);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V", at = @At("TAIL"))
    private void ragdollphysics$applyLocalRagdoll(AvatarRenderState state, CallbackInfo callback) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null || state.id != localPlayer.getId() || !RagdollClientState.isEnabled()) {
            return;
        }

        float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        RagdollPose.apply((PlayerModel) (Object) this, RagdollClientState.rig(), partialTick);
    }
}
