package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;

public class Frame implements Initializable, Navigator {
    private Stage stage;

    @FXML
    private MenuBar topMenu;
    @FXML
    private Label loggedInAs;
    @FXML
    private AnchorPane content;
    @FXML
    private HBox message;
    @FXML
    private Label messageText;
    @FXML
    private Button exitButton;
    @FXML
    private Button backButton;

    private FXMLLoader frameLoader;
    private ClientCommunication client;
    private Stack<Navigatable> navigationStack;

    public Frame(ClientCommunication client) {
        this.client = client;
        this.navigationStack = new Stack<>();
        frameLoader = new FXMLLoader(getClass().getResource("/main/gui/Frame.fxml"));
        frameLoader.setController(this);
    }

    public Scene getScene() throws IOException {
        return new Scene(frameLoader.load());
    }

    private void setContent(Node content) {
        AnchorPane.setTopAnchor(content, 0.0);
        AnchorPane.setBottomAnchor(content, 10.0);
        AnchorPane.setLeftAnchor(content, 10.0);
        AnchorPane.setRightAnchor(content, 10.0);
        this.content.getChildren().clear();
        this.content.getChildren().add(content);
    }

    public void navigateTo(Navigatable content) {
        navigationStack.push(content);
        closeMessage();
        setContent(content.getScene());

        if (this.navigationStack.size() >= 2) {
            this.exitButton.setVisible(true);
            if (navigationStack.size() >= 3) {
                this.backButton.setVisible(true);
            }
        }
    }

    public void navigateBack() {
        navigationStack.peek().onExit();
        navigationStack.pop();
        setContent(navigationStack.peek().getScene());

        if (navigationStack.size() == 1) {
            this.exitButton.setVisible(false);
        }
        if (navigationStack.size() <= 2) {
            this.backButton.setVisible(false);
        }
    }

    public void navigateBack(String message) {
        messageText.setText(message);
        this.message.setMaxHeight(Region.USE_COMPUTED_SIZE);
        this.message.setVisible(true);
        navigateBack();
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialize");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/frame/gui/LogIn.fxml"));
        LogIn controller = new LogIn(client, this);
        loader.setController(controller);

        loggedInAs.setText("Utilizador não autenticado");
        this.message.managedProperty().bindBidirectional(this.message.visibleProperty());
        this.backButton.managedProperty().bindBidirectional(this.backButton.visibleProperty());
        this.exitButton.managedProperty().bindBidirectional(this.exitButton.visibleProperty());

        this.exitButton.setVisible(false);
        this.backButton.setVisible(false);

        Node n = null;
        try {
            n = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Node finalN = n;
        navigateTo(() -> {
            return finalN;
        });
    }

    public void login() {
        //loggedInAs.setText("Autenticado como: " + sgr.getUtilizadorAutenticado().getNome());
        //Navigatable menu;

        //Utilizador autenticado = sgr.getUtilizadorAutenticado();
        //    menu = new MainMenu(sgr, this);
        //this.exitButton.setVisible(true);
        //navigateTo(menu);
    }

    @FXML
    public void onBack() {
        navigateBack();
    }
    @FXML
    public void onExit() {
        while (navigationStack.size() != 1)
            navigateBack("Sessão terminada");
    }
    @FXML
    public void onSave() {
        //FileChooser fileChooser = new FileChooser();
        //FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("OBJ Files (*.obj)", "*.obj");
        //fileChooser.getExtensionFilters().add(extFilter);
        //File file = fileChooser.showSaveDialog(getStage());
        //if (file != null) {
        //    try {
        //        client.writeToFile(file.getAbsolutePath());
        //    } catch (IOException e) {
        //        Alert alert = new Alert(Alert.AlertType.ERROR);
        //        alert.setTitle("Impossível guardar");
        //        alert.setHeaderText(e.getMessage());
        //        e.printStackTrace();
        //        alert.showAndWait();
        //    }
        //}
    }

    @FXML
    public void onOpen() {
        //FileChooser fileChooser = new FileChooser();
        //FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("OBJ Files (*.obj)", "*.obj");
        //fileChooser.getExtensionFilters().add(extFilter);
        //File file = fileChooser.showOpenDialog(getStage());
        //if (file != null) {
        //    try {
        //        client.loadFromFile(file.getAbsolutePath());
        //        System.out.println("Loaded " + sgr.getClientes().size() + " clients");
        //    } catch (IOException | ClassNotFoundException e) {
        //        Alert alert = new Alert(Alert.AlertType.ERROR);
        //        alert.setTitle("Impossível abrir");
        //        alert.setHeaderText(e.getMessage());
        //        e.printStackTrace();
        //        alert.showAndWait();
        //    }
        //}
        //onExit();
    }

    @FXML
    public void closeMessage() {
        this.message.setVisible(false);
        this.message.setMaxHeight(0);
    }
}
