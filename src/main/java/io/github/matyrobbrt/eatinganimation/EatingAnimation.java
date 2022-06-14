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

package io.github.matyrobbrt.eatinganimation;

import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkConstants;

@Mod(EatingAnimation.MOD_ID)
public class EatingAnimation {

    public static float animationTicks = 0;

    public static Set<String> compatibleMods;

    public static final List<Item> ANIMATED_FOOD = Lists.newArrayList(Items.APPLE, Items.BAKED_POTATO, Items.BEEF,
            Items.BEETROOT, Items.CARROT, Items.CHICKEN, Items.BREAD, Items.CHORUS_FRUIT, Items.COD, Items.COOKED_BEEF,
            Items.COOKED_CHICKEN, Items.COOKED_COD, Items.COOKED_MUTTON, Items.COOKED_PORKCHOP, Items.COOKED_RABBIT,
            Items.COOKED_SALMON, Items.COOKIE, Items.DRIED_KELP, Items.GOLDEN_APPLE, Items.GOLDEN_CARROT,
            Items.HONEY_BOTTLE, Items.MELON_SLICE, Items.MILK_BUCKET, Items.MUSHROOM_STEW, Items.MUTTON,
            Items.POISONOUS_POTATO, Items.PORKCHOP, Items.POTATO, Items.PUMPKIN_PIE, Items.RABBIT, Items.RABBIT_STEW,
            Items.BEETROOT_SOUP, Items.ROTTEN_FLESH, Items.SALMON, Items.SPIDER_EYE, Items.SUSPICIOUS_STEW,
            Items.SWEET_BERRIES, Items.TROPICAL_FISH, Items.ENCHANTED_GOLDEN_APPLE, Items.GLOW_BERRIES);

    public static final String MOD_ID = "eatinganimation";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static boolean wasInstalledBefore;

    public EatingAnimation() {
        final var configName = EatingAnimation.MOD_ID + "-client.toml";
        if (Files.exists(FMLPaths.CONFIGDIR.relative().resolve(configName).toAbsolutePath()))
            wasInstalledBefore = true;
        ModLoadingContext.get().registerConfig(Type.CLIENT, Config.SPEC, configName);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> new ClientSetup(FMLJavaModLoadingContext.get().getModEventBus()));
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        final var file = ModList.get().getModFileById(MOD_ID).getFile().findResource("compat");
        try {
            compatibleMods = Files.walk(file, 1).map(path -> file.relativize(path))
                    .filter(path -> path.getNameCount() > 0) // skip the root entry
                    .map(p -> p.toString().replaceAll("/$", "")) // remove the trailing slash, if present
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            LOGGER.error("Exception trying to resolve compatible mods: ", e);
            compatibleMods = Set.of();
        }
    }

}
