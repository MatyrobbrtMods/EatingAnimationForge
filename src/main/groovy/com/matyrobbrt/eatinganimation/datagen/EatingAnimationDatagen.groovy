/*
 * Copyright (c) Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.eatinganimation.datagen

import groovy.transform.CompileStatic
import net.minecraft.data.DataGenerator
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent

@CompileStatic
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
class EatingAnimationDatagen {

    @SubscribeEvent
    static void gatherData(final GatherDataEvent event) {
        final DataGenerator gen = event.getGenerator()
        final existingFileHelper = event.getExistingFileHelper()
        gen.addProvider(event.includeClient(), new ItemModelsProvider(gen, existingFileHelper))
    }

}
