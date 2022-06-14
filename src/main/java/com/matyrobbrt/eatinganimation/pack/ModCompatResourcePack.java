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

package com.matyrobbrt.eatinganimation.pack;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.matyrobbrt.eatinganimation.EatingAnimation;
import com.matyrobbrt.eatinganimation.datagen.ItemModelGenerator;

import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;

import static com.matyrobbrt.eatinganimation.EatingAnimation.MOD_ID;

public final class ModCompatResourcePack extends AbstractPackResources {

    public static final Gson GSON = new Gson();

    public static final int NUMBER_OF_MODELS = 3;

    private final String namespace;
    private final Set<String> namespaces;
    private final Set<String> itemNames;
    private final Map<String, IOSupplier> resources = new HashMap<>();
    private final byte[] packMcMeta;

    public ModCompatResourcePack(Path path, String namespace) {
        super(new File("dummy"));
        this.namespace = namespace;

        this.namespaces = Set.of(EatingAnimation.MOD_ID, namespace);
        itemNames = collectNames(path, namespace);

        for (final var name : itemNames) {
            final var propsFile = path.resolve(name).resolve("properties.json");
            Properties props = null;
            if (Files.exists(propsFile))
                props = readProps(propsFile);
            if (props == null)
                props = new Properties();
            final var itemModel = ItemModelGenerator.generateItemModel(name, namespace,
                    props.resolveModel(namespace + ":item/" + name),
                    props.values);
            resources.put("assets/" + namespace + "/models/item/" + name + ".json", () -> toIs(itemModel));
            for (int i = 0; i < NUMBER_OF_MODELS; i++) {
                final var fullPath = path.resolve(name).resolve(i + ".png");
                resources.put("assets/" + MOD_ID + "/textures/item/" + namespace + "/" + name + "_" + i + ".png",
                        () -> Files.newInputStream(fullPath));
                final var animation = ItemModelGenerator.generateAnimationModel(name, namespace, i);
                resources.put("assets/" + MOD_ID + "/models/item/" + namespace + "/" + name + "_" + i + ".json",
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

    private static Properties readProps(Path path) {
        try (final var is = Files.newInputStream(path)) {
            final var str = org.apache.commons.io.IOUtils.toString(is, StandardCharsets.UTF_8);
            return GSON.fromJson(str, Properties.class);
        } catch (Exception e) {
            EatingAnimation.LOGGER.error("Exception trying to read model properties file: {}", e);
            return null;
        }
    }

    private static Set<String> collectNames(Path path, String namespace) {
        final var names = new HashSet<String>();
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    final var name = dir.getFileName().toString();
                    if (!name.equals(namespace))
                        names.add(name);
                    return super.postVisitDirectory(dir, exc);
                }
            });
        } catch (IOException e) {}
        return names;
    }

    @Override
    // TODO pathIn.. we should use it
    public Collection<ResourceLocation> getResources(PackType type, String resourceNamespace, String pathIn,
            Predicate<ResourceLocation> p_215342_) {
        if (type != PackType.CLIENT_RESOURCES || !resourceNamespace.equals(EatingAnimation.MOD_ID)
                || !resourceNamespace.equals(namespace))
            return Collections.emptyList();
        // So, we need to make sure it knows we hold dynamic textures and models
        // Textures -> eatinganimation:textures/item/namespace/item_name_index.png
        // Models -> eatinganimation:models/item/namespace/item_name_index.json
        // Replaced Models -> namespace:models/item/item_name.json

        return itemNames.stream().<ResourceLocation>flatMap(name -> {
            // Now we generate the locations
            final var locations = new HashSet<ResourceLocation>();
            if (resourceNamespace.equals(namespace))
                locations.add(new ResourceLocation(namespace, "models/item/" + name + ".json"));
            else {
                for (int i = 0; i < NUMBER_OF_MODELS; i++) {
                    locations.add(new ResourceLocation(resourceNamespace,
                            "textures/item/" + namespace + "/" + name + "_" + i + ".png"));
                    locations.add(new ResourceLocation(resourceNamespace,
                            "models/item/" + namespace + "/" + name + "_" + i + ".json"));
                }
            }
            return locations.stream();
        }).filter(p_215342_).toList();
    }

    @Override
    public Set<String> getNamespaces(PackType p_10283_) {
        return p_10283_ == PackType.CLIENT_RESOURCES ? namespaces : Set.of();
    }

    @Override
    public void close() {
    }

    @Override
    protected InputStream getResource(String p_10220_) throws IOException {
        if (p_10220_.equals("pack.mcmeta"))
            return new ByteArrayInputStream(packMcMeta);
        final var res = resources.get(p_10220_);
        return res == null ? null : res.get();
    }

    @Override
    protected boolean hasResource(String name) {
        return resources.containsKey(name);
    }

    @Override
    public String getName() {
        return "Eating Animations " + namespace + " compatibility";
    }

    @Override
    public String toString() {
        return getClass() + "/" + getName();
    }

    public final class Properties {

        @SerializedName("layer0")
        public String defaultModel;
        public float[] values = {
                0.35f, 0.70f, 0.90f
        };

        public String resolveModel(String fallback) {
            return defaultModel == null ? fallback : defaultModel;
        }
    }

    interface IOSupplier {

        InputStream get() throws IOException;
    }
}
