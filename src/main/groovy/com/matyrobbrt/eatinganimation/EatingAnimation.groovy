/*
 * Copyright (c) Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.eatinganimation

import com.matyrobbrt.gml.GMod
import groovy.transform.CompileStatic
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.config.ModConfig.Type
import net.minecraftforge.fml.loading.FMLPaths
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Files
import java.util.stream.Collectors

@CompileStatic
@GMod(EatingAnimation.MOD_ID)
@SuppressWarnings('UnnecessaryQualifiedReference') // Stubs...
class EatingAnimation {

    public static float animationTicks = 0

    public static Set<String> compatibleMods

    public static final List<Item> ANIMATED_FOOD = [Items.APPLE, Items.BAKED_POTATO, Items.BEEF,
            Items.BEETROOT, Items.CARROT, Items.CHICKEN, Items.BREAD, Items.CHORUS_FRUIT, Items.COD, Items.COOKED_BEEF,
            Items.COOKED_CHICKEN, Items.COOKED_COD, Items.COOKED_MUTTON, Items.COOKED_PORKCHOP, Items.COOKED_RABBIT,
            Items.COOKED_SALMON, Items.COOKIE, Items.DRIED_KELP, Items.GOLDEN_APPLE, Items.GOLDEN_CARROT,
            Items.HONEY_BOTTLE, Items.MELON_SLICE, Items.MILK_BUCKET, Items.MUSHROOM_STEW, Items.MUTTON,
            Items.POISONOUS_POTATO, Items.PORKCHOP, Items.POTATO, Items.PUMPKIN_PIE, Items.RABBIT, Items.RABBIT_STEW,
            Items.BEETROOT_SOUP, Items.ROTTEN_FLESH, Items.SALMON, Items.SPIDER_EYE, Items.SUSPICIOUS_STEW,
            Items.SWEET_BERRIES, Items.TROPICAL_FISH, Items.ENCHANTED_GOLDEN_APPLE, Items.GLOW_BERRIES]

    public static final String MOD_ID = 'eatinganimation'
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID)

    public static boolean wasInstalledBefore

    EatingAnimation() {
        final configName = "$MOD_ID-client.toml"
        if (Files.exists(FMLPaths.CONFIGDIR.relative().resolve(configName).toAbsolutePath()))
            wasInstalledBefore = true

        ModLoadingContext.get().registerConfig(Type.CLIENT, Config.SPEC, configName)

        final var file = ModList.get().getModFileById(MOD_ID).getFile().findResource("compat");
        try (final var stream = Files.walk(file, 1)) {
            compatibleMods = stream.map { file.relativize(it) }
                    .filter(path -> path.getNameCount() > 0) // skip the root entry
                    .map(p -> p.toString().replaceAll('/$', '')) // remove the trailing slash, if present
                    .collect(Collectors.toSet())
        } catch (Exception e) {
            LOGGER.error("Exception trying to resolve compatible mods: ", e)
            compatibleMods = Set.of()
        }
    }
}
