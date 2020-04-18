package controller;

import event.core.EventListener;
import event.core.EventSource;
import event.gameEvents.*;
import event.gameEvents.lobby.*;
import event.gameEvents.prematch.*;
import model.BoardManager;
import model.CardEnum;
import model.Player;
import model.WorkerColors;
import model.exception.InvalidCardException;
import model.exception.InvalidMovementException;
import model.gamemap.Worker;

import javax.naming.LimitExceededException;
import java.util.*;

import static event.core.ListenerType.VIEW;

public class PreGameController extends EventSource implements EventListener {
    private BoardManager boardManager;
    private String challenger;
    private Room room;
    private Map<String, CardEnum> playersCardsCorrespondence;
    private List<CardEnum> availableCards;
    private Map<Integer, Player> turnSequence;

    public PreGameController(BoardManager boardManager, Room room) {
        this.boardManager = boardManager;
        this.room = room;
        this.playersCardsCorrespondence = new HashMap<String, CardEnum>();
        this.availableCards = new ArrayList<CardEnum>();
        this.turnSequence = new HashMap<Integer, Player>();
    }

    public void start() {
        setColors();
        chooseChallenger();
    }

    public void chooseChallenger() {
        //choose the challenger in a random way
        Random random = new Random();
        int number = random.nextInt(room.getSIZE());
        List<String>  players = room.getActiveUsers();
        challenger = players.get(number);
        for (String recipient: players){
            if (!recipient.equals(challenger)){
                CV_WaitGameEvent requestEvent = new CV_WaitGameEvent("is the challenger, he's now choosing the cards", challenger, recipient);
                notifyAllObserverByType(VIEW, requestEvent);
            }

        }

        //DEBUG
//        System.out.println("Il challenger è "+challenger);

        CV_ChallengerChosenEvent event = new CV_ChallengerChosenEvent(challenger, room.getSIZE());
        notifyAllObserverByType(VIEW, event);
    }

    public Map<String, CardEnum> getPlayersCardsCorrespondence() {
        return playersCardsCorrespondence;
    }

    public Map<Integer, Player> getTurnSequence() {
        return turnSequence;
    }

    public void setColors(){
        //DA FARE
        List <String> players = room.getActiveUsers();
        for (int i = 0; i<room.getSIZE(); i++)
        {
            String player = players.get(i);
            boardManager.getPlayer(player).setColor(WorkerColors.values()[i]);
        }
    }


    @Override
    public void handleEvent(VC_ChallengerCardsChosenEvent event) {
        availableCards = event.getCardsChosen();
        for (CardEnum card : availableCards) {
            try {
                boardManager.selectCard(card);
            } catch (InvalidCardException e) {

            } catch (LimitExceededException e) {

            }
        }
        List<String> players = room.getActiveUsers();
        int i = players.indexOf(challenger);
        if(i+1 < room.getSIZE()) {
            i++;
        }
        else if (i+1 == room.getSIZE()){
            i=0;
        }
        for (String recipient: players){
            if (!recipient.equals(players.get(i))){
                CV_WaitGameEvent requestEvent = new CV_WaitGameEvent("is choosing his card", players.get(i), recipient);
                notifyAllObserverByType(VIEW, requestEvent);
            }
        }
        CV_CardChoiceRequestGameEvent requestEvent = new CV_CardChoiceRequestGameEvent("Choose one card from the list", availableCards, players.get(i));
        notifyAllObserverByType(VIEW, requestEvent);
    }

    @Override
    public void handleEvent(VC_PlayerCardChosenEvent event) {
        try {
            boardManager.takeCard(event.getCard());
            playersCardsCorrespondence.put(event.getPlayer(), event.getCard());
            availableCards.remove(event.getCard());
            List<String> players = room.getActiveUsers();

            int i = players.indexOf(event.getPlayer());
            if (availableCards.size()> 0){
                if((i < room.getSIZE() - 1) ){
                    i++;
                }
                else if ((i+1 == room.getSIZE() - 1)){
                    i=0;
                }
                for (String recipient: players){
                    if (!recipient.equals(players.get(i))){
                        CV_WaitGameEvent requestEvent = new CV_WaitGameEvent("is choosing his card", players.get(i), recipient);
                        notifyAllObserverByType(VIEW, requestEvent);
                    }
                }
                CV_CardChoiceRequestGameEvent requestEvent = new CV_CardChoiceRequestGameEvent("Choose one card from the list", availableCards, players.get(i));
                notifyAllObserverByType(VIEW, requestEvent);
            }
            else {
                for (String recipient: players){
                    if (!recipient.equals(players.get(i))){
                        CV_WaitGameEvent requestEvent = new CV_WaitGameEvent("is choosing the first player", challenger, recipient);
                        notifyAllObserverByType(VIEW, requestEvent);
                    }
                }

                CV_ChallengerChooseFirstPlayerRequestEvent requestEvent = new CV_ChallengerChooseFirstPlayerRequestEvent("Choose the first player", challenger, players);
                notifyAllObserverByType(VIEW, requestEvent);
            }
        } catch (InvalidCardException e) {

        }
    }

    @Override
    public void handleEvent(VC_ChallengerChosenFirstPlayerEvent event) {
        boardManager.setPlayersCardsCorrespondence(playersCardsCorrespondence);
        String firstPlayer = event.getUsername();
        int index = 0;
        turnSequence.put(index, boardManager.getPlayer(firstPlayer));
        index ++;
        String[] players = room.getActiveUsersCopy();

        //create the turnSequence based on the
        for (int i = 0; i < players.length; i++) {
            if (players[i].equals(firstPlayer)) {
                if (i == players.length - 1) { //the player chosen is the last in the list of the room
                    int j = 0;
                    while (j < players.length - 1) {
                        turnSequence.put(index, boardManager.getPlayer(players[j]));
                        index ++;
                        j++;
                    }
                } else { // the player is not the last in the list of the room
                    int j = i;
                    while (j < players.length - 1) { //adds all the player past the chosen player
                        turnSequence.put(index, boardManager.getPlayer(players[j]));
                        index++;
                        j++;
                    }
                    if (turnSequence.size() < players.length) { // if there are, adds all the player before the chosen one
                        j = 0;
                        while (j < i) {
                            turnSequence.put(index, boardManager.getPlayer(players[j]));
                            index++;
                            j++;
                        }
                    }
                }
            }
        }
        placeAllWorkers();
        room.beginGame(turnSequence);
    }

    public void placeAllWorkers()
    {
        List<Player> players = new ArrayList<Player>();
        for (Map.Entry<Integer, Player> player : turnSequence.entrySet()) {
            players.add(player.getValue());
        }
        for (Player player : players){
            for (Player recipient: players){
                if (!recipient.getUsername().equals(player.getUsername())){
                    CV_WaitGameEvent requestEvent = new CV_WaitGameEvent("is placing his workers", player.getUsername(), recipient.getUsername());
                    notifyAllObserverByType(VIEW, requestEvent);
                }
            }
            CV_PlayerPlaceWorkersRequestEvent event = new CV_PlayerPlaceWorkersRequestEvent("Choose where to put your workers", player.getUsername());
            notifyAllObserverByType(VIEW, event);
        }
    }

    @Override
    public void handleEvent(VC_PlayerPlacedWorkerEvent event){
        Player actingPlayer = boardManager.getPlayer(event.getActingPlayer());
        Worker worker = actingPlayer.getWorker(event.getId());
        int x = event.getPosX();
        int y = event.getPosY();
        try {
            actingPlayer.getCard().placeWorker(worker, x , y, boardManager.getIsland());
            //DEBUG
            System.out.println("DEBUG: PREGAME: Worker placed");
        } catch (CloneNotSupportedException e) {

        } catch (InvalidMovementException e) {

        }
    }


        @Override //NO IMPL
    public void handleEvent(CV_ChallengerChosenEvent event) {
    }

    @Override
    public void handleEvent(CV_CardChoiceRequestGameEvent event) {

    }

    @Override
    public void handleEvent(CV_WaitGameEvent event) {

    }

    @Override
    public void handleEvent(CV_ChallengerChooseFirstPlayerRequestEvent event) {

    }

    @Override
    public void handleEvent(CV_PlayerPlaceWorkersRequestEvent event) {

    }

    @Override
    public void handleEvent(GameEvent event) {
    }

    @Override
    public void handleEvent(VC_RoomSizeResponseGameEvent event) {
    }

    @Override
    public void handleEvent(CV_RoomUpdateGameEvent event) {

    }

    @Override
    public void handleEvent(VC_ConnectionRequestGameEvent event) {
    }

    @Override
    public void handleEvent(CC_ConnectionRequestGameEvent event) {

    }

    @Override
    public void handleEvent(CV_RoomSizeRequestGameEvent event) {
    }

    @Override
    public void handleEvent(CV_ConnectionRejectedErrorGameEvent event) {
    }
}
