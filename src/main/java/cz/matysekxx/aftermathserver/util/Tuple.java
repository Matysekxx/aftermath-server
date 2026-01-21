package cz.matysekxx.aftermathserver.util;

public record Tuple<V1, V2>(V1 first, V2 second) {
    public static <V1, V2> Tuple<V1, V2> of(V1 first, V2 second) {
        return new Tuple<V1, V2>(first, second);
    }
}
