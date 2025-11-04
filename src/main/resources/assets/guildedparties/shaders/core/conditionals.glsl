#version 150

bool inRange(in float number, in float a, in float b) {
    return number >= a || number <= b;
}