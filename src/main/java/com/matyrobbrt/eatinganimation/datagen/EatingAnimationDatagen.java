package com.matyrobbrt.eatinganimation.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EatingAnimationDatagen {

	@SubscribeEvent
	public static void gatherData(final GatherDataEvent event) {
		final DataGenerator gen = event.getGenerator();
		final ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        gen.addProvider(event.includeClient(), new ItemModelsProvider(gen, existingFileHelper));
	}

}
