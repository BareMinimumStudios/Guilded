package keno.guildedparties.networking.packets.serverbound;

import keno.guildedparties.networking.GPNetworking;

/** Checks if the player is in a guild before loading the guilded main menu
 * @see GPNetworking#init()  GPNetworking*/
public record DoesPlayerHaveGuildPacket() {}
