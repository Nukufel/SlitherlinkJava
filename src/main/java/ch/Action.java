package ch;

public class Action {
    private Cell cell;
    private String location;
    private Boolean value;
    private int row;
    private int col;

    public Action(Cell cell, String location, Boolean value) {
        this.cell = cell;
        this.location = location;
        this.value = value;
        this.row = cell.getRow();
        this.col = cell.getCol();
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

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}
