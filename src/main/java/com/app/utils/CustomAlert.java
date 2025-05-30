package com.app.utils;

import com.app.controllers.CustomAlertController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class CustomAlert {
    public static void showSuccessAlert(String title, boolean autoClose, int seconds) throws IOException {
        FXMLLoader loader = new FXMLLoader(CustomAlert.class.getResource("/fxml/custom-alert.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT); // Cho phép bo góc

        CustomAlertController controller = loader.getController();

        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setResizable(false);

        controller.setAlertStage(stage);
        controller.setSuccessTitle(title);
        controller.setConfirmMode(false);

        if (autoClose)
            controller.enableAutoClose(seconds);

        controller.setupMode();
        controller.startAutoClose();
        stage.showAndWait();
    }

    public static boolean showConfirmAlert(String title, String message) throws IOException {
        FXMLLoader loader = new FXMLLoader(CustomAlert.class.getResource("/fxml/custom-alert.fxml"));
        Parent root = loader.load();

        CustomAlertController controller = loader.getController();

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(new Scene(root));
        stage.setResizable(false);

        controller.setAlertStage(stage);
        controller.setTitle(title);
        controller.setMessage(message);
        controller.setConfirmMode(true);

        controller.setupMode();
        stage.showAndWait();

        return controller.getUserChoice();
    }

    public static void showAlert(Alert.AlertType alertType, String title, String message) throws IOException {
        FXMLLoader loader = new FXMLLoader(CustomAlert.class.getResource("/fxml/custom-alert.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT); // Cho phép bo góc

        CustomAlertController controller = loader.getController();

        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setResizable(false);

        controller.setAlertStage(stage);
        controller.setTitle(title);
        controller.setMessage(message);
        controller.setConfirmMode(false); // Không hiển thị nút xác nhận cho thông báo thông thường

        // Thiết lập kiểu thông báo dựa trên AlertType
        switch (alertType) {
            case INFORMATION:
                controller.setSuccessTitle(title); // Sử dụng giao diện thành công (giả sử giống showSuccessAlert)
                break;
            default:
                controller.setTitle(title); // Mặc định sử dụng tiêu đề chung
                break;
        }

        controller.setupMode();
        controller.startAutoClose(); // Tùy thuộc vào CustomAlertController, có thể tự động đóng
        stage.showAndWait();
    }
}

