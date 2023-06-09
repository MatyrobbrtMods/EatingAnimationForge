/*
 * Copyright (c) Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.eatinganimation

import groovy.transform.CompileStatic
import net.minecraft.client.player.RemotePlayer
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import org.groovymc.gml.bus.EventBusSubscriber
import org.groovymc.gml.bus.type.ForgeBus

@CompileStatic
@EventBusSubscriber(value = ForgeBus, dist = [Dist.CLIENT])
class EventListeners {
    @SubscribeEvent
    static void onPlayerTick(final TickEvent.PlayerTickEvent event) {
        if (event.@phase === TickEvent.Phase.START && event.player instanceof RemotePlayer) {
            if (event.player.getTicksUsingItem() > 31) {
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
