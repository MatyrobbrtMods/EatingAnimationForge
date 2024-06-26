/*
 * Copyright (c) Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.eatinganimation

import groovy.transform.CompileStatic
import net.minecraft.client.player.RemotePlayer
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.tick.PlayerTickEvent

@CompileStatic
@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, value = [Dist.CLIENT])
class EventListeners {
    @SubscribeEvent
    static void onPlayerTick(final PlayerTickEvent.Pre event) {
        if (event.entity instanceof RemotePlayer) {
            if (event.entity.getTicksUsingItem() > 31) {
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
}
