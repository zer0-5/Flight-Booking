package gui;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class PopUp<T> {
    interface OnDone<T> {
        void run(T result, String message);
    }

    private Node content;
    private Navigator parent;
    private Stage stage;
    private OnDone onDone;

    protected abstract Scene getScene();

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return this.stage;
    }

    public void setOnDone(OnDone<T> onDone) {
        this.onDone = onDone;
    }

    public OnDone getOnDone() {
        return this.onDone;
    }
}
