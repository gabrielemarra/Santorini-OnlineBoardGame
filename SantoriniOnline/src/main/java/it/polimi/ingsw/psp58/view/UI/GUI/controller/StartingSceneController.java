package it.polimi.ingsw.psp58.view.UI.GUI.controller;

import it.polimi.ingsw.psp58.event.gameEvents.lobby.VC_ConnectionRequestGameEvent;
import it.polimi.ingsw.psp58.networking.client.SantoriniClient;
import it.polimi.ingsw.psp58.view.UI.CLI.CLIView;
import it.polimi.ingsw.psp58.view.UI.GUI.BoardPopUp;
import it.polimi.ingsw.psp58.view.UI.GUI.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class StartingSceneController {
    private GUI gui;

    //LOGIN ELEMENTS
    @FXML
    private TextField ipField = null;
    @FXML
    private TextField userField = null;
    @FXML
    private Button connectButton = null;
    @FXML
    private Text loadText = null;
    @FXML
    private ProgressBar loadProgress = null;

    @FXML
    private RadioButton localhostButton;
    @FXML
    private RadioButton awsButton;
    @FXML
    private RadioButton customIPButton;
    @FXML
    private VBox customVBox;
    public Text versionText;

    private String selectedIP;
    private boolean customIP;

    public void start() throws IOException {
        loadText.setText("");
        loadProgress.setVisible(false);
        loadText.setVisible(false);

        selectedIP = "127.0.0.1";
        customIP = false;
        customVBox.setDisable(true);

        localhostButton.selectedProperty().setValue(true);
        awsButton.selectedProperty().setValue(false);
        customIPButton.selectedProperty().setValue(false);
        versionText.setText("Santorini Online - v." + gui.getGameVersion());
    }

    public void close() {
        System.exit(1);
    }

    private void disableAllLoginFields() {
        connectButton.setDisable(true);
        ipField.setDisable(true);
        userField.setDisable(true);
    }

    public void enableAllLoginFields() {
        connectButton.setDisable(false);
        ipField.setDisable(false);
        userField.setDisable(false);
    }

    public void complete() {
        loadProgress.setProgress(1);
    }

    private String updateLoadText(String text) {
        return "< " + text.toLowerCase() + " >";
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public void onClickEventConnectButton() {
        if (customIP) {
            selectedIP = ipField.getText();
        }
        loadProgress.setVisible(true);
        loadText.setText(updateLoadText("Handling connection request"));
        loadText.setVisible(true);

        loadProgress.setProgress(0.15f);
        disableAllLoginFields();
        loadProgress.setProgress(0.17f);

        String userProposal = userField.getText().toLowerCase();
        //TODO estrudere questi 2 metodi dalla cli dato che servono ovunque
        boolean localUserIsValid = CLIView.checkLocalUsernameAlphaNumeric(userProposal);
        boolean localIpIsValid = CLIView.checkValidIP(selectedIP);

        if (localIpIsValid && localUserIsValid) {
            tryConnection(userProposal);
            gui.setUsername(userProposal.toLowerCase());
        }
    }

    private void tryConnection(String userProposal) {
        //set up the client
        loadText.setText(updateLoadText("establishing connection"));
        SantoriniClient client = new SantoriniClient(gui, selectedIP, gui.isPingEnabled());
        client.begin();
        gui.setClient(client);
        VC_ConnectionRequestGameEvent req = new VC_ConnectionRequestGameEvent("connection attempt", "--", 0, userProposal);
        client.sendEvent(req);
        loadProgress.setProgress(0.5f);
        new Thread(client).start();
    }

    public void onClickLocalhostButton() {
        selectedIP = "127.0.0.1";
        customIP = false;
        customVBox.setDisable(true);

        awsButton.selectedProperty().setValue(false);
        customIPButton.selectedProperty().setValue(false);
    }

    public void onClickAWSButton() {
        try {
            File remote_server_IP = new File("src\\main\\resources\\remote_server_IP.txt");
            Scanner fileReader = new Scanner(remote_server_IP);
            selectedIP = fileReader.nextLine();

            customIP = false;
            customVBox.setDisable(true);

            localhostButton.selectedProperty().setValue(false);
            customIPButton.selectedProperty().setValue(false);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("Remote Server IP not found, please select another option");
            BoardPopUp.show("Remote Server IP not found, please select another option".toUpperCase(), gui.getStage());
            onClickLocalhostButton();
        }
    }

    public void onClickCustomIPButton() {
        selectedIP = "";
        customIP = true;
        customVBox.setDisable(false);

        localhostButton.selectedProperty().setValue(false);
        awsButton.selectedProperty().setValue(false);
    }
}
