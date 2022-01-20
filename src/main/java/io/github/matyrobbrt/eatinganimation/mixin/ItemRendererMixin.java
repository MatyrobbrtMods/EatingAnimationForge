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

package io.github.matyrobbrt.eatinganimation.mixin;

import java.util.Arrays;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import io.github.matyrobbrt.eatinganimation.EatingAnimation;
import io.github.matyrobbrt.eatinganimation.EatingAnimation.Config;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin implements ResourceManagerReloadListener {

	@Final
	@Shadow
	private ItemModelShaper itemModelShaper;
	@Shadow
	public float blitOffset;

	@Shadow
	protected abstract void renderGuiItem(ItemStack stack, int x, int y, BakedModel model);

	private BakedModel transformEatingModel(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity,
			int seed) {
		final BakedModel initialModel = this.itemModelShaper.getItemModel(stack);
		final ClientLevel clientLevel = level instanceof ClientLevel cl ? cl : null;
		final BakedModel initialOverridenModel = initialModel.getOverrides().resolve(initialModel, stack, clientLevel,
				entity, seed);
		BakedModel transformedModel = checkOverrideContains(initialModel.getOverrides(),
				new ResourceLocation(EatingAnimation.MOD_ID, "eat")) ? this.itemModelShaper.getItemModel(stack)
						: initialOverridenModel;
		return transformedModel == null ? itemModelShaper.getModelManager().getMissingModel() : transformedModel;
	}

	@Inject(at = @At("HEAD"), method = "Lnet/minecraft/client/renderer/entity/ItemRenderer;tryRenderGuiItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;III)V", cancellable = true)
	private void eatinganimation$tryRenderGuiItem(@Nullable LivingEntity entity, ItemStack stack, int x, int y,
			int seed,
			CallbackInfo ci) {
		if (stack.isEmpty() || !stack.isEdible() || entity == null
				|| Boolean.TRUE.equals(Config.RENDER_INVENTORY_EATING.get())) {
			return;
		}
		if (entity.getUseItem() != stack) { return; }

		final var model = transformEatingModel(stack, null, entity, seed);
		this.blitOffset = model.isGui3d() ? this.blitOffset + 50.0F + seed : this.blitOffset + 50.0F;
		try {
			this.renderGuiItem(stack, x, y, model);
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering item");
			CrashReportCategory crashreportcategory = crashreport.addCategory("Item being rendered");
			crashreportcategory.setDetail("Item Type", () -> {
				return String.valueOf(stack.getItem());
			});
			crashreportcategory.setDetail("Registry Name", () -> String.valueOf(stack.getItem().getRegistryName()));
			crashreportcategory.setDetail("Item Damage", () -> {
				return String.valueOf(stack.getDamageValue());
			});
			crashreportcategory.setDetail("Item NBT", () -> {
				return String.valueOf(stack.getTag());
			});
			crashreportcategory.setDetail("Item Foil", () -> {
				return String.valueOf(stack.hasFoil());
			});
			throw new ReportedException(crashreport);
		}
		this.blitOffset = model.isGui3d() ? this.blitOffset - 50.0F - seed : this.blitOffset - 50.0F;
		ci.cancel();
	}

	private static boolean checkOverrideContains(final ItemOverrides overrides, final ResourceLocation predicate) {
		return Arrays.asList(overrides.properties).contains(predicate);
	}

}
