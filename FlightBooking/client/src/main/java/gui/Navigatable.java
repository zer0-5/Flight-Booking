package gui;

import javafx.scene.Node;

public interface Navigatable {
    Node getScene();

    default void onExit() {}
}
