package keno.guildedparties.utils;

import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import net.minecraft.util.Identifier;

public class Utils {
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

    public static <T extends Record> StructEndec<T> convertToStruct(Endec<T> endec) {
        return (StructEndec<T>) endec;
    }

    public enum FileType {
        JSON,
        PNG
    }
}
