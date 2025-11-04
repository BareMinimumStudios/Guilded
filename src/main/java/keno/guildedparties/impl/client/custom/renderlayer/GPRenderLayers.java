package keno.guildedparties.impl.client.custom.renderlayer;

import keno.guildedparties.impl.client.custom.shader.GPShaders;
import keno.guildedparties.impl.client.utils.RenderingHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;

// allat just for funni synthwave lines ;-;
@Environment(EnvType.CLIENT)
public class GPRenderLayers {
    public static final RenderLayer.MultiPhase GUI_SYNTH_WAVE = positionColorLayer("guildedparties:gui_synth_wave",
            DrawMode.QUADS,
            786432,
            false,
            MultiPhaseParameters.builder()
                    .program(RenderingHelper.getBackingProgram(GPShaders.SYNTH_GRID))
                    .depthTest(RenderPhase.LEQUAL_DEPTH_TEST)
                    .cull(RenderPhase.DISABLE_CULLING)
                    .build(false));

    public static RenderLayer.MultiPhase positionColorLayer(String name, DrawMode mode, int expectedBufferSize,
                                                            boolean translucent, MultiPhaseParameters parameters) {
        return RenderLayer.of(name, VertexFormats.POSITION_COLOR, mode, expectedBufferSize, false, translucent, parameters);
    }

    public static void init() {

    }
}
