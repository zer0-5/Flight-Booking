import client.Client;
import gui.Frame;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApp extends Application {
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Frame frame = new Frame(new Client());
        frame.setStage(stage);
        Scene mainScene = frame.getScene();

        stage.setScene(mainScene);
        stage.setTitle("AirportSystem");
        stage.show();
    }
}
