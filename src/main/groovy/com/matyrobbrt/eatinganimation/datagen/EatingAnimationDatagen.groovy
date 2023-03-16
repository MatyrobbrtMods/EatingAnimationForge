/*
 * Copyright (c) Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.eatinganimation.datagen

import com.matyrobbrt.gml.bus.EventBusSubscriber
import com.matyrobbrt.gml.bus.type.ModBus
import com.matyrobbrt.gml.util.Environment
import groovy.transform.CompileStatic
import net.minecraft.data.DataGenerator
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

@CompileStatic
@EventBusSubscriber(value = ModBus, environment = [Environment.DEV], dist = [Dist.CLIENT])
class EatingAnimationDatagen {

    @SubscribeEvent
    static void gatherData(final GatherDataEvent event) {
        final DataGenerator gen = event.getGenerator()
        final ExistingFileHelper existingFileHelper = event.getExistingFileHelper()
        gen.addProvider(event.includeClient(), new ItemModelsProvider(gen, existingFileHelper))
    }

}
