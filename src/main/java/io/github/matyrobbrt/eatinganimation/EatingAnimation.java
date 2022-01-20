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

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;

@Mod(EatingAnimation.MOD_ID)
public class EatingAnimation {

	public static float animationTicks = 0;

	public static final List<Item> ANIMATED_FOOD = Lists.newArrayList(Items.APPLE, Items.BAKED_POTATO, Items.BEEF,
			Items.BEETROOT, Items.CARROT, Items.CHICKEN, Items.BREAD, Items.CHORUS_FRUIT, Items.COD, Items.COOKED_BEEF,
			Items.COOKED_CHICKEN, Items.COOKED_COD, Items.COOKED_MUTTON, Items.COOKED_PORKCHOP, Items.COOKED_RABBIT,
			Items.COOKED_SALMON, Items.COOKIE, Items.DRIED_KELP, Items.GOLDEN_APPLE, Items.GOLDEN_CARROT,
			Items.HONEY_BOTTLE, Items.MELON_SLICE, Items.MILK_BUCKET, Items.MUSHROOM_STEW, Items.MUTTON,
			Items.POISONOUS_POTATO, Items.PORKCHOP, Items.POTATO, Items.PUMPKIN_PIE, Items.RABBIT, Items.RABBIT_STEW,
			Items.BEETROOT_SOUP, Items.ROTTEN_FLESH, Items.SALMON, Items.SPIDER_EYE, Items.SUSPICIOUS_STEW,
			Items.SWEET_BERRIES, Items.TROPICAL_FISH, Items.ENCHANTED_GOLDEN_APPLE);

	public static final String MOD_ID = "eatinganimation";

	public EatingAnimation() {
		ModLoadingContext.get().registerConfig(Type.CLIENT, EatingAnimation.Config.SPEC,
				EatingAnimation.MOD_ID + "-client.toml");
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
				() -> () -> new ClientSetup(FMLJavaModLoadingContext.get().getModEventBus()));
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
				() -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
	}

	public static final class Config {

		public static final ForgeConfigSpec SPEC;
		public static final ForgeConfigSpec.Builder BUILDER = new Builder();

		public static final ForgeConfigSpec.BooleanValue RENDER_INVENTORY_EATING;

		static {
			BUILDER.push("general");

			RENDER_INVENTORY_EATING = BUILDER
					.comment("If the eating animation of items should be rendered in the inventory as well.")
					.define("renderInventoryEating", true);

			BUILDER.pop();
			SPEC = BUILDER.build();
		}
	}
}
