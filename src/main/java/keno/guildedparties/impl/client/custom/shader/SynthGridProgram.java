package keno.guildedparties.impl.client.custom.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.shader.BlurProgram;
import io.wispforest.owo.shader.GlProgram;
import io.wispforest.owo.ui.event.WindowResizeCallback;
import keno.guildedparties.GuildedParties;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.VertexFormats;
import org.lwjgl.opengl.GL30;

public class SynthGridProgram extends GlProgram {
    private GlUniform inputResolution;
    private Framebuffer input;

    public SynthGridProgram() {
        super(GuildedParties.GPLoc("synth_grid"), VertexFormats.POSITION_COLOR);

        WindowResizeCallback.EVENT.register((client, window) -> {
            if (this.input == null) return;
            //? if >=1.21.3 {
            this.input.resize(window.getFramebufferWidth(), window.getFramebufferHeight());
            //?}

            //? if < 1.21.3 {
            /*this.input.resize(window.getFramebufferWidth(), window.getFramebufferHeight(), MinecraftClient.IS_SYSTEM_MAC);
            *///?}
        });
    }

    @Override
    public void use() {
        var buffer = MinecraftClient.getInstance().getFramebuffer();

        this.input.beginWrite(false);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, buffer.fbo);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, input.fbo);
        ShaderUtils.blitWholeFrameBuffer(input, GL30.GL_COLOR_BUFFER_BIT, GL30.GL_LINEAR);
        buffer.beginWrite(false);

        this.inputResolution.set((float) buffer.textureWidth, (float) buffer.textureHeight);
        //? if >= 1.21.3 {
        this.backingProgram.addSamplerTexture("InputSampler", this.input.getColorAttachment());
        //?}

        //? if < 1.21.3 {
         /*this.backingProgram.addSampler("InputSampler", this.input.getColorAttachment());
        *///?}
        super.use();
    }

    @Override
    protected void setup() {
        this.inputResolution = this.findUniform("InputResolution");

        var window = MinecraftClient.getInstance().getWindow();
        //? if >= 1.21.3 {
        this.input = new SimpleFramebuffer(window.getFramebufferWidth(), window.getFramebufferHeight(), false);
        //?}

        //? if < 1.21.3 {
        /*this.input = new SimpleFramebuffer(window.getFramebufferWidth(), window.getFramebufferHeight(), false, MinecraftClient.IS_SYSTEM_MAC);
        *///?}
    }
}
