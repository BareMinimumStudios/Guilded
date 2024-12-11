package keno.guildedparties.client.screens.own_guild.management.rank;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.DiscreteSliderComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.guilds.Rank;
import keno.guildedparties.networking.GPNetworking;
import keno.guildedparties.networking.packets.serverbound.AddRankPacket;
import net.minecraft.text.Text;

import java.util.List;

public class RankAdditionMenu extends BaseUIModelScreen<FlowLayout> {
    private final String guildName;
    private final List<Rank> ranks;

    private String rankName = "";
    private double rankPriority = 20;

    public RankAdditionMenu(String guildName, List<Rank> ranks) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("rank_addition_ui")));
        this.guildName = guildName;
        this.ranks = ranks;
    }

    @Override
    protected void build(FlowLayout flowLayout) {
        flowLayout.childById(TextBoxComponent.class, "rank-name")
                .onChanged().subscribe(text -> this.rankName = text);

        flowLayout.childById(DiscreteSliderComponent.class, "rank-priority")
                .setFromDiscreteValue(rankPriority)
                .onChanged().subscribe(value -> this.rankPriority = value);

        flowLayout.childById(ButtonComponent.class, "confirm-button")
                .onPress(button -> {
                    boolean canAddRank = !this.rankName.isEmpty() && !this.rankName.isBlank();

                    for (Rank rank : this.ranks) {
                        if (rank.name().strip().compareToIgnoreCase(this.rankName.strip()) == 0) {
                            canAddRank = false;
                            this.client.player.sendMessage(Text.translatable("guildedparties.rank_cannot_be_add"), true);
                            break;
                        }
                    }

                    if (!canAddRank) {
                        return;
                    }

                    GPNetworking.GP_CHANNEL.clientHandle().send(new AddRankPacket(guildName, rankName, (int) rankPriority));
                    this.client.setScreen(null);
                });
    }
}
