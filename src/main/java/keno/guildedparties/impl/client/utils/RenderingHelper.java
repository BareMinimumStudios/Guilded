package keno.guildedparties.impl.client.utils;

import io.wispforest.owo.ui.core.OwoUIDrawContext;
import keno.guildedparties.mixin.client.DrawContextInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class RenderingHelper {
    /**
     * {@link DrawContext#fillGradient(int, int, int, int, int, int)} but sideways
     */
    public static void fillSidewaysGradient(DrawContext context, int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
        filLSidewaysGradient(context, ((DrawContextInvoker)context).getVertexConsumers().getBuffer(RenderLayer.getGui()), startX, startY, endX, endY, colorStart, colorEnd);
    }

    public static void filLSidewaysGradient(DrawContext context, VertexConsumer vertexConsumer, int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        vertexConsumer.vertex(matrix4f, (float)startX, (float)startY, 0f).color(colorStart);
        vertexConsumer.vertex(matrix4f, (float)startX, (float)endY, 0f).color(colorStart);
        vertexConsumer.vertex(matrix4f, (float)endX, (float)endY, 0f).color(colorEnd);
        vertexConsumer.vertex(matrix4f, (float)endX, (float)startY, 0f).color(colorEnd);
    }
}
