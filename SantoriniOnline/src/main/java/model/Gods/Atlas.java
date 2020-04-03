package model.Gods;

import model.BehaviourManager;
import model.Card;
import model.Player;

public class Atlas extends Card {
    public Atlas(Player player) {
        super(player);
        name = "Atlas";
        description = "Your Worker may build a dome at any level.";
    }

    /**
     * Reset the {@code behaviour} of the {@link Player} to the default value of the card, some Gods need to override this method
     */
    @Override
    public void resetBehaviour() {
        BehaviourManager behaviour = playedBy.getBehaviour();
        behaviour.setBlockPlacementLeft(1);
        behaviour.setMovementsRemaining(1);
        behaviour.setCanClimb(true);
        behaviour.setCanBuildDomeEverywhere(true); //<-- Set to True for this God
    }
}
