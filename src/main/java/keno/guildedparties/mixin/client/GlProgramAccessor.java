package keno.guildedparties.mixin.client;

import io.wispforest.owo.shader.GlProgram;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.ShaderProgram;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(GlProgram.class)
public interface GlProgramAccessor {
    @Accessor
    ShaderProgram getBackingProgram();
}
