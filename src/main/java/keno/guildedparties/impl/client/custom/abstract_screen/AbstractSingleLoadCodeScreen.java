package keno.guildedparties.impl.client.custom.abstract_screen;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.ParentComponent;

public abstract class AbstractSingleLoadCodeScreen<R extends ParentComponent> extends BaseOwoScreen<R> {
    private boolean elementsLoaded = false;

    @Override
    protected void init() {
        super.init();

        if (this.uiAdapter == null) return;

        beforeElementLoad(this.uiAdapter);
        if (!elementsLoaded) loadElements(this.uiAdapter);
        elementsLoaded = true;
        afterElementLoad(this.uiAdapter);
    }

    protected abstract void beforeElementLoad(OwoUIAdapter<R> uiAdapter);

    protected abstract void loadElements(OwoUIAdapter<R> uiAdapter);

    protected abstract void afterElementLoad(OwoUIAdapter<R> uiAdapter);
}
