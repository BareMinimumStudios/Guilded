package keno.guildedparties.utils;

import net.minecraft.util.Identifier;

public class IdUtils {
    /** Takes the id for a file, and reduces it to it's name
     * @param id Identifier to convert
     * @param fileType The type of file the id references
     * @return the file name referenced, minus file extension*/
    public static String reduceIdToFilename(Identifier id, FileType fileType) {
        String name = id.getPath();
        String[] pathSeparated = name.split("/");
        name = pathSeparated[pathSeparated.length - 1];
        switch (fileType) {
            case PNG -> name = name.replaceAll(".png", "");
            case JSON -> name = name.replaceAll(".json", "");
        }
        return name;
    }

    public enum FileType {
        JSON,
        PNG
    }
}
