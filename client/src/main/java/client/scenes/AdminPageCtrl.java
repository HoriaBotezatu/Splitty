package client.scenes;

import client.utils.ServerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import commons.Event;
import commons.Person;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class AdminPageCtrl {

    private final MainCtrl mainCtrl;
    private final ServerUtils server;

    private ObservableList<Event> data;

    @FXML
    private TableView<Event> events;

    @FXML
    private TableColumn<Event, String> titleColumn;
    @FXML
    private TableColumn<Event, Date> creationDateColumn;
    @FXML
    private TableColumn<Event, LocalDateTime> lastModifiedColumn;

    @FXML
    private Button deleteEvent;

    @FXML
    private Button downloadEvent;

    @FXML
    private Button importEvent;

    @FXML
    private Label selectedEvent;

    /**
     * Constructor for the admin page controller.
     *
     * @param server   the server
     * @param mainCtrl the main controller
     */
    @Inject
    public AdminPageCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        data = FXCollections.observableList(new ArrayList<>());
    }

    /**
     * Gets all the events from the server and adds them to the tableview.
     */
    public void showEvents() {
        events.getItems().clear();
        var e = server.getEvents();
        if (e != null) {
            data = FXCollections.observableList(e);
            events.setItems(data);
        }
    }

    @FXML
    void initialize() {
        //set up the columns in the table
        titleColumn.setCellValueFactory(new PropertyValueFactory<Event, String>("title"));
        creationDateColumn.setCellValueFactory(new PropertyValueFactory<Event, Date>("creationDate"));
        lastModifiedColumn.setCellValueFactory(new PropertyValueFactory<Event, LocalDateTime>("lastModified"));
        server.registerForMessages("/topic/event", Person.class, event -> {
            Platform.runLater(this::showEvents);
        });
    }

    /**
     * Changes the selected event label to the event title.
     */
    void changeSelectedEvent() {
        events.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Event>() {
            @Override
            public void changed(ObservableValue<? extends Event> observable, Event oldValue, Event newValue) {
                if (newValue != null) {
                    selectedEvent.setText(newValue.getTitle());
                }
            }
        });
    }

    public void joinEventsList() {
        events.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                    try {
                        Event event = events.getSelectionModel().selectedItemProperty().get();
                        mainCtrl.showEventPage(event.getId());
                    }
                    catch (Error e) {
                        mainCtrl.showAlert("This event doesn't exist anymore!");
                    }
                }
            }
        });
    }

    public void delete() {
        Event event = events.getSelectionModel().getSelectedItem();
        if (event == null) {
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete");
        alert.setContentText("Are you sure you want to delete event: " + event.getTitle());
        alert.showAndWait().filter(ButtonType.OK::equals).ifPresent(b -> {
            server.deleteEventById(event, event.getId());
            clear();
            showEvents();
        });
        alert.close();
    }

    public void goBack() {
        mainCtrl.showAdminLogin();
    }

    public void setLanguageText(ResourceBundle resourceBundle) {

    }

    public void downloadEvent() {
        Event event = events.getSelectionModel().getSelectedItem();
        if (event == null) {
            return;
        }
        //object mapper to write object to json
        ObjectMapper obj = new ObjectMapper().registerModule(new JavaTimeModule());
        obj.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        ObjectWriter writer = obj.writerWithDefaultPrettyPrinter();

        //file chooser to let user choose download destination
        FileChooser fileChooser = new FileChooser();
        Window parent = new Stage();
        fileChooser.setInitialFileName(event.getTitle() + ".json");
        FileChooser.ExtensionFilter extensionFilter =
                new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extensionFilter);

        File saveFile = fileChooser.showSaveDialog(parent);
        try {
            if (saveFile != null) {
                writer.writeValue(saveFile, event);
                System.out.println("File saved!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importEvent() {
        Window parent = new Stage();
        FileChooser chooser = new FileChooser();
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        FileChooser.ExtensionFilter extensionFilter =
                new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        chooser.getExtensionFilters().add(extensionFilter);
        File file = chooser.showOpenDialog(parent);
        try {
            Event event = objectMapper.readValue(file, Event.class);
            System.out.println(event);
            server.addEvent(event);
            showEvents();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() {
        events.getItems().clear();
    }

}
