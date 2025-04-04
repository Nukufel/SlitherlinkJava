package ch;

public class Action {
    private Cell cell;
    private String location;
    private Boolean value;


    public Action(Cell cell, String location, Boolean value) {
        this.cell = cell;
        this.location = location;
        this.value = value;
    }

    public Action(Action other) {
        this.cell = new Cell(other.cell);
        this.location = other.location;
        this.value = other.value;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }
}
