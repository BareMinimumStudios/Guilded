package keno.guildedparties.impl.client.screens.abstract_screen;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.ParentComponent;

/***
 * We often want to handle element loading once, to prevent duplicate elements.
 * This class provides a handling for that towards xml Screens; if you want to
 * have this handling for code-defined, see {@link AbstractSingleLoadCodeScreen}
 */
public abstract class AbstractSingleLoadXMLScreen<R extends ParentComponent> extends BaseUIModelScreen<R> {
    private boolean elementsLoaded = false;

    protected AbstractSingleLoadXMLScreen(Class<R> rootComponentClass, DataSource source) {
        super(rootComponentClass, source);
    }

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
