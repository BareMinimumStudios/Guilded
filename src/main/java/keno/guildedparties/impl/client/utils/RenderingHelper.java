package keno.guildedparties.impl.client.utils;

import io.wispforest.owo.shader.GlProgram;
import keno.guildedparties.mixin.client.DrawContextInvoker;
import keno.guildedparties.mixin.client.GlProgramAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.RenderPhase.ShaderProgram;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class RenderingHelper {
    /**
     * {@link DrawContext#fillGradient(int, int, int, int, int, int)} but sideways
     */
    public static void fillSidewaysGradient(DrawContext context, int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
        filLSidewaysGradient(context, startX, startY, endX, endY, colorStart, colorEnd);
    }

    public static void filLSidewaysGradient(DrawContext context, int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
        VertexConsumer vertexConsumer = getBufferFromContext(context, RenderLayer.getGui());
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        vertexConsumer.vertex(matrix4f, (float)startX, (float)startY, 0f).color(colorStart);
        vertexConsumer.vertex(matrix4f, (float)startX, (float)endY, 0f).color(colorStart);
        vertexConsumer.vertex(matrix4f, (float)endX, (float)endY, 0f).color(colorEnd);
        vertexConsumer.vertex(matrix4f, (float)endX, (float)startY, 0f).color(colorEnd);
    }

    public static VertexConsumer getBufferFromContext(DrawContext context, RenderLayer layer) {
        return ((DrawContextInvoker)context).getVertexConsumers().getBuffer(layer);
    }

    public static RenderPhase.ShaderProgram getBackingProgram(GlProgram program) {
        //? if >= 1.21.3 {
        return program.renderPhaseProgram();
        //?}

        //? if < 1.21.3 {
        /*return new ShaderProgram(() -> ((GlProgramAccessor)program).getBackingProgram());
        *///?}
    }
}
