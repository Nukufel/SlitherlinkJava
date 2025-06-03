package ch;

public enum MyBoolean {
    TRUE, FALSE, NONE;

    public static MyBoolean switchMyBool(MyBoolean myBool) {
        return switch (myBool) {
            case TRUE -> FALSE;
            case FALSE -> TRUE;
            default -> NONE;
        };
    }
}
