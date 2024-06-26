/*
 * Copyright (c) Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

package com.matyrobbrt.eatinganimation;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static {
        BUILDER.push("general");

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
