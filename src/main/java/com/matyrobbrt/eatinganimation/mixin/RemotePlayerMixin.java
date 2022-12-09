/*
 * Copyright (c) Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.eatinganimation.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.matyrobbrt.eatinganimation.EatingAnimation;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.player.ProfilePublicKey;

@Mixin(RemotePlayer.class)
public abstract class RemotePlayerMixin extends AbstractClientPlayer {

    private RemotePlayerMixin(ClientLevel p_234112_, GameProfile p_234113_) {
        super(p_234112_, p_234113_);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void eatinganimation$tickEatingAnimation(CallbackInfo ci) {
        if (this.getTicksUsingItem() > 31) {
            // Increase the static animation ticks field, for rendering the model in
            // multi-player
            if (EatingAnimation.animationTicks < 31) {
                ++EatingAnimation.animationTicks;
            } else {
                EatingAnimation.animationTicks = 0;
            }
        }
    }

}
