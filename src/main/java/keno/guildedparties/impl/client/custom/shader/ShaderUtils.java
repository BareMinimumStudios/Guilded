package keno.guildedparties.impl.client.custom.shader;

import io.wispforest.owo.ui.core.ParentComponent;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;

public class ShaderUtils {
    public static void blitWholeFrameBuffer(Framebuffer buffer, int mask, int filter) {
        blitWholeFrameBuffer(buffer, buffer, mask, filter);
    }

    public static void blitWholeFrameBuffer(Framebuffer input, Framebuffer output, int mask, int filter) {
        GL30.glBlitFramebuffer(0, 0, input.textureWidth, input.textureHeight,
                0, 0, output.textureWidth, output.textureHeight, mask, filter);
    }

    public static void drawQuadOverComponent(BufferBuilder buffer, Matrix4f matrix, ParentComponent component, int xPadding, int yPadding) {
        int finalX1 = component.x() + xPadding;
        int finalY1 = component.y() + yPadding;
        int finalX2 = (component.x() + component.width()) - xPadding;
        int finalY2 = (component.y() + component.height()) - yPadding;

        buffer.vertex(matrix, finalX1, finalY1, 0.1f).color(1f, 0f, 1f, 1f);
        buffer.vertex(matrix, finalX1, finalY2, 0.1f).color(1f, 0f, 1f, 1f);
        buffer.vertex(matrix, finalX2, finalY2, 0.1f).color(1f, 0f, 1f, 1f);
        buffer.vertex(matrix, finalX2, finalY1, 0.1f).color(1f, 0f, 1f, 1f);
    }

    public static void drawQuadOverComponent(VertexConsumer consumer, Matrix4f matrix, ParentComponent component, int xPadding, int yPadding) {
        int finalX1 = component.x() + xPadding;
        int finalY1 = component.y() + yPadding;
        int finalX2 = (component.x() + component.width()) - xPadding;
        int finalY2 = (component.y() + component.height()) - yPadding;

        consumer.vertex(matrix, finalX1, finalY1, 0.1f).color(1f, 0f, 1f, 1f);
        consumer.vertex(matrix, finalX1, finalY2, 0.1f).color(1f, 0f, 1f, 1f);
        consumer.vertex(matrix, finalX2, finalY2, 0.1f).color(1f, 0f, 1f, 1f);
        consumer.vertex(matrix, finalX2, finalY1, 0.1f).color(1f, 0f, 1f, 1f);
    }
}
