package com.app.controllers.Rooms;

import com.app.controllers.HomePageController;
import com.app.controllers.PaymentsController;
import com.app.controllers.Residents.ResidentsController;
import com.app.controllers.RevenuesController;
import com.app.models.Rooms;
import com.app.utils.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class RoomsController {
    private String role;
    private String username;

    //    Header
    @FXML
    private Label roleLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private MenuItem MenuItem_SignUp;

    //    Body
    @FXML
    private GridPane roomsGrid;
    @FXML
    private TextField roomField;
    @FXML
    private ComboBox<ComboBoxOption> floorBox;
    @FXML
    private ComboBox<ComboBoxOption> statusBox;

    private final ObservableList<Rooms> roomList = FXCollections.observableArrayList();

    private final ObservableList<Rooms> filteredRoomList = FXCollections.observableArrayList();

    @FXML
    public void initialize(String role, String username) throws IOException {
        this.role = role;
        this.username = username;

        if (Objects.equals(role, "admin")) {
            roleLabel.setText("Bạn đang đăng nhập với quyền Quản trị viên.");
            MenuItem_SignUp.setVisible(true);
        } else if (Objects.equals(role, "accountant")) {
            roleLabel.setText("Bạn đang đăng nhập với quyền Kế toán.");
        }

        nameLabel.setText("Xin chào, " + username);

        initFloorBox();
        initStatusBox();
        loadRoomsFromDatabase();

        filteredRoomList.addAll(roomList);
        populateRoomsGrid();

        setupSearchAndFilter();
    }

    private void initFloorBox() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "SELECT DISTINCT floor FROM rooms ORDER BY floor ASC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            ObservableList<ComboBoxOption> allFloors = FXCollections.observableArrayList();

            while (rs.next()) {
                int floor = rs.getInt("floor");
                ComboBoxOption option = new ComboBoxOption("Tầng " + floor, String.valueOf(floor));
                allFloors.add(option);
            }
            allFloors.addFirst(new ComboBoxOption("Tất cả", "Tất cả"));
            floorBox.setItems(allFloors);
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Không thể tải dữ liệu tầng từ CSDL.");
        }
    }

    private void initStatusBox() {
        statusBox.setItems(FXCollections.observableArrayList(
                new ComboBoxOption("Tất cả", "Tất cả"),
                new ComboBoxOption("Đang ở", "occupied"),
                new ComboBoxOption("Trống", "available")
        ));
    }

    //    Header Buton ---------------------------------------------------------
    public void changeToHomePage(ActionEvent event) throws Exception {
        FXMLLoader loader = SceneNavigator.switchScene("/fxml/home-page.fxml"
                , "/styles/home-page.css", event, true);

        HomePageController controller = loader.getController();
        controller.initialize(role, username);
    }

    public void changeToResidents(ActionEvent event) throws Exception {
        FXMLLoader loader = SceneNavigator.switchScene("/fxml/residents.fxml", "/styles/residents.css",
                event, true);

        ResidentsController controller = loader.getController();
        controller.initialize(role, username);
    }

    public void changeToRevenues(Event event) throws Exception {
        FXMLLoader loader = SceneNavigator.switchScene("/fxml/revenues.fxml", "/styles/revenues.css",
                event, true);

        RevenuesController controller = loader.getController();
        controller.initialize(role, username);
    }

    public void changeToPayments(ActionEvent event) throws Exception {
        FXMLLoader loader = SceneNavigator.switchScene("/fxml/payments.fxml", "/styles/payments.css",
                event, true);

        PaymentsController controller = loader.getController();
        controller.initialize(role, username);
    }

    //  Pop-up Button Cài đặt --------------------------------------------------
    public void changeToSignUp() {
        try {
            Stage owner = StageManager.getPrimaryStage();
            SceneNavigator.showPopupScene("/fxml/create-account.fxml",
                    "/styles/sign-in-create-account.css", owner);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeToSignIn(ActionEvent event) throws Exception {
        SceneNavigator.switchScene("/fxml/sign-in.fxml", "/styles/sign-in-create-account.css",
                event, false);
    }

    //    Body -----------------------------------------------------------------
    private void openRoomDetailScene(Event event, String roomName) throws IOException {
        FXMLLoader loader = SceneNavigator.switchScene("/fxml/room-detail.fxml", "/styles/room-detail.css",
                event, true);

        RoomDetailController controller = loader.getController();
        controller.initialize(role, username, roomName);
    }
    
    private void loadRoomsFromDatabase() {
        roomList.clear();
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM rooms ORDER BY floor ASC, room_number ASC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Rooms room = new Rooms(
                        rs.getString("room_number"),
                        rs.getInt("floor"),
                        rs.getFloat("area"),
                        rs.getString("status")
                );
                roomList.add(room);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Không thể tải danh sách phòng từ CSDL.");
        }
    }

    private void setupSearchAndFilter() {
        // Lọc khi nhập tên phòng
        roomField.textProperty().addListener((observable, oldValue, newValue) -> filterRooms());

        // Lọc khi chọn tầng
        floorBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> filterRooms());

        // Lọc khi chọn trạng thái
        statusBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> filterRooms());
    }

    private void filterRooms() {
        String roomName = roomField.getText().trim().toLowerCase();
        String selectedFloor = (floorBox.getValue() != null) ? (floorBox.getValue().getValue()) : null;
        String selectedStatus = (statusBox.getValue() != null) ? (statusBox.getValue().getValue()) : null;

        filteredRoomList.clear();

        for (Rooms room : roomList) {
            boolean matches = true;

            // Lọc theo tên phòng (số phòng)
            if (!roomName.isEmpty() && !room.getRoomNumber().toLowerCase().contains(roomName)) {
                matches = false;
            }

            // Lọc theo tầng - bỏ qua nếu chọn "Tất cả" hoặc chưa chọn gì
            if (selectedFloor != null && !selectedFloor.equals("Tất cả") && !(room.getFloor() == Integer.parseInt(selectedFloor))) {
                matches = false;
            }

            // Lọc theo trạng thái - bỏ qua nếu chọn "Tất cả" hoặc chưa chọn gì
            if (selectedStatus != null && !selectedStatus.equals("Tất cả") && !room.getStatus().equals(selectedStatus)) {
                matches = false;
            }

            if (matches) {
                filteredRoomList.add(room);
            }
        }

        // Cập nhật lại GridPane
        populateRoomsGrid();
    }

    //    Hiển thị grid child
    private void populateRoomsGrid() {
        roomsGrid.getChildren().clear();
        roomsGrid.getRowConstraints().clear();
        int column = 0, row = 0;

        for (Rooms room : filteredRoomList) {
            // Tạo VBox cho mỗi phòng
            VBox roomBox = new VBox(20);
            roomBox.setAlignment(Pos.CENTER);
            roomBox.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 5, 0.1, 0, 2);");
            roomBox.setPrefHeight(188.0);
            roomBox.setPrefWidth(410.0);

            // Tạo Label cho tên phòng
            Label roomLabel = new Label("Phòng " + room.getRoomNumber());
            roomLabel.setAlignment(Pos.CENTER);
            roomLabel.setPrefWidth(350.0);
            roomLabel.setPrefHeight(70.0);
            roomLabel.setStyle("-fx-background-color: #586995; -fx-background-radius: 15; -fx-font-weight: bold");
            roomLabel.setTextFill(Color.WHITE);
            roomLabel.setFont(new Font(36.0));

            // Tạo HBox chứa trạng thái và nút chi tiết
            HBox statusBox = new HBox(20);
            statusBox.setAlignment(Pos.CENTER);
            statusBox.setPrefHeight(40.0);
            statusBox.setPrefWidth(410.0);

            Label statusLabel = new Label("Trạng thái: " + (room.getStatus().equals("occupied") ? "Đang ở" : "Trống"));
            statusLabel.setAlignment(Pos.CENTER);
            statusLabel.setPrefHeight(40.0);
            statusLabel.setPrefWidth(210.0);
            statusLabel.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 10; -fx-border-color: #586995; -fx-border-radius: 10;");
            statusLabel.setFont(new Font(18.0));

            Button detailButton = new Button("Chi tiết");
            detailButton.setAlignment(Pos.CENTER);
            detailButton.setPrefHeight(40.0);
            detailButton.setPrefWidth(120.0);
            detailButton.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 10; -fx-border-color: #586995; -fx-border-radius: 10; -fx-cursor: hand");
            detailButton.setFont(new Font(18.0));
            detailButton.setOnMouseEntered(e -> {
                detailButton.setStyle("-fx-background-color: #E0E0E0; -fx-background-radius: 10; -fx-border-color: #586995; -fx-border-radius: 10; -fx-cursor: hand; -fx-translate-y: -0.5px;");
            });
            detailButton.setOnMouseExited(e -> {
                detailButton.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 10; -fx-border-color: #586995; -fx-border-radius: 10; -fx-cursor: hand");
            });
            detailButton.setOnMousePressed(e -> {
                detailButton.setStyle("-fx-background-color: #D6D6D6; -fx-background-radius: 10; -fx-border-color: #586995; -fx-border-radius: 10; -fx-cursor: hand; -fx-translate-y: 1px;");
            });
            detailButton.setOnAction(event -> {
                try {
                    openRoomDetailScene(event, room.getRoomNumber());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            statusBox.getChildren().addAll(statusLabel, detailButton);
            roomBox.getChildren().addAll(roomLabel, statusBox);

            // Thêm roomBox vào GridPane
            roomsGrid.add(roomBox, column, row);
            column++;
            if (column == 3) { // 3 cột mỗi hàng
                column = 0;
                row++;
            }
        }
    }

    private void showErrorAlert(String message) {
        try {
            CustomAlert.showErrorAlert(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}