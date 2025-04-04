package ch;

import java.io.Serializable;
import java.util.ArrayList;

public class Boarder implements Serializable {
    MyBoolean state = MyBoolean.NULL;
    MyBoolean result = MyBoolean.NULL;
    Integer id;
    ArrayList<Integer> cellIds = new ArrayList<>(2);

    public Boarder(Integer id) {
        this.id = id;
    }

    public Boarder(Boarder other) {
        this.id = other.id;
        this.state = other.state;
        this.result = other.result;
        this.cellIds = new ArrayList<>(other.cellIds);
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

    public void addCellId(Integer id) {
        cellIds.add(id);
    }

    public ArrayList<Integer> getCellIds() {
        return cellIds;
    }

    public Integer getOtherCellId(Integer id) {
        for (Integer cellId : cellIds) {
            if (!cellId.equals(id)) {
                return cellId;
            }
        }
        return null;
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
