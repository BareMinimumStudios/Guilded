package keno.guildedparties.api.networking.packets.serverbound;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.api.networking.packets.ProcessType;

public record StrParamServerPacket(String str, String type, int flag) {
    public static StructEndec<StrParamServerPacket> ENDEC = StructEndecBuilder.of(
            StructEndec.STRING.fieldOf("str", StrParamServerPacket::str),
            StructEndec.STRING.optionalFieldOf("type", StrParamServerPacket::type, "none"),
            StructEndec.INT.fieldOf("flag", StrParamServerPacket::flag),
            StrParamServerPacket::new);

    // For guild-related processes
    public static final int DISBAND_GUILD = 0;
    public static final int GET_GUILD_SETTINGS = 1;
    public static final int INVITE_PLAYER = 2;
    public static final int JOIN_GUILD = 3;
    public static final int LEAVE_GUILD = 4;

    public StrParamServerPacket(String str, ProcessType type, int flag) {
        this(str, type.asString(), flag);
    }

    public StrParamServerPacket(String str, int flag) {
        this(str, ProcessType.NONE, flag);
    }
}
