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

import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@SuppressWarnings("deprecation")
public class ClientSetup {

	public ClientSetup(final IEventBus modBus) {
		modBus.addListener(this::onClientSetup);
	}

	private void onClientSetup(final FMLClientSetupEvent event) {
		ItemProperties.registerGeneric(new ResourceLocation(EatingAnimation.MOD_ID, "eat"), EAT_PROPERTY);
		ItemProperties.registerGeneric(new ResourceLocation(EatingAnimation.MOD_ID, "eating"), EATING_PROPERTY);
	}

	public static final ItemPropertyFunction EAT_PROPERTY = (stack, world, entity, i) -> {
		if (entity == null) { return 0.0F; }
		if (entity instanceof RemotePlayer && entity.getTicksUsingItem() > 31) {
			return EatingAnimation.animationTicks / 30;
		}
		return entity.getUseItem() != stack ? 0.0F
				: (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 30.0F;
	};

	private static final ItemPropertyFunction EATING_PROPERTY = (stack, world, entity, i) -> {
		if (entity == null) { return 0.0F; }
		return entity.isUsingItem() && entity.getUseItem() == stack ? 1 : 0;
	};

}
