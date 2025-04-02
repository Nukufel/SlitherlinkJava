package ch;

public class Boarder {
    MyBoolean state = MyBoolean.NULL;
    MyBoolean result = MyBoolean.NULL;
    Integer id;

    public Boarder(Integer id) {
        this.id = id;
    }

    public void toggleBoarder() {
        if (state == MyBoolean.FALSE) {
            state = MyBoolean.NULL;
        }
        if (state == MyBoolean.TRUE) {
            state = MyBoolean.FALSE;
        }
        if (state == MyBoolean.NULL) {
            state = MyBoolean.TRUE;
        }
    }

    public boolean isCorrect(){
        return state == result;
    }

    public MyBoolean getResult() {
        return result;
    }

    public void setResult(MyBoolean result) {
        this.result = result;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MyBoolean getState() {
        return state;
    }

    public void setState(MyBoolean state) {
        this.state = state;
    }
}
