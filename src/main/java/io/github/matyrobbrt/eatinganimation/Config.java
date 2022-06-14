package io.github.matyrobbrt.eatinganimation;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;

public final class Config {

    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.Builder BUILDER = new Builder();

    static {
        BUILDER.push("general");

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
