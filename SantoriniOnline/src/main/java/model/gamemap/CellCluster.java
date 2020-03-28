package model.gamemap;

import model.exception.*;

import java.util.ArrayList;
import java.util.List;

import static model.gamemap.BlockTypeEnum.*; //Controllare che cosa significa, generato automaticamente

public class CellCluster implements Cloneable{
    private List<BlockTypeEnum> costruction;
    private boolean isComplete, isFree;
    private Worker worker;
//    private final int x,y;

    public CellCluster() {
        costruction = new ArrayList<BlockTypeEnum>();
        isComplete = false;
        isFree = true;
        isComplete = false;
    }

    /**
     * Checks if the construction inside this cell has a valid CostructionBlock order
     * @return: true if the order is valid, false otherwise
     */
    public boolean checkBuildingBlockOrder (BlockTypeEnum toBeAdded) {
        List <BlockTypeEnum> costructionAfter = new ArrayList<BlockTypeEnum>();
        costructionAfter.addAll(costruction);
        costructionAfter.add(toBeAdded);

        int[] array = new int[costructionAfter.size()]; //Auxiliary array
        for (int i = 0; i< array.length; i++) { //Converts the costruction into an integer array

            switch (costructionAfter.get(i)) {
                case LEVEL1:
                    array[i] = 1;
                    break;
                case LEVEL2:
                    array[i] = 2;
                    break;
                case LEVEL3:
                    array[i] = 3;
                    break;
                case DOME:
                    array[i] = 4;
                    break;
            }

        }

        for (int i = 0; i < array.length -1 ; i++) { //checks if it's ascending order
            if (array[i] >= array[i+1]) {
                return false;
            }
        }

        return true;

    }
    public void build(BlockTypeEnum block) throws InvalidBuildException {
        isFree = false;
        //Build:
        if (!isComplete) {

            costruction.add(block);

            if (costruction.contains(DOME)) {
                isComplete = true;
            }
        } else {
            throw new InvalidBuildException("Beware, you cannot build here, this cell is full.");
        }
    }

    //GETTERS

    /**
     * @return the actual height of construction, 0 if the cell is free
     */
    public int getCostructionHeight() {
        if (isFree && costruction.isEmpty()) {
            return 0;
        } else {
            return costruction.size();
        }

    }

    /**
     * @return true if the cell has a complete construction (with the dome)
     */
    public boolean isComplete() {
        return isComplete;
    }

    /**
     * @return true if the cell is free (no player, no constructions)
     */
    public boolean isFree() {
        return isFree;
    }

    public void addWorker(Worker worker) throws InvalidMovementException {
        if (this.worker != null) {
            throw new InvalidMovementException("WorkerAlreadyOnTop");
        } else if (isComplete) {
            throw new InvalidMovementException("DomeOnTop");
        } else {
            this.worker = worker;
        }

        isFree = false;
    }
    /*
    public Worker removeWorker () {
        Worker r = this.worker;
        worker = null;
        return this.worker;
    } */

    public void removeWorker() {
        worker = null;
    }


    public boolean hasWorkerOnTop() {
        if (worker != null) {
            return true;
        } else {
            return false;
        }
    }

    public Object clone() throws
            CloneNotSupportedException
    {
        return super.clone();
    }
}
