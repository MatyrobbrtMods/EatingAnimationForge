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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.google.common.collect.Lists;

import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.level.DataPackConfig;

import io.github.matyrobbrt.eatinganimation.pack.ModCompatResourcePack;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.resource.DelegatingResourcePack;

@SuppressWarnings("deprecation")
public class ClientSetup {

    public ClientSetup(final IEventBus modBus) {
        modBus.addListener(this::onClientSetup);
        modBus.addListener(this::onPackFinders);
        modBus.addListener(this::onLoadComplete);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        ItemProperties.registerGeneric(new ResourceLocation(EatingAnimation.MOD_ID, "eat"), EAT_PROPERTY);
        ItemProperties.registerGeneric(new ResourceLocation(EatingAnimation.MOD_ID, "eating"), EATING_PROPERTY);
    }

    private void onLoadComplete(final FMLLoadCompleteEvent event) {
        if (!EatingAnimation.wasInstalledBefore) {
            final var listBefore = Lists
                    .newArrayList(Minecraft.getInstance().getResourcePackRepository().getSelectedIds());
            if (listBefore.contains("mod:" + EatingAnimation.MOD_ID))
                listBefore.remove("mod:" + EatingAnimation.MOD_ID);
            // And now add us back, but at the top
            listBefore.add("mod:" + EatingAnimation.MOD_ID);
            Minecraft.getInstance().getResourcePackRepository().setSelected(listBefore);
        }
    }

    private void onPackFinders(final AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES)
            return;
        final Function<String, Path> fileGetter = name -> ModList.get().getModFileById(EatingAnimation.MOD_ID).getFile()
                .findResource("compat", name);
        event.addRepositorySource((source, factory) -> {
            final List<PackResources> packs = new ArrayList<>();
            for (final var mod : EatingAnimation.compatibleMods) {
                if (ModList.get().isLoaded(mod)) {
                    final var packName = "eatinganimations:compat/" + mod;
                    packs.add(new ModCompatResourcePack(fileGetter.apply(mod), mod));
                    DataPackConfig.DEFAULT.addModPacks(List.of(packName));
                }
            }
            final var fullPack = Pack.create("eatinganimations_compat", false,
                    () -> new DelegatingResourcePack("eatinganimations_compat", "EatingAnimations Compat",
                            new PackMetadataSection(new TranslatableComponent("eatinganimations.resources.compat"),
                                    PackType.CLIENT_RESOURCES.getVersion(SharedConstants.getCurrentVersion())),
                            packs),
                    factory, Pack.Position.TOP, PackSource.DEFAULT);
            source.accept(fullPack);
        });
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
