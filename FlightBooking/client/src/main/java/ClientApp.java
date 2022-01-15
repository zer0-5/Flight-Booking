import gui.ClientCommunication;
import gui.Frame;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientApp extends Application {
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        ClientCommunication client = new ClientCommunication();
        Frame frame = new Frame(client);
        frame.setStage(stage);
        Scene mainScene = frame.getScene();

        stage.setScene(mainScene);
        stage.setTitle("AirportSystem");
        stage.show();
    }
}
