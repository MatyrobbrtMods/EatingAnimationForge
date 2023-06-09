/*
 * Copyright (c) Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.eatinganimation.datagen

import groovy.transform.CompileStatic
import net.minecraft.data.DataGenerator
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import org.groovymc.gml.bus.EventBusSubscriber
import org.groovymc.gml.bus.type.ModBus
import org.groovymc.gml.util.Environment

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
