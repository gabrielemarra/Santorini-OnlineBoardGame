package model.Gods;

import model.BehaviourManager;
import model.Card;
import model.Player;
import model.exception.InvalidMovementException;
import model.exception.WinningException;
import model.gamemap.CellCluster;
import model.gamemap.Island;
import model.gamemap.Worker;

import java.util.List;

public class Apollo extends Card {
    public Apollo(Player player) {
        super(player);
        name = "Apollo";
        description = "Your Worker may move into an opponent Worker’s space by forcing their Worker to the space yours just vacated.";
    }

    @Override
    public void move(Worker worker, int desiredX, int desiredY, Island island) throws InvalidMovementException, WinningException {
        if (island.getCellCluster(desiredX, desiredY).hasWorkerOnTop()) {
            int actualX = worker.getPosition()[0];
            int actualY = worker.getPosition()[1];

            if (!isValidDestination(actualX, actualY, desiredX, desiredY, island)) {
                throw new InvalidMovementException("Invalid move for this worker");
            }
            //decrementa il numero di movimenti rimasti
            playedBy.getBehaviour().setMovementsRemaining(playedBy.getBehaviour().getMovementsRemaining() - 1);

            CellCluster desiredCellCluster = island.getCellCluster(desiredX, desiredY);
            String enemyUsername = desiredCellCluster.getWorkerOwnerUsername();
            Worker.IDs enemyWorkerID = desiredCellCluster.getWorkerID();

            List<Player> playerList = playedBy.getPlayers();
            Player oppositePlayer = null;
            for (Player actual : playerList) {
                if (actual.getUsername().equals(enemyUsername)) {
                    oppositePlayer = actual;
                }
            }

            if (oppositePlayer == null) throw new InvalidMovementException("Opposite Player not found");
            Worker enemyWorker = oppositePlayer.getWorker(enemyWorkerID);

            desiredCellCluster.removeWorker();
            island.moveWorker(worker, desiredX, desiredY);
            island.placeWorker(enemyWorker, actualX, actualY);

            if (!checkWorkerPosition(island, worker, desiredX, desiredY) && !checkWorkerPosition(island, enemyWorker, actualX, actualY)) {
                throw new InvalidMovementException("The move is valid but there was an error applying desired changes");
            } else {
                //Memorizzo l'altitudine del worker per poi controllare se è effettivamente salito
                int oldAltitudeOfPlayer = island.getCellCluster(actualX, actualY).getCostructionHeight();
                checkWin(island, desiredX, desiredY, oldAltitudeOfPlayer);
            }
        } else {
            super.move(worker, desiredX, desiredY, island);
        }
    }

    @Override
    protected boolean isValidDestination(int actualX, int actualY, int desiredX, int desiredY, Island island) {
        CellCluster actualCellCluster = island.getCellCluster(actualX, actualY);
        CellCluster desiredCellCluster = island.getCellCluster(desiredX, desiredY);
        BehaviourManager behaviour = playedBy.getBehaviour();

        //Verifico che la coordinate di destinazione siano diverse da quelle attuali
        if (actualX == desiredX && actualY == desiredY) {
            return false;
        }
        //verifica il behaviour permette di muoversi
        if (behaviour.getMovementsRemaining() <= 0) {
            return false;
        }
        //calcola la distanza euclidea e verifica che sia min di 2 (ritorna false altrimenti)
        if (distance(actualX, actualY, desiredX, desiredY) >= 2) {
            return false;
        }
        //verifica il behaviour permette di salire
        if (behaviour.isCanClimb()) {
            //al max salgo di 1
            if (actualCellCluster.getCostructionHeight() + 1 < desiredCellCluster.getCostructionHeight()) {
                return false;
            }
        } else {
            //non posso salire
            if (actualCellCluster.getCostructionHeight() < desiredCellCluster.getCostructionHeight()) {
                return false;
            }
        }
        return true;
    }
}
