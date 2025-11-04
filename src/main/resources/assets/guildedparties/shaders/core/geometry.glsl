#version 150

#moj_import "conditionals.glsl"

float opRep(in vec2 uv, in float size, in vec2 c) {
    vec2 q = mod(uv+0.5*c,c)-0.5*c;
    return sqSquare(q, size, vec2(0));
}

float sdSquare(in vec2 uv, in float size, in vec2 offset) {
    float x = uv.x - offset.x;
    float y = uv.y - offset.y;

    return max(abs(x), abs(y)) - size;
}

float sdBox(in vec2 p, in vec2 b, in vec2 offset) {
    vec2 d = abs(p)-b;
    d -= offset;
    return length(max(d,0.0)) + min(max(d.x,d.y),0.0);
}

bool createGridLine(in vec2 pos, in float lineStart, in float lineEnd) {
    if (inRange(pos.x, lineStart, lineEnd) || inRange(pos.y, lineStart, lineEnd)) {
        return true;
    }
    return false;
}

bool createGridLine(in vec4 pos, in float lineStart, in float lineEnd) {
    return createGridLine(pos.xy, lineStart, lineEnd);
}

bool createGridLine(in vec3 pos, in float lineStart, in float lineEnd) {
    return createGridLine(pos.xy, lineStart, lineEnd);
}