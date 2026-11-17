package com.andre.ragdollphysics.client;

import com.andre.ragdollphysics.RagdollPhysics;
import com.andre.ragdollphysics.config.RagdollConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

public final class RagdollPhysicsClient implements ClientModInitializer {
    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(RagdollPhysics.id("main"));

    private static KeyMapping toggleRagdollKey;

    @Override
    public void onInitializeClient() {
        RagdollConfig.load();

        toggleRagdollKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.ragdollphysics.toggle",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_R,
                CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleRagdollKey.consumeClick()) {
                boolean enabled = RagdollClientState.toggle(RagdollConfig.get().getPhysicsMode());
                if (client.player != null) {
                    String message = enabled
                            ? "Ragdoll physics enabled (" + RagdollConfig.get().getPhysicsMode().displayName() + ") - use F5 to see your full body"
                            : "Ragdoll physics disabled";
                    client.player.sendOverlayMessage(Component.literal(message));
                }
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                RagdollClientState.reset();
                return;
            }
            RagdollClientState.tick();
        });

        RagdollPhysics.LOGGER.info("R key registered for the ragdoll prototype.");
    }
}
