package gui;

import javafx.stage.Modality;
import javafx.stage.Stage;

public interface Navigator {
    // Esta interface gere a navegação entre dois ecrãs
    void navigateTo(Navigatable node);
    void navigateBack();
    void navigateBack(String message);

    Stage getStage();

    default <T> void openPopup(PopUp<T> popUp) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(getStage());

        popUp.setStage(dialog);

        dialog.setScene(popUp.getScene());
        dialog.show();
    }
}
