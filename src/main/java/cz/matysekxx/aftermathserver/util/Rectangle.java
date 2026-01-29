package cz.matysekxx.aftermathserver.util;


/// @param x Rectangle top-left corner position x
/// @param y Rectangle top-left corner position y
/// @param width Rectangle width
/// @param height Rectangle height
public record Rectangle(int x, int y, int width, int height) {
    public static Rectangle of(int x, int y, int width, int height)  {
        return new Rectangle(x, y, width, height);
    }
}
