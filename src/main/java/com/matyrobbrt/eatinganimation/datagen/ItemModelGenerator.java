/*
 * Copyright (c) Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.eatinganimation.datagen;

import com.matyrobbrt.eatinganimation.EatingAnimation;

import java.util.List;

public class ItemModelGenerator {

    public static String generateItemModel(String itemName, String modNamespace, String normalTexture, List<Float> values) {
        return "{\"parent\":\"item/generated\",\"textures\":{\"layer0\":\"%1$s\"},\"overrides\":[{\"predicate\":{\"%2$s:eating\":1,\"%2$s:eat\":%5$s},\"model\":\"%2$s:item/%3$s/%4$s_0\"},{\"predicate\":{\"%2$s:eating\":1,\"%2$s:eat\":%6$s},\"model\":\"%2$s:item/%3$s/%4$s_1\"},{\"predicate\":{\"%2$s:eating\":1,\"%2$s:eat\":%7$s},\"model\":\"%2$s:item/%3$s/%4$s_2\"}]}"
                .formatted(normalTexture, EatingAnimation.MOD_ID, modNamespace, itemName, values.get(0), values.get(1),
                        values.get(2));
    }

    public static String generateAnimationModel(String itemName, String modNamespace, int index) {
        return "{\"parent\":\"item/generated\",\"textures\":{\"layer0\":\"%s:item/%s/%s_%s\"}}"
                .formatted(EatingAnimation.MOD_ID, modNamespace, itemName, index);
    }

}
