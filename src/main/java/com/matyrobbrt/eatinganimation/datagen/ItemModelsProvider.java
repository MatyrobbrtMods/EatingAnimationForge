/*
 * Copyright (c) Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.eatinganimation.datagen;

import java.util.List;

import com.google.common.collect.Lists;
import com.matyrobbrt.eatinganimation.EatingAnimation;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import static com.matyrobbrt.eatinganimation.EatingAnimation.MOD_ID;

import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

@SuppressWarnings("deprecation")
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
        return buildModel(item, BuiltInRegistries.ITEM.getKey(item).getPath(), eatProgress);
    }

    private ItemModelBuilder buildModel(Item item, String initialTexture, float... eatProgress) {
        final ItemModelBuilder builder = getBuilder(BuiltInRegistries.ITEM.getKey(item).toString()).parent(itemGenerated)
                .texture("layer0", "item/" + initialTexture);

        for (int i = 0; i < eatProgress.length; i++) {
            final String modelName = String.format("%s_%s_%s",
                    item == Items.ENCHANTED_GOLDEN_APPLE ? "golden_apple" : BuiltInRegistries.ITEM.getKey(item).getPath(),
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
