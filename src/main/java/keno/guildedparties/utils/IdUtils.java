package keno.guildedparties.utils;

import net.minecraft.util.Identifier;

public class IdUtils {
    /** Takes the id for a json file, and reduces it to it's name
     * @param id Identifier to convert
     * @return the file name referenced, minus .json*/
    public static String reduceIdToFilename(Identifier id) {
        String name = id.getPath();
        String[] pathSeparated = name.split("/");
        name = pathSeparated[pathSeparated.length - 1];
        return name.replaceAll(".json", "");
    }
}
