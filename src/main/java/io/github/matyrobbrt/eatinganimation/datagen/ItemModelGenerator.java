package io.github.matyrobbrt.eatinganimation.datagen;

import io.github.matyrobbrt.eatinganimation.EatingAnimation;

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

    public static void main(String[] args) {
        System.out.println(generateItemModel("test", "myNamespace", "hello:kek", new float[] {
                0.1f, 0.2f, 0.3f
        }));
    }

}
