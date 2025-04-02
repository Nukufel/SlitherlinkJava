package ch;

public enum Location {
    TOP,RIGHT,BOTTOM,LEFT;

    public static Location getNext(Location loc) {
        return switch (loc) {
            case TOP -> RIGHT;
            case RIGHT -> BOTTOM;
            case BOTTOM -> LEFT;
            case LEFT -> TOP;
        };
    }
}
