/*
 * Copyright (c) Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.eatinganimation.pack;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.matyrobbrt.eatinganimation.EatingAnimation;
import com.matyrobbrt.eatinganimation.datagen.ItemModelGenerator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.FileUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.matyrobbrt.eatinganimation.EatingAnimation.MOD_ID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class ModCompatResourcePack extends AbstractPackResources {

    public static final Gson GSON = new Gson();

    public static final int NUMBER_OF_MODELS = 3;

    private final String namespace;
    private final Set<String> namespaces;
    private final Map<ResourceLocation, IoSupplier<InputStream>> resources = new HashMap<>();
    private final byte[] packMcMeta;

    public ModCompatResourcePack(Path path, String namespace) {
        super("eating_animations_compat_" + namespace, true);
        this.namespace = namespace;

        this.namespaces = Set.of(EatingAnimation.MOD_ID, namespace);
        final Set<String> itemNames = collectNames(path, namespace);

        for (final var name : itemNames) {
            final var propsFile = path.resolve(name).resolve("properties.json");
            final var props = Optional.ofNullable(Files.exists(propsFile) ? readProps(propsFile) : null).orElse(new Properties(null));
            final var itemModel = ItemModelGenerator.generateItemModel(name, namespace, props.resolveModel(namespace + ":item/" + name), props.values);
            resources.put(new ResourceLocation(namespace, "models/item/" + name + ".json"), () -> toIs(itemModel));
            for (int i = 0; i < NUMBER_OF_MODELS; i++) {
                final var fullPath = path.resolve(name).resolve(i + ".png");
                resources.put(new ResourceLocation(MOD_ID, "textures/item/" + namespace + "/" + name + "_" + i + ".png"),
                        () -> Files.newInputStream(fullPath));
                final var animation = ItemModelGenerator.generateAnimationModel(name, namespace, i);
                resources.put(new ResourceLocation(MOD_ID, "models/item/" + namespace + "/" + name + "_" + i + ".json"),
                        () -> toIs(animation));
            }
        }

        packMcMeta = "{\"pack\":{\"description\":\"EatingAnimations %s compatibility\",\"pack_format\":%s}}"
                .formatted(namespace, PackType.CLIENT_RESOURCES.getVersion(SharedConstants.getCurrentVersion()))
                .getBytes(StandardCharsets.UTF_8);
    }

    private static ByteArrayInputStream toIs(String str) {
        return new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
    }

    @Nullable
    private static Properties readProps(Path path) {
        try (final Reader reader = Files.newBufferedReader(path)) {
            final JsonObject json = GSON.fromJson(reader, JsonObject.class);
            return Properties.CODEC.decode(JsonOps.INSTANCE, json).getOrThrow(false, it -> {
            }).getFirst();
        } catch (Exception e) {
            EatingAnimation.LOGGER.error("Exception trying to read model properties file:", e);
            return null;
        }
    }

    private static Set<String> collectNames(Path path, String namespace) {
        final var names = new HashSet<String>();
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    final var name = dir.getFileName().toString();
                    if (!name.equals(namespace))
                        names.add(name);
                    return super.postVisitDirectory(dir, exc);
                }
            });
        } catch (IOException ignored) {
        }
        return names;
    }

    @Override
    public void listResources(PackType type, String namespace, String path, ResourceOutput resourceOutput) {
        if (type != PackType.CLIENT_RESOURCES || !namespace.equals(EatingAnimation.MOD_ID)
                || !namespace.equals(this.namespace))
            return;

        resources.entrySet().stream()
                .filter(it -> it.getKey().getNamespace().equals(namespace) && it.getKey().getNamespace().startsWith(path))
                .forEach(it -> resourceOutput.accept(it.getKey(), it.getValue()));
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        return type == PackType.CLIENT_RESOURCES ? namespaces : Set.of();
    }

    @Override
    public void close() {
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... paths) {
        if (paths.length == 1 && paths[0].equals("pack.mcmeta")) return () -> new ByteArrayInputStream(packMcMeta);
        return resources.get(new ResourceLocation(namespace, String.join("/", paths)));
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
        if (type != PackType.CLIENT_RESOURCES) return null;
        return resources.get(location);
    }

    public record Properties(@Nullable String defaultModel, List<Float> values) {
        public static final Codec<Properties> CODEC = RecordCodecBuilder.create(in -> in.group(
                Codec.STRING.fieldOf("layer0").forGetter(Properties::defaultModel),
                Codec.FLOAT.listOf().optionalFieldOf("values", List.of(0.35f, 0.70f, 0.90f)).forGetter(Properties::values)
        ).apply(in, Properties::new));

        public Properties(@Nullable String defaultModel) {
            this(defaultModel, List.of(0.35f, 0.70f, 0.90f));
        }

        public String resolveModel(String fallback) {
            return defaultModel == null ? fallback : defaultModel;
        }
    }
}
