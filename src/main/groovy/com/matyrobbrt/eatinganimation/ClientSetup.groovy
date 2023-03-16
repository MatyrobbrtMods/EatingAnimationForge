/*
 * Copyright (c) Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.eatinganimation

import com.google.common.collect.Lists
import com.matyrobbrt.eatinganimation.pack.ModCompatResourcePack
import com.matyrobbrt.gml.bus.EventBusSubscriber
import com.matyrobbrt.gml.bus.type.ModBus
import groovy.transform.CompileStatic
import net.minecraft.SharedConstants
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.player.RemotePlayer
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.client.renderer.item.ItemPropertyFunction
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackResources
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.metadata.pack.PackMetadataSection
import net.minecraft.server.packs.repository.Pack
import net.minecraft.server.packs.repository.PackSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.DataPackConfig
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.AddPackFindersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import net.minecraftforge.resource.DelegatingPackResources

import java.nio.file.Path
import java.util.function.Function

@CompileStatic
@SuppressWarnings('deprecation')
@EventBusSubscriber(value = ModBus, dist = [Dist.CLIENT])
class ClientSetup {

    @SubscribeEvent
    static void onClientSetup(final FMLClientSetupEvent event) {
        ItemProperties.registerGeneric(new ResourceLocation(EatingAnimation.MOD_ID, "eat"), EAT_PROPERTY);
        ItemProperties.registerGeneric(new ResourceLocation(EatingAnimation.MOD_ID, "eating"), EATING_PROPERTY);

        ItemProperties.registerGeneric(new ResourceLocation(EatingAnimation.MOD_ID, "drink"), DRINK_PROPERTY);
        ItemProperties.registerGeneric(new ResourceLocation(EatingAnimation.MOD_ID, "drinking"), DRINKING_PROPERTY);
    }

    @SubscribeEvent
    static void onLoadComplete(final FMLLoadCompleteEvent event) {
        if (!EatingAnimation.wasInstalledBefore) {
            final var listBefore = Lists.newArrayList(Minecraft.instance.resourcePackRepository.selectedIds)
            listBefore.remove('mod:' + EatingAnimation.MOD_ID)
            // And now add us back, but at the top
            listBefore.add('mod:' + EatingAnimation.MOD_ID)
            Minecraft.instance.resourcePackRepository.selected = listBefore
        }
    }

    @SubscribeEvent
    static void onPackFinders(final AddPackFindersEvent event) {
        if (event.packType !== PackType.CLIENT_RESOURCES) return

        final Function<String, Path> fileGetter = (String name) -> ModList.get().getModFileById(EatingAnimation.MOD_ID).getFile()
                .findResource('compat', name)
        event.addRepositorySource((source) -> {
            final List<PackResources> packs = new ArrayList<>();
            for (final mod : EatingAnimation.compatibleMods) {
                if (ModList.get().isLoaded(mod)) {
                    final var packName = "eatinganimations:compat/" + mod
                    packs.add(new ModCompatResourcePack(fileGetter.apply(mod), mod))
                    DataPackConfig.DEFAULT.addModPacks([packName])
                }
            }

            final var rpVersion = SharedConstants.currentVersion.getPackVersion(PackType.CLIENT_RESOURCES);
            final var dpVersion = SharedConstants.currentVersion.getPackVersion(PackType.SERVER_DATA);

            final var fullPack = Pack.create('eatinganimations_compat', Component.literal("Eating Animations Compat"),
                    false,
                    {new DelegatingPackResources('eatinganimations_compat', true,
                            new PackMetadataSection(Component.translatable('eatinganimations.resources.compat'),
                                    rpVersion, Map.of(PackType.CLIENT_RESOURCES, rpVersion, PackType.SERVER_DATA, dpVersion)),
                            packs)},
                    new Pack.Info(
                            Component.translatable('eatinganimations.resources.compat'), dpVersion, rpVersion, FeatureFlags.DEFAULT_FLAGS, false
                    ),
                    PackType.CLIENT_RESOURCES, Pack.Position.TOP, false, PackSource.DEFAULT);
            source.accept(fullPack)
        });
    }

    public static final ItemPropertyFunction EAT_PROPERTY = { ItemStack stack, ClientLevel world, LivingEntity entity, int i ->
        if (entity === null)
            return 0.0F
        if (entity instanceof RemotePlayer && entity.getTicksUsingItem() > 31) {
            return (float)(EatingAnimation.animationTicks / 30)
        }
        return (float)(entity.getUseItem() !== stack ? 0.0F
                : (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 30.0F)
    };

    private static final ItemPropertyFunction EATING_PROPERTY = { ItemStack stack, ClientLevel world, LivingEntity entity, int i ->
        if (entity === null)
            return 0.0F
        return (float)(entity.isUsingItem() && entity.getUseItem() === stack && stack.getItem().isEdible() ? 1 : 0)
    };

    public static final ItemPropertyFunction DRINK_PROPERTY = { ItemStack itemStack, ClientLevel world, LivingEntity livingEntity, int i ->
        if (livingEntity === null)
            return 0.0F;

        return (float)(livingEntity.getUseItem() !== itemStack ? 0.0F
                : (itemStack.getUseDuration() - livingEntity.getUseItemRemainingTicks()) / 30.0F)
    };

    private static final ItemPropertyFunction DRINKING_PROPERTY = { ItemStack itemStack, ClientLevel world, LivingEntity livingEntity, int i ->
        if (livingEntity === null)
            return 0.0F

        return (float)(livingEntity.isUsingItem() && livingEntity.getUseItem() === itemStack
                && itemStack.getItem().getUseAnimation(itemStack) === UseAnim.DRINK ? 1.0F : 0.0F)
    };

}
