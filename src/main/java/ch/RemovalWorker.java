package ch;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;

public class RemovalWorker implements Callable<ArrayList<Cell>> {
    private final Grid grid;
    private Random rand;

    public RemovalWorker(final Grid grid, Random rand) {
        this.grid = grid;
        this.rand = rand;
    }

    @Override
    public ArrayList<Cell> call() throws Exception {
        System.out.println("Started thread: " + Thread.currentThread().getName());
        return grid.getNumbersToRemove(rand);
    }
}
