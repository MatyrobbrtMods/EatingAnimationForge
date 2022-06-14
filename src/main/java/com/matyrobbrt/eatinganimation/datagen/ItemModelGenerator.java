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

import com.matyrobbrt.eatinganimation.EatingAnimation;

public class ItemModelGenerator {

    public static String generateItemModel(String itemName, String modNamespace, String normalTexture, float[] values) {
        return "{\"parent\":\"item/generated\",\"textures\":{\"layer0\":\"%1$s\"},\"overrides\":[{\"predicate\":{\"%2$s:eating\":1,\"%2$s:eat\":%5$s},\"model\":\"%2$s:item/%3$s/%4$s_0\"},{\"predicate\":{\"%2$s:eating\":1,\"%2$s:eat\":%6$s},\"model\":\"%2$s:item/%3$s/%4$s_1\"},{\"predicate\":{\"%2$s:eating\":1,\"%2$s:eat\":%7$s},\"model\":\"%2$s:item/%3$s/%4$s_2\"}]}"
                .formatted(normalTexture, EatingAnimation.MOD_ID, modNamespace, itemName, values[0], values[1],
                        values[2]);
    }

    public static String generateAnimationModel(String itemName, String modNamespace, int index) {
        return "{\"parent\":\"item/generated\",\"textures\":{\"layer0\":\"%s:item/%s/%s_%s\"}}"
                .formatted(EatingAnimation.MOD_ID, modNamespace, itemName, index);
    }

}
