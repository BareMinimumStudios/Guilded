package keno.guildedparties.client.screens;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.guilds.Rank;
import net.minecraft.text.Text;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class RankSelectionScreen<R extends Record> extends BaseUIModelScreen<FlowLayout> {
    private final List<Rank> ranks;
    private TriFunction<Rank, String, String, R> packet;
    private String actionName;
    private String guildName;
    private String username = "";

    private Rank selectedRank = null;

    private boolean elementsLoaded = false;
    private final FlowLayout ranksContainer = Containers.verticalFlow(Sizing.fill(100), Sizing.content());

    private final ButtonComponent button = Components.button(Text.translatable("gui.guildedparties.confirm"),
            button -> this.client.setScreen(new ActionConfirmScreen<>(actionName,
                    this.packet.apply(selectedRank, guildName, username)))).active(false);

    public RankSelectionScreen(List<Rank> ranks,
                               String guildName,
                               String actionName,
                               TriFunction<Rank, String, String, R> packetToSend) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("rank_selection_ui")));
        this.ranks = ranks;
        this.packet = packetToSend;
        this.actionName = actionName;
        this.guildName = guildName;
    }

    public RankSelectionScreen(List<Rank> ranks,
                               String guildName,
                               String actionName,
                               String username,
                               TriFunction<Rank, String, String, R> packetToSend) {
        this(ranks, guildName, actionName, packetToSend);
        this.username = username;
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout flowLayout) {
        flowLayout.surface(Surface.VANILLA_TRANSLUCENT)
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .padding(Insets.of(5))
                .sizing(Sizing.fill(100));

        flowLayout.child(Containers.verticalScroll(Sizing.fill(50), Sizing.fill(100), this.ranksContainer)
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .positioning(Positioning.relative(50, 0)));

        flowLayout.child(this.button.sizing(Sizing.fill(20), Sizing.content(10))
                .positioning(Positioning.relative(90, 100)));
    }

    @Override
    protected void init() {
        super.init();

        if (!this.elementsLoaded) {
            int i = 0;
            for (Rank rank : ranks) {
                if (!rank.name().equals("Recruit")) {
                    this.ranksContainer.child(getRankSelectionElement(rank, i++));
                }
            }

            this.elementsLoaded = true;
        }
    }

    private FlowLayout getRankSelectionElement(Rank rank, int iteration) {
        FlowLayout element = this.model.expandTemplate(FlowLayout.class, "rank-element",
                Map.of("name", rank.name(), "priority", String.valueOf(rank.priority())));

        element.childById(ButtonComponent.class, "select-rank").onPress(selectButton -> {
            this.selectedRank = rank;
            this.ranksContainer.forEachDescendant(component -> {
                if (component instanceof FlowLayout flowLayout) {
                    flowLayout.forEachDescendant(component1 -> {
                        if (component1 instanceof ButtonComponent inactiveButton) {
                            if (!inactiveButton.active() && !inactiveButton.id().equals(this.button.id())) {
                                inactiveButton.active(true);
                            }
                        }
                    });
                }
            });
            selectButton.active(false);
            this.button.active(true);
        });

        if (iteration == 0) {
            element.margins(Insets.bottom(2));
        } else if (iteration == this.ranks.size() - 1) {
            element.margins(Insets.top(2));
        } else {
            element.margins(Insets.vertical(2));
        }

        return element;
    }
}
