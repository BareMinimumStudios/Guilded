package keno.guildedparties.client.screens;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.networking.GPNetworking;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ActionConfirmScreen<R extends Record> extends BaseUIModelScreen<FlowLayout> {
    private boolean elementsLoaded = false;

    private FlowLayout container = Containers.verticalFlow(Sizing.content(), Sizing.content());

    private final String actionName;
    private final R packetToSend;

    public ActionConfirmScreen(String actionName, R packetToSend) {
        super(FlowLayout.class, DataSource.asset(GuildedParties.GPLoc("action_confirm_ui")));
        this.actionName = actionName;
        this.packetToSend = packetToSend;
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout flowLayout) {
        flowLayout.surface(Surface.VANILLA_TRANSLUCENT)
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .sizing(Sizing.fill(100));
    }

    @Override
    protected void init() {
        super.init();

        if (this.uiAdapter == null) return;

        if (!this.elementsLoaded) {
            this.uiAdapter.rootComponent.child(container.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER));

            this.container.child(getActionConfirmElement());

            this.elementsLoaded = true;
        }
    }

    private FlowLayout getActionConfirmElement() {
        FlowLayout layout = this.model.expandTemplate(FlowLayout.class, "confirm-element@guildedparties:action_confirm_ui",
                Map.of("action", this.actionName));

        layout.childById(ButtonComponent.class, "confirm").onPress(button -> {
            GPNetworking.GP_CHANNEL.clientHandle().send(this.packetToSend);
            this.client.setScreen(null);
        });

        layout.childById(ButtonComponent.class, "cancel").onPress(button
                -> this.client.setScreen(null));

        return layout;
    }
}
