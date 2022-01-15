package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import users.Client;

import java.net.URL;
import java.util.ResourceBundle;

public class LogIn implements Initializable {

    @FXML
    private TextField nif;

    @FXML
    private TextField password;

    @FXML
    private Button loginButton;

    @FXML
    private Pane errors;

    private ClientCommunication client;
    private Frame frame;

    public LogIn(ClientCommunication client, Frame frame) {
        this.client = client;
        this.frame = frame;
    }

    public void login(ActionEvent e)  {
        resetError();
            //client.autenticaUtilizador(nif.getText(), password.getText());
            frame.login();
    }

    @Override
    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errors.setVisible(false);
    }

    private void resetError() {
        errors.getChildren().clear();
        errors.setVisible(false);
    }

    private void showError(String error) {
        errors.getChildren().add(new Label(error));
        errors.setVisible(true);
    }
}