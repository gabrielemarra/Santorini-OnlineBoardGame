package model;

import model.Gods.Artemis;
import model.exception.*;
import model.gamemap.BlockTypeEnum;
import model.gamemap.Island;
import model.gamemap.Worker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import placeholders.IslandPrinter;

import static org.junit.Assert.*;

public class CardTest {
    Card card = null;
    Player player = new Player("Marco");
    Island island = null;
    Worker worker1 = null;
    Worker worker2 = null;
    IslandPrinter ip = null;

    @Before
    public void setUp() throws Exception {
        card = new Artemis(player);
        player.setCard(card);
        island = new Island();
        ip = new IslandPrinter(island);
        worker1 = new Worker(Worker.IDs.A, player.getUsername());
        worker2 = new Worker(Worker.IDs.B, player.getUsername());
    }

    @After
    public void tearDown() throws Exception {
        player.setCard((Card) null);
        card = null;
    }


    @Test //DONE
    public void resetBehaviour() {
        BehaviourManager behaviourManager = player.getBehaviour();
        behaviourManager.setCanClimb(false);
        behaviourManager.setCanBuildDomeEverywhere(true);
        behaviourManager.setBlockPlacementLeft(25);
        behaviourManager.setMovementsRemaining(0);

        card.resetBehaviour();

        assertTrue(behaviourManager.isCanClimb());
        assertFalse(behaviourManager.isCanBuildDomeEverywhere());
        assertEquals(1, behaviourManager.getBlockPlacementLeft());
        assertEquals(1, behaviourManager.getMovementsRemaining());
    }

    @Test //DONE
    public void distance() {
        assertEquals(5, card.distance(0, 0, 4, 3), 0);
    }

    @Test //DONE
    public void checkWorkerPosition_CorrectInput() throws InvalidBuildException, InvalidMovementException {
        System.out.println("TEST: checkWorkerPosition_CorrectInput");
        island.buildBlock(BlockTypeEnum.LEVEL1, 3, 3);
        island.buildBlock(BlockTypeEnum.LEVEL1, 4, 4);
        island.placeWorker(worker1, 4, 4);

        assertTrue(card.checkWorkerPosition(island, worker1, 4, 4));
    }

    @Test //DONE
    public void checkWorkerPosition_WrongInput_WorkerNotOnTop() throws InvalidBuildException, InvalidMovementException, InvalidWorkerRemovalException {
        System.out.println("TEST: checkWorkerPosition_WrongInput_WorkerNotOnTop");
        island.buildBlock(BlockTypeEnum.LEVEL1, 3, 3);
        island.buildBlock(BlockTypeEnum.LEVEL1, 4, 4);
        island.placeWorker(worker1, 4, 4);
        System.out.println(island.getCellCluster(4, 4).hasWorkerOnTop());
        island.removeWorker(worker1);

        assertFalse(card.checkWorkerPosition(island, worker1, 4, 4));
    }

    @Test(expected = WinningException.class) //DONE
    public void checkWin_correctInput() throws InvalidBuildException, InvalidMovementException, WinningException {
        System.out.println("TEST: checkWin_correctInput");
        island.buildBlock(BlockTypeEnum.LEVEL1, 3, 3);
        island.buildBlock(BlockTypeEnum.LEVEL2, 3, 3);
        island.buildBlock(BlockTypeEnum.LEVEL3, 3, 3);
        island.placeWorker(worker1, 3, 3);

        card.checkWin(island, 3, 3, 2);
    }

    @Test //DONE
    public void checkWin_WrongInput_NotAWorkerOnTop() throws InvalidBuildException, InvalidMovementException, WinningException {
        System.out.println("TEST: checkWin_correctInput");
        island.buildBlock(BlockTypeEnum.LEVEL1, 3, 3);
        island.buildBlock(BlockTypeEnum.LEVEL2, 3, 3);
        island.buildBlock(BlockTypeEnum.LEVEL3, 3, 3);
        island.placeWorker(worker1, 4, 4);

        card.checkWin(island, 3, 3, 2);
    }

    @Test //DONE
    public void checkWin_WrongInput_WorkerAtLevel2() throws InvalidBuildException, InvalidMovementException, WinningException {
        System.out.println("TEST: checkWin_correctInput");
        island.buildBlock(BlockTypeEnum.LEVEL1, 3, 3);
        island.buildBlock(BlockTypeEnum.LEVEL2, 3, 3);
        island.placeWorker(worker1, 4, 4);

        card.checkWin(island, 3, 3, 2);
    }

    @Test //DONE
    public void checkWin_WrongInput_WorkerDontClimb() throws InvalidBuildException, InvalidMovementException, WinningException {
        System.out.println("TEST: checkWin_correctInput");
        island.buildBlock(BlockTypeEnum.LEVEL1, 3, 3);
        island.buildBlock(BlockTypeEnum.LEVEL2, 3, 3);
        island.buildBlock(BlockTypeEnum.LEVEL3, 3, 3);
        island.placeWorker(worker1, 3, 3);

        card.checkWin(island, 3, 3, 3);
    }

    @Test
    public void placeWorker_correctInput() throws InvalidMovementException, InvalidBuildException, CloneNotSupportedException {
        island.buildBlock(BlockTypeEnum.LEVEL1, 3, 4);

        island.placeWorker(worker1, 2, 2);

        card.placeWorker(worker2, 1, 1, island);
    }

//    @Test
//    public void placeWorker_correctInput() throws InvalidMovementException, MoveExecutionException, InvalidBuildException, CloneNotSupportedException {
//        ip.displayIsland();
//        island.buildBlock(BlockTypeEnum.LEVEL1, 3, 4);
//
//        ip.displayIsland();
//
//        island.placeWorker(worker1, 2, 2);
//
//        ip.displayIsland();
//
//        card.placeWorker(worker2, 1, 1, island);
//
//        ip.displayIsland();
//    }

    @Test(expected = InvalidMovementException.class)
    public void placeWorker_WrongInput_PlayerAlreadyOnTop() throws InvalidMovementException, CloneNotSupportedException {
        island.placeWorker(worker1, 4, 4);

        card.placeWorker(worker2, 4, 4, island);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void placeWorker_WrongInput_ErrorIndex() throws InvalidMovementException, CloneNotSupportedException {
        island.placeWorker(worker1, 4, 4);

        card.placeWorker(worker2, 6, 4, island);
    }

    @Test(expected = InvalidMovementException.class)
    public void placeWorker_WrongInput_WorkerAlreadyPlaced() throws InvalidMovementException, CloneNotSupportedException {
        island.placeWorker(worker1, 4, 4);

        card.placeWorker(worker1, 4, 4, island);
    }

    @Test(expected = InvalidMovementException.class)
    public void placeWorker_WrongInput_DomeOnTop() throws InvalidMovementException, InvalidBuildException, CloneNotSupportedException {
        island.buildBlock(BlockTypeEnum.DOME, 3, 4);

        card.placeWorker(worker2, 3, 4, island);
    }

    @Test
    public void move_Simple_RightMove() throws InvalidMovementException, WinningException, CloneNotSupportedException {
        card.placeWorker(worker1, 0, 0, island);
        card.placeWorker(worker2, 0, 1, island);

        card.resetBehaviour();
        card.move(worker1, 1, 1, island);
    }

    @Test
    public void move_RightMove() throws InvalidMovementException, WinningException, InvalidBuildException, CloneNotSupportedException {
        card.placeWorker(worker1, 0, 0, island);
        island.buildBlock(BlockTypeEnum.LEVEL1, 1, 1);
        card.placeWorker(worker2, 0, 1, island);

        card.resetBehaviour();
        card.move(worker1, 1, 1, island);
    }

    @Test(expected = InvalidMovementException.class)
    public void move_Simple_WrongMove_PlayerAlreadyOnCell() throws InvalidMovementException, WinningException, CloneNotSupportedException {
        card.placeWorker(worker1, 0, 0, island);
        card.placeWorker(worker2, 0, 1, island);

        card.resetBehaviour();
        card.move(worker1, 0, 1, island);
    }

    @Test(expected = InvalidMovementException.class)
    public void move_Simple_WrongMove_TooFar() throws InvalidMovementException, WinningException, CloneNotSupportedException {
        card.placeWorker(worker1, 0, 0, island);

        card.resetBehaviour();
        card.move(worker1, 2, 1, island);
    }

    @Test(expected = InvalidMovementException.class)
    public void move_WrongMove_TooHigh() throws InvalidMovementException, WinningException, InvalidBuildException, CloneNotSupportedException {
        card.placeWorker(worker1, 0, 0, island);
        island.buildBlock(BlockTypeEnum.LEVEL1, 0, 1);
        island.buildBlock(BlockTypeEnum.LEVEL2, 0, 1);

        card.resetBehaviour();
        card.move(worker1, 0, 1, island);
    }

    @Test(expected = InvalidMovementException.class)
    public void move_WrongMove_DomeOnTop() throws InvalidMovementException, WinningException, InvalidBuildException, CloneNotSupportedException {
        card.placeWorker(worker1, 0, 0, island);
        island.buildBlock(BlockTypeEnum.DOME, 1, 1);

        card.resetBehaviour();
        card.move(worker1, 1, 1, island);
    }

    @Test(expected = InvalidMovementException.class)
    public void move_WrongMove_NoMovementesRemaining() throws InvalidMovementException, WinningException, InvalidBuildException, CloneNotSupportedException {
        card.placeWorker(worker1, 0, 0, island);
        card.resetBehaviour();

        BehaviourManager behaviourManager = player.getBehaviour();
        behaviourManager.setMovementsRemaining(0);

        card.move(worker1, 1, 1, island);
    }

    @Test(expected = InvalidMovementException.class)
    public void move_WrongMove_NoClimbAdmit() throws InvalidMovementException, WinningException, InvalidBuildException, CloneNotSupportedException {
        card.placeWorker(worker1, 0, 0, island);
        island.buildBlock(BlockTypeEnum.LEVEL1, 0, 1);
        card.resetBehaviour();

        BehaviourManager behaviourManager = player.getBehaviour();
        behaviourManager.setCanClimb(false);

        card.move(worker1, 0, 1, island);
    }

    @Test(expected = InvalidMovementException.class)
    public void move_WrongMove_SameCoordinates() throws InvalidMovementException, WinningException, CloneNotSupportedException {
        card.placeWorker(worker1, 0, 0, island);

        card.resetBehaviour();
        card.move(worker1, 0, 0, island);
    }

    @Test(expected = WinningException.class)
    public void move_RightMove_Win() throws InvalidMovementException, WinningException, InvalidBuildException, CloneNotSupportedException {
        island.buildBlock(BlockTypeEnum.LEVEL1, 0, 0);
        island.buildBlock(BlockTypeEnum.LEVEL2, 0, 0);
        card.placeWorker(worker1, 0, 0, island);
        island.buildBlock(BlockTypeEnum.LEVEL1, 0, 1);
        island.buildBlock(BlockTypeEnum.LEVEL2, 0, 1);
        island.buildBlock(BlockTypeEnum.LEVEL3, 0, 1);

        card.resetBehaviour();
        card.move(worker1, 0, 1, island);
    }

    @Test(expected = InvalidMovementException.class)
    public void move_WrongMove_TooMuchMovements() throws InvalidMovementException, WinningException, CloneNotSupportedException {
        card.placeWorker(worker1, 0, 0, island);

        card.resetBehaviour();
        card.move(worker1, 0, 0, island);
        card.move(worker1, 0, 0, island);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void move_WrongMove_IndexError() throws InvalidMovementException, WinningException, CloneNotSupportedException {
        card.placeWorker(worker1, 0, 0, island);

        card.resetBehaviour();
        card.move(worker1, 0, -5, island);
    }

    @Test
    public void build_RightBuild() throws InvalidMovementException, InvalidBuildException, CloneNotSupportedException, WinningException {
        card.placeWorker(worker1, 0, 0, island);
        card.move(worker1, 1, 0, island);

        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL1, 1, 1, island);
    }

    @Test
    public void build_ComplexCorrectSequence() throws InvalidMovementException, InvalidBuildException, CloneNotSupportedException, WinningException {
        card.placeWorker(worker1, 0, 0, island);
        card.move(worker1, 1, 0, island);

        card.build(worker1, BlockTypeEnum.LEVEL1, 1, 1, island);
        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL1, 0, 0, island);
        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL1, 0, 1, island);
        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL1, 2, 1, island);
        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL2, 1, 1, island);
        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL3, 1, 1, island);
        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.DOME, 1, 1, island);
    }

    @Test(expected = InvalidBuildException.class)
    public void build_WrongBuild_tooFar() throws InvalidMovementException, InvalidBuildException, CloneNotSupportedException, WinningException {
        card.placeWorker(worker1, 0, 0, island);
        card.move(worker1, 1, 0, island);

        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL1, 1, 2, island);
    }

    @Test(expected = InvalidBuildException.class)
    public void build_WrongBuild_PlayerOnTop1() throws InvalidMovementException, InvalidBuildException, CloneNotSupportedException, WinningException {
        card.placeWorker(worker1, 0, 0, island);
        card.move(worker1, 1, 0, island);

        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL1, 1, 0, island);
    }

    @Test(expected = InvalidBuildException.class)
    public void build_WrongBuild_PlayerOnTop2() throws InvalidMovementException, InvalidBuildException, CloneNotSupportedException, WinningException {
        card.placeWorker(worker1, 0, 0, island);
        card.move(worker1, 1, 0, island);
        card.placeWorker(worker2, 1, 1, island);

        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL1, 1, 1, island);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void build_WrongBuild_ErrorIndex() throws InvalidMovementException, InvalidBuildException, CloneNotSupportedException, WinningException {
        card.placeWorker(worker1, 0, 0, island);
        card.move(worker1, 1, 0, island);

        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL1, 7, 0, island);
    }

    @Test(expected = InvalidBuildException.class)
    public void build_WrongBuild_DomeOnTop() throws InvalidMovementException, InvalidBuildException, CloneNotSupportedException, WinningException {
        card.placeWorker(worker1, 0, 0, island);
        island.buildBlock(BlockTypeEnum.DOME, 1, 1);
        card.move(worker1, 1, 0, island);

        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL1, 1, 1, island);
    }

    @Test(expected = InvalidBuildException.class)
    public void build_WrongBuild_TooMuchBlockPlacement() throws InvalidMovementException, InvalidBuildException, CloneNotSupportedException, WinningException {
        card.placeWorker(worker1, 0, 0, island);
        card.move(worker1, 1, 0, island);

        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL1, 1, 1, island);
        card.build(worker1, BlockTypeEnum.LEVEL1, 1, 2, island);
    }

    @Test(expected = InvalidBuildException.class)
    public void build_WrongBuild_WrongOrderPosition() throws InvalidMovementException, InvalidBuildException, CloneNotSupportedException, WinningException {
        card.placeWorker(worker1, 0, 0, island);
        card.move(worker1, 1, 0, island);

        card.build(worker1, BlockTypeEnum.LEVEL1, 1, 1, island);
        card.resetBehaviour();
        card.build(worker1, BlockTypeEnum.LEVEL3, 1, 1, island);
    }

    @Test(expected = InvalidBuildException.class)
    public void build_WrongBuild_BuildDomeEveryWhere() throws InvalidMovementException, InvalidBuildException, CloneNotSupportedException, WinningException {
        card.placeWorker(worker1, 0, 0, island);
        card.move(worker1, 1, 0, island);

        card.resetBehaviour();

        BehaviourManager behaviourManager = player.getBehaviour();
        behaviourManager.setCanBuildDomeEverywhere(true);
        behaviourManager.setBlockPlacementLeft(2);

        card.build(worker1, BlockTypeEnum.DOME, 1, 1, island);
        card.build(worker1, BlockTypeEnum.DOME, 1, 2, island);
    }
}