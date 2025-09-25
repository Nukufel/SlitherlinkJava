package ch;

import java.util.ArrayList;

public enum MyBoolean {
    TRUE, FALSE, NONE;

    public static MyBoolean switchMyBool(MyBoolean myBool) {
        return switch (myBool) {
            case TRUE -> FALSE;
            case FALSE -> TRUE;
            default -> NONE;
        };
    }

    public static ArrayList<MyBoolean> validStates(){
        ArrayList<MyBoolean> list = new ArrayList<>();
        list.add(MyBoolean.TRUE);
        list.add(MyBoolean.FALSE);
        return list;
    }
}
