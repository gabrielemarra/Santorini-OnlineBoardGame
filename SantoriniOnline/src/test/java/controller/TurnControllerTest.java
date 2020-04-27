package controller;

import event.gameEvents.match.VC_PlayerCommandGameEvent;
import exceptions.InvalidBuildException;
import exceptions.InvalidMovementException;
import model.BoardManager;
import model.CardEnum;
import model.Player;
import model.gamemap.BlockTypeEnum;
import model.gamemap.Worker;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static model.TurnAction.*;
import static org.junit.Assert.*;

public class TurnControllerTest {
    private TurnController turnController;
    private BoardManager boardManager;
    private Map<Integer, Player> turnSequence;
    private Room room;

    @Before
    public void setUp() throws Exception {
        boardManager= new BoardManager();

        //creates a mock room
        room = new Room(2, "0");

        //setup player 1
        boardManager.addPlayer("Jack");
        Player p1 = boardManager.getPlayer("Jack");
        p1.setCard(CardEnum.PROMETHEUS);
        boardManager.getIsland().placeWorker(p1.getWorker(Worker.IDs.A), 1, 2);
        boardManager.getIsland().placeWorker(p1.getWorker(Worker.IDs.B), 3, 2);

        //setup player 2
        boardManager.addPlayer("Adrian");
        Player p2 = boardManager.getPlayer("Adrian");
        p2.setCard(CardEnum.ATLAS);
        boardManager.getIsland().placeWorker(p2.getWorker(Worker.IDs.A), 1, 3);
        boardManager.getIsland().placeWorker(p2.getWorker(Worker.IDs.B), 3, 3);

        //set a mock turnSequence
        turnSequence = new HashMap<>();
        turnSequence.put(0, boardManager.getPlayer("Jack"));
        turnSequence.put(1, boardManager.getPlayer("Adrian"));

        //create the controller
        turnController = new TurnController(boardManager, turnSequence, 2,room);
    }

    @After
    public void tearDown() throws Exception {
        boardManager=null;
        turnController=null;
        turnSequence=null;
        room = null;
    }

    //simulates a turn
    @Test
    public void firstTurn_normalTurn_shouldReturnNormally() {
        turnController.firstTurn();
        assertEquals(1, turnController.getCurrentTurnNumber());
        assertEquals(turnSequence.get(0).getUsername(), turnController.getCurrentPlayerUser());
    }



    @Test
    public void handleEvent_CommandLegalMove_shouldReturnNormally() {
        firstTurn_normalTurn_shouldReturnNormally();
        int[] position = new int[]{2,2};
        VC_PlayerCommandGameEvent commandEvent = new VC_PlayerCommandGameEvent("", MOVE, "Jack",  position, Worker.IDs.A, null);
        turnController.handleEvent(commandEvent);
        Assert.assertArrayEquals( position, boardManager.getPlayer("Jack").getWorker(Worker.IDs.A).getPosition());
        assertEquals(1, turnController.getCurrentTurnInstance().getNumberOfMove());
        assertEquals(0, turnController.getCurrentTurnInstance().getNumberOfBuild());
    }

    @Test
    public void handleEvent_CommandNotLegalMove_shouldThrowException() {
        firstTurn_normalTurn_shouldReturnNormally();
        int[] position = new int[]{1,3};
        VC_PlayerCommandGameEvent commandEvent = new VC_PlayerCommandGameEvent("", MOVE, "Jack",  position, Worker.IDs.A, null);
        turnController.handleEvent(commandEvent);
        position =new int[]{1,2};
        Assert.assertArrayEquals( position, boardManager.getPlayer("Jack").getWorker(Worker.IDs.A).getPosition());
        assertEquals(0, turnController.getCurrentTurnInstance().getNumberOfMove());
        assertEquals(0, turnController.getCurrentTurnInstance().getNumberOfBuild());
    }

    @Test
    public void handleEvent_CommandMoveNotHisTurn_shouldReturnNormally() {
        firstTurn_normalTurn_shouldReturnNormally();
        int[] position = new int[]{1,4};
        VC_PlayerCommandGameEvent commandEvent = new VC_PlayerCommandGameEvent("", MOVE, "Adrian",  position, Worker.IDs.A, null);
        turnController.handleEvent(commandEvent);
        position = new int[]{1,3};
        Assert.assertArrayEquals( position, boardManager.getPlayer("Adrian").getWorker(Worker.IDs.A).getPosition());
    }

    @Test
    public void handleEvent_CommandLegalBuild_shouldReturnNormally(){
        handleEvent_CommandLegalMove_shouldReturnNormally();
        int[] position = new int[]{2,3};
        VC_PlayerCommandGameEvent commandEvent = new VC_PlayerCommandGameEvent("", BUILD, "Jack",  position, Worker.IDs.A, null);
        turnController.handleEvent(commandEvent);
        assertEquals(1 , boardManager.getIsland().getCellCluster(position[0], position[1]).getCostructionHeight());
        assertEquals(1, turnController.getCurrentTurnInstance().getNumberOfMove());
        assertEquals(1, turnController.getCurrentTurnInstance().getNumberOfBuild());
    }

    @Test
    public void handleEvent_CommandBuildInvalidCellCluster_shouldThrowException(){
        handleEvent_CommandLegalMove_shouldReturnNormally();
        int[] position = new int[]{2,2};
        VC_PlayerCommandGameEvent commandEvent = new VC_PlayerCommandGameEvent("", BUILD, "Jack",  position, Worker.IDs.A, null);
        turnController.handleEvent(commandEvent);
        assertEquals(0 , boardManager.getIsland().getCellCluster(position[0], position[1]).getCostructionHeight());
        assertEquals(1, turnController.getCurrentTurnInstance().getNumberOfMove());
        assertEquals(0, turnController.getCurrentTurnInstance().getNumberOfBuild());
    }

    @Test
    public void handleEvent_CommandBuildInvalidBlock_shouldReturnNormally(){
        handleEvent_CommandLegalMove_shouldReturnNormally();
        int[] position = new int[]{3,3};
        VC_PlayerCommandGameEvent commandEvent = new VC_PlayerCommandGameEvent("", BUILD, "Jack",  position, Worker.IDs.A, BlockTypeEnum.DOME);
        turnController.handleEvent(commandEvent);
        assertEquals(0 , boardManager.getIsland().getCellCluster(position[0], position[1]).getCostructionHeight());
        assertEquals(1, turnController.getCurrentTurnInstance().getNumberOfMove());
        assertEquals(0, turnController.getCurrentTurnInstance().getNumberOfBuild());
    }

    @Test
    public void handleEvent_CommandBuildNotHisTurn_shouldReturnNormally(){
        handleEvent_CommandLegalMove_shouldReturnNormally();
        int[] position = new int[]{1,4};
        VC_PlayerCommandGameEvent commandEvent = new VC_PlayerCommandGameEvent("", BUILD, "Adrian",  position, Worker.IDs.A, null);
        turnController.handleEvent(commandEvent);
        assertEquals(0, boardManager.getIsland().getCellCluster(position[0], position[1]).getCostructionHeight());
        assertEquals(1, turnController.getCurrentTurnInstance().getNumberOfMove());
        assertEquals(0, turnController.getCurrentTurnInstance().getNumberOfBuild());
    }

    @Test
    public void nextTurn_normalTurn_shouldReturnNormally(){
        handleEvent_CommandLegalBuild_shouldReturnNormally();
        VC_PlayerCommandGameEvent commandEvent = new VC_PlayerCommandGameEvent("", PASS, "Jack", null , null, null);
        turnController.handleEvent(commandEvent);
        assertEquals(turnSequence.get(1).getUsername(), turnController.getCurrentPlayerUser());
    }



}