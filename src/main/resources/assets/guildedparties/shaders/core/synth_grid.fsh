#version 150

#moj_import "geometry.glsl"

uniform sampler2D InputSampler;
uniform vec2 InputResolution;
uniform vec4 ColorModulator;
uniform int WorldTime;

out vec4 fragColor;

void main() {
    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = gl_FragCoord.xy / InputResolution.xy;
    // Pixel colour
    vec4 col = texture(InputSampler, uv);

    if (col.a == 0.) discard;

    vec3 rgb = vec3(col.rgb);

    float res = sdSquare(uv, 0.5, vec2(0.2));

    res = step(0., res);

    rgb = mix(vec3(1.,0.,0.), rgb, res);

    col.rgb += rgb;

    fragColor = col * ColorModulator;
}