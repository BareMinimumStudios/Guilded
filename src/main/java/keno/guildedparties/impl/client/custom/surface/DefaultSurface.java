package keno.guildedparties.impl.client.custom.surface;

import io.wispforest.owo.ui.core.Surface;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.impl.client.custom.renderlayer.GPRenderLayers;
import keno.guildedparties.impl.client.custom.shader.GPShaders;
import keno.guildedparties.impl.client.custom.shader.ShaderUtils;
import keno.guildedparties.impl.client.utils.RenderingHelper;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;

public enum DefaultSurface {
    CLASSIC(Surface.PANEL),
    CLASSIC_DARK(Surface.DARK_PANEL),
    // Most unexpectedly good UI-style I have ever found
    AZURE(Surface.flat(0x808080FF).and(Surface.outline(0x0000F1FF))),
    // I would like to give this UI-style a cool grid animation, but I need to learn shaders first
    SYNTH_WAVE(handleSynthGrid());

    private final Surface surface;

    DefaultSurface(Surface surface) {
        this.surface = surface;
    }

    public Surface getSurface() {
        return surface;
    }

    private static Surface handleSynthGrid() {
        return (context, component) -> {
            GPSurfaces.createCustomSurface(GuildedParties.GPLoc("synth_wave")).draw(context, component);

            Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
            // For some reason, 1.21.1 OwoUI doesn't let you retrieve vertex consumers; use accessor instead
            VertexConsumer buffer = RenderingHelper.getBufferFromContext(context, GPRenderLayers.GUI_SYNTH_WAVE);

            ShaderUtils.drawQuadOverComponent(buffer, matrix, component, 0, 0);
            GPShaders.SYNTH_GRID.use();
        };
    }
}
