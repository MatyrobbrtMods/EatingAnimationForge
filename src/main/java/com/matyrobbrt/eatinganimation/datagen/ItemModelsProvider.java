/**
 * This file is part of the Eating Animation Minecraft mod and is licensed under
 * the MIT license:
 *
 * MIT License
 *
 * Copyright (c) 2022 Matyrobbrt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.matyrobbrt.eatinganimation.datagen;

import java.util.List;

import com.google.common.collect.Lists;
import com.matyrobbrt.eatinganimation.EatingAnimation;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import static com.matyrobbrt.eatinganimation.EatingAnimation.MOD_ID;

import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

class ItemModelsProvider extends ItemModelProvider {

	public ItemModelsProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, "minecraft", existingFileHelper);
	}

	private final ModelFile itemGenerated = getExistingFile(mcLoc("item/generated"));

	@Override
	protected void registerModels() {
		EatingAnimation.ANIMATED_FOOD.stream().filter(i -> i != Items.ENCHANTED_GOLDEN_APPLE).forEach(this::buildModel);
		buildModel(Items.ENCHANTED_GOLDEN_APPLE, "golden_apple", .35f, .70f, .90f);
	}

	private ItemModelBuilder buildModel(Item item) {
		return buildModel(item, 0.35f, 0.70f, 0.90f);
	}

	private ItemModelBuilder buildModel(Item item, float... eatProgress) {
		return buildModel(item, item.getRegistryName().getPath(), eatProgress);
	}

	private ItemModelBuilder buildModel(Item item, String initialTexture, float... eatProgress) {
		final ItemModelBuilder builder = getBuilder(item.getRegistryName().toString()).parent(itemGenerated)
				.texture("layer0", "item/" + initialTexture);

		for (int i = 0; i < eatProgress.length; i++) {
			final String modelName = String.format("%s_%s_%s",
					item == Items.ENCHANTED_GOLDEN_APPLE ? "golden_apple" : item.getRegistryName().getPath(),
					DRINKABLES.contains(item) ? "drinking" : "eating", i);
			final ItemModelBuilder overrideModel = getBuilder(MOD_ID + ":" + modelName).parent(itemGenerated)
					.texture("layer0",
					new ResourceLocation(MOD_ID, "item/" + modelName));
			builder.override().predicate(new ResourceLocation(MOD_ID, "eating"), 1)
					.predicate(new ResourceLocation(MOD_ID, "eat"), eatProgress[i]).model(overrideModel);
		}

		return builder;
	}

	private static final List<Item> DRINKABLES = Lists.newArrayList(Items.MILK_BUCKET, Items.HONEY_BOTTLE);

}
