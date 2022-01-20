package io.github.matyrobbrt.eatinganimation.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import io.github.matyrobbrt.eatinganimation.EatingAnimation;
import io.github.matyrobbrt.eatinganimation.EatingAnimation.Config;

@SuppressWarnings("deprecation")
@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin implements IResourceManagerReloadListener {

	@Final
	@Shadow
	private ItemModelMesher itemModelShaper;
	@Shadow
	public float blitOffset;

	@Shadow
	protected abstract IBakedModel getModel(ItemStack p_184393_1_, @Nullable World p_184393_2_,
			@Nullable LivingEntity p_184393_3_);

	@Shadow
	protected abstract void renderGuiItem(ItemStack pStack, int pX, int pY, IBakedModel pBakedmodel);

	private IBakedModel transformEatingModel(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
		final IBakedModel initialModel = this.itemModelShaper.getItemModel(stack);
		final ClientWorld clientWorld = world instanceof ClientWorld ? (ClientWorld) world : null;
		final IBakedModel initialOverridenModel = initialModel.getOverrides().resolve(initialModel, stack, clientWorld,
				entity);
		IBakedModel transformedModel = checkOverrideContains(initialModel.getOverrides(),
				new ResourceLocation(EatingAnimation.MOD_ID, "eat")) ? this.itemModelShaper.getItemModel(stack)
						: initialOverridenModel;
		return transformedModel == null ? itemModelShaper.getModelManager().getMissingModel() : transformedModel;
	}

	@Inject(at = @At("HEAD"), method = "Lnet/minecraft/client/renderer/ItemRenderer;tryRenderGuiItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;II)V", cancellable = true)
	private void eatinganimation$tryRenderGuiItem(@Nullable LivingEntity entity, ItemStack stack, int x, int y,
			CallbackInfo ci) {
		if (stack.isEmpty() || !stack.isEdible() || entity == null
				|| Boolean.TRUE.equals(Config.RENDER_INVENTORY_EATING.get())) {
			return;
		}
		if (entity.getUseItem() != stack) { return; }

		this.blitOffset += 50.0F;
		try {
			this.renderGuiItem(stack, x, y, transformEatingModel(stack, null, entity));
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
		this.blitOffset -= 50.0F;
		ci.cancel();
	}

	private static boolean checkOverrideContains(final ItemOverrideList overrides, final ResourceLocation predicate) {
		return overrides.getOverrides().stream().anyMatch(override -> override.predicates.containsKey(predicate));
	}

}
