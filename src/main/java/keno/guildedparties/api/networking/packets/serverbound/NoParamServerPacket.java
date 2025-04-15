package keno.guildedparties.api.networking.packets.serverbound;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import keno.guildedparties.api.networking.packets.ProcessType;

public record NoParamServerPacket(String type, int flag) {
    public static StructEndec<NoParamServerPacket> ENDEC = StructEndecBuilder.of(
        StructEndec.STRING.optionalFieldOf("type", NoParamServerPacket::type, "none"),
        StructEndec.INT.fieldOf("flag", NoParamServerPacket::flag),
    NoParamServerPacket::new);

    // for guild-related processes
    public static final int GET_GUILD_INFO = 0;
    public static final int DOES_PLAYER_HAVE_GUILD = 1;
    public static final int GET_INVITABLE_PLAYERS = 2;
    public static final int GET_OWN_GUILD = 3;
    public static final int QUICK_JOIN = 4;


    public NoParamServerPacket(ProcessType type, int flag) {
        this(type.asString(), flag);
    }

    public NoParamServerPacket(int flag) {
        this(ProcessType.NONE, flag);
    }
}
