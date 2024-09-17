package keno.net.guilded_parties.utils;

import net.minecraft.util.Identifier;

public class IdUtils {
    public static Identifier modLoc(String location) {
        return Identifier.of("guilded_parties", location);
    }

    public static Identifier mcLoc(String location) {
        return Identifier.of("minecraft", location);
    }
}
