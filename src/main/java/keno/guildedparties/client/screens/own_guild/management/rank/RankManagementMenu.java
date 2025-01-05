package keno.guildedparties.client.screens.own_guild.management.rank;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.DiscreteSliderComponent;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Insets;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.guilds.Rank;
import keno.guildedparties.networking.GPNetworking;
import keno.guildedparties.networking.packets.clientbound.OwnGuildMenuPacket;
import keno.guildedparties.networking.packets.serverbound.AddRankPacket;
import keno.guildedparties.networking.packets.serverbound.GetOwnGuildPacket;
import keno.guildedparties.networking.packets.serverbound.ModifyRankPacket;
import keno.guildedparties.networking.packets.serverbound.RemoveRankPacket;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

public class RankManagementMenu extends BaseUIModelScreen<FlowLayout> {
    private final String guildName;
    private final List<Rank> ranks;

    private String newRankName = textBoxDefault();
    private int newRankPriority = 25;

    private Rank selectedRank = null;

    private boolean elementsLoaded = false;

    public RankManagementMenu(String guildName, List<Rank> ranks) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("rank_management_ui")));
        this.guildName = guildName;
        this.ranks = ranks;
    }

    @Override
    protected void build(FlowLayout flowLayout) {
        flowLayout.childById(TextBoxComponent.class, "rank-name")
                .text(textBoxDefault()).onChanged().subscribe(value -> this.newRankName = value);

        flowLayout.childById(DiscreteSliderComponent.class, "rank-priority")
                .setFromDiscreteValue(this.newRankPriority)
                .onChanged().subscribe(value -> this.newRankPriority = (int) value);

        flowLayout.childById(ButtonComponent.class, "back")
                .onPress(button -> GPNetworking.GP_CHANNEL.clientHandle().send(new GetOwnGuildPacket()));

        flowLayout.childById(ButtonComponent.class, "add-rank")
                .onPress(this::addRank);

        flowLayout.childById(ButtonComponent.class, "remove-rank")
                .onPress(button -> removeRank(flowLayout));

        flowLayout.childById(ButtonComponent.class, "modify-rank")
                .onPress(button -> modifyRank(flowLayout));
    }

    @Override
    protected void init() {
        super.init();

        if (this.uiAdapter == null) return;

        if (!this.elementsLoaded) {
            if (!this.ranks.isEmpty()) {
                int ranksSize = ranks.size();
                int counter = 0;

                for (Rank rank : ranks) {
                    counter++;
                    if (!rank.isCoLeader() && !rank.name().equals("Recruit")) {
                        if (++counter < ranksSize)
                            this.uiAdapter.rootComponent.childById(FlowLayout.class, "ranks")
                                    .child(getRankElement(rank).margins(Insets.bottom(5)));
                        else {
                            this.uiAdapter.rootComponent.childById(FlowLayout.class, "ranks")
                                    .child(getRankElement(rank));
                        }
                    }
                }
            }

            this.elementsLoaded = true;
        }
    }

    public String textBoxDefault() {
        if (I18n.hasTranslation("gui.guildedparties.rank_name")) {
            return I18n.translate("gui.guildedparties.rank_name");
        }
        return "Rank name";
    }

    public Component getRankElement(Rank rank) {
        FlowLayout layout = this.model.expandTemplate(FlowLayout.class,
                "rank",
                Map.of("rank-name", rank.name()));

        layout.childById(LabelComponent.class, "rank-priority")
                .text(Text.translatable("gui.guildedparties.rank_priority", rank.priority()));

        layout.childById(ButtonComponent.class, "select")
                .onPress(button -> {
                    this.uiAdapter.rootComponent.forEachDescendant(component -> {
                        if (component instanceof ButtonComponent buttonComponent) {
                            buttonComponent.active(true);
                        }
                    });

                    this.uiAdapter.rootComponent.childById(TextBoxComponent.class, "rank-name")
                                    .text(rank.name());

                    this.uiAdapter.rootComponent.childById(DiscreteSliderComponent.class, "rank-priority")
                                    .setFromDiscreteValue(rank.priority());

                    button.active(false);

                    this.selectedRank = rank;
                });

        return layout.id(rank.name());
    }

    public void addRank(ButtonComponent button) {
        Rank rankToAdd = new Rank(this.newRankName, this.newRankPriority);

        if ((rankToAdd.name().equals("Recruit") || rankToAdd.isCoLeader()) && ranks.contains(rankToAdd)) {
            this.client.inGameHud.getChatHud().addMessage(Text.translatable("guildedparties.rank_cannot_be_add"));
            return;
        }

        ranks.add(rankToAdd);
        GPNetworking.GP_CHANNEL.clientHandle().send(new AddRankPacket(this.guildName, rankToAdd));
    }

    public void removeRank(FlowLayout layout) {
        Rank rankToRemove = this.selectedRank;

        if ((rankToRemove.name().equals("Recruit") || rankToRemove.isCoLeader()) && !ranks.contains(rankToRemove)) {
            this.client.inGameHud.getChatHud().addMessage(Text.translatable("guildedparties.cannot_remove_rank", rankToRemove.name()));
            return;
        }

        GPNetworking.GP_CHANNEL.clientHandle().send(new RemoveRankPacket(this.guildName, rankToRemove));
        ranks.remove(rankToRemove);
        layout.childById(FlowLayout.class, "ranks")
                .removeChild(layout.childById(FlowLayout.class, "ranks").childById(FlowLayout.class, rankToRemove.name()));

        resetButtons(layout);
    }

    public void modifyRank(FlowLayout layout) {
        Rank oldRank = this.selectedRank;
        Rank newRank = new Rank(this.newRankName, this.newRankPriority);

        if (((oldRank.name().equals("Recruit") || newRank.isCoLeader()) && !ranks.contains(oldRank))
            && (newRank.name().equals("Recruit") || newRank.isCoLeader()) && ranks.contains(newRank)) {
            this.client.inGameHud.getChatHud().addMessage(Text.translatable("guildedparties.cannot_modify", oldRank.name()));
            return;
        }

        GPNetworking.GP_CHANNEL.clientHandle().send(new ModifyRankPacket(this.guildName, oldRank, newRank));
        ranks.remove(oldRank);
        ranks.add(newRank);

        layout.childById(FlowLayout.class, "ranks")
                .removeChild(layout.childById(FlowLayout.class, "ranks").childById(FlowLayout.class, oldRank.name()));

        this.selectedRank = null;

        resetButtons(layout);
    }

    public void resetButtons(FlowLayout layout) {
        layout.childById(ButtonComponent.class, "remove-rank")
                .active(false);
        layout.childById(ButtonComponent.class, "modify-rank")
                .active(false);
    }
}
