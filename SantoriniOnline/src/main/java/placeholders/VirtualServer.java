package placeholders;

import event.core.EventListener;
import event.core.EventSource;
import event.core.ListenerType;
import event.gameEvents.*;
import event.gameEvents.lobby.*;
import event.gameEvents.match.*;
import event.gameEvents.prematch.*;
import view.VirtualView;

/**
 * DEBUG CLASS, USED ONLY FOR TESTING PURPOSE: public attribute
 */
public class VirtualServer extends EventSource implements EventListener {

    //todo AFTER DEBUG: remove username and make virtualView Private
    public VirtualView virtualView;
    public String username;

    public VirtualServer() {
        virtualView = null;
        //virtualView = new VirtualView( Lobby.instance(), this);
    }

    /* public VirtualServer(Lobby lobby) {
        this.virtualView = new VirtualView(lobby, this);
    } */

    //TO CONTROLLER

    @Override
    public void handleEvent(VC_ConnectionRequestGameEvent event) {
        virtualView.handleEvent(event);
    }

    @Override
    public void handleEvent(VC_RoomSizeResponseGameEvent event) {
        virtualView.handleEvent(event);
    }

    @Override
    public void handleEvent(VC_ChallengerCardsChosenEvent event) {
        virtualView.handleEvent(event);
    }

    @Override
    public void handleEvent(VC_PlayerCardChosenEvent event) {
        virtualView.handleEvent(event);
    }

    @Override
    public void handleEvent(VC_ChallengerChosenFirstPlayerEvent event) {
        virtualView.handleEvent(event);
    }


    //    TO VIEW

    @Override
    public void handleEvent(CV_RoomSizeRequestGameEvent event) {
        notifyAllObserverByType(ListenerType.VIEW, event);
    }

    @Override
    public void handleEvent(CV_RoomUpdateGameEvent event) {
        notifyAllObserverByType(ListenerType.VIEW, event);
    }

    @Override
    public void handleEvent(CV_GameStartedGameEvent event) {

    }

    @Override
    public void handleEvent(CV_NewTurnEvent event) {

    }

    @Override
    public void handleEvent(CV_IslandUpdateEvent event) {

    }

    @Override
    public void handleEvent(VC_PlayerCommandGameEvent event) {

    }

    @Override
    public void handleEvent(CV_ChallengerChosenEvent event) {
        notifyAllObserverByType(ListenerType.VIEW, event);
    }

    @Override
    public void handleEvent(CV_ChallengerChooseFirstPlayerRequestEvent event) {
        notifyAllObserverByType(ListenerType.VIEW, event);
    }

    @Override
    public void handleEvent(VC_PlayerPlacedWorkerEvent event) {

    }

    @Override
    public void handleEvent(CV_CommandRequestEvent event) {

    }

    @Override
    public void handleEvent(CV_GameOverEvent event) {

    }

    @Override
    public void handleEvent(CV_PlayerPlaceWorkerRequestEvent event) {

    }

    @Override
    public void handleEvent(CV_CardChoiceRequestGameEvent event) {
        notifyAllObserverByType(ListenerType.VIEW, event);
    }

    @Override
    public void handleEvent(CV_WaitGameEvent event) {
        notifyAllObserverByType(ListenerType.VIEW, event);
    }

    @Override
    public void handleEvent(CV_GameErrorGameEvent event) {

    }

    @Override
    public void handleEvent(CV_ConnectionRejectedErrorGameEvent event) {
        notifyAllObserverByType(ListenerType.VIEW, event);
    }


    //    NOT IMPLMENTED
    @Override
    public void handleEvent(CC_ConnectionRequestGameEvent event) {
        System.err.println("CC_Connection has been called ");
    }


    @Override
    public void handleEvent(GameEvent event) {
        System.err.println("generic <handleEvent> has been called ");
    }
}
