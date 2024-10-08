/**
 * Sample Skeleton for 'StarterPage.fxml' Controller Class
 */

package client.scenes;

import java.net.URL;
import java.util.*;

import client.utils.LanguageManager;
import client.utils.ServerUtils;
import commons.Event;
import jakarta.ws.rs.BadRequestException;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import com.google.inject.Inject;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.tuple.Pair;

public class StarterPageCtrl implements Initializable {

    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    private final LanguageManager languageManager;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="createButton"
    private Button createButton;

    @FXML // fx:id="createTextLabel"
    private Label createTextLabel;

    @FXML // fx:id="createTextField"
    private TextField createTextField;

    @FXML // fx:id="joinEventText"
    private Label joinEventText;

    @FXML // fx:id="joinButton"
    private Button joinButton;

    @FXML // fx:id="joinTextField"
    private TextField joinTextField;

    @FXML // fx:id="recentlyViewedText"
    private Label recentlyViewedText;

    @FXML // fx:id="languageSelector"
    private ComboBox languageSelector; // Value injected by FXMLLoader

    @FXML // fx:id="listView"
    private ListView<Event> listView;

    @FXML
    private Button adminbutton;

    @FXML
    private Button settingButton;

    private List<Event> recentEvents;

    /**
     * Constructor for the StarterPageCtrl class.
     *
     * @param mainCtrl - the main controller
     * @param server
     */
    @Inject
    public StarterPageCtrl(MainCtrl mainCtrl, ServerUtils server, LanguageManager languageManager) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.languageManager = languageManager;
        recentEvents = new ArrayList<>();
    }

    @FXML
    void adminLogin(){
        mainCtrl.showAdminLogin();
    }

    /**
     * Function to add to the listView item.
     */
    public void addListView() {
        List<Event> events = new ArrayList<>();
        for (Event e: events) {
            listView.getItems().add(e);
        }
    }

    /**
     * Delete method for the list view.
     * @param e - event to be deleted from the list view.
     * @return - the event that has been deleted.
     */
    public Event deleteListView(Event e) {
        if (e == null) {
            return null;
        }
        listView.getItems().remove(e);
        return e;
    }

    /**
     * Method to return the number of elements in the listView.
     * @return - the number of elements in the listView.
     */
    public int numberOfElements() {
        return listView.getItems().size();
    }


    /**
     * Method that changes the primary stage to the event page.
     */
    public void showEventPage() {
        String name = createTextField.getText();
        if(name.equals("")) {name = "New Event";}
        Event event = server.addEvent(new Event("", name, 0, generateToken(), new ArrayList<>(), new ArrayList<>()));
        recentEvents.add(event);
        listView.getItems().add(event);
        mainCtrl.showEventPage(event.getId());
    }

    /**
     * Generates a token for the event.
     */
    protected String generateToken(){
        String allChars = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String token = "";
        while (token.length() < 5){
            Random random = new Random();
            int charIndex = random.nextInt(allChars.length());
            token += allChars.charAt(charIndex);
        }
        try
        {
            server.getEventByToken(token);
        }
        catch (BadRequestException e){
            return token;
        }
        return generateToken();
    }

    /**
     * Method that allows you to join an event using a code.
     */
    public void joinEvent() {
        String token = joinTextField.getText();
        try {
            server.getEventByToken(token);
            if (!listView.getItems().contains(server.getEventByToken(token))) {
                recentEvents.add(server.getEventByToken(token));
                listView.getItems().add(server.getEventByToken(token));
            }
            mainCtrl.showEventPage(server.getEventByToken(token).getId());
        }
        catch (Exception e){
            // System.out.println("This event doesn't exist");
            mainCtrl.showAlert(languageManager.getResourceBundle().getString("error.event.doesNotExist"));
        }
    }

    /**
     * Translates the sharecode back to the id, needed to join an event via sharecode.
     * @param shareCode the sharecode to translate to the id.
     * @return the id of the event.
     */
    public int translateShareCode(String shareCode){
        //String hardCodedShareCode = inviteCode.getText();
        int size = shareCode.length();
        String result = "";
        for( int i =0; i < size; i++){
            int number = shareCode.charAt(i);
            number = number - 65;
            result += number;
        }
        int total = Integer.parseInt(result);
        total = total/3000929;
        System.out.println("Translated from the sharecode, eventID = " + total);
        return total;
    }

    /**
     * list of all the events that have been joined with this token.
     */
    public void joinEventsList() {
        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                    try {
                        String token = listView.getSelectionModel().getSelectedItem().getToken();
                        Event event = server.getEventByToken(token);
                        mainCtrl.showEventPage(event.getId());
                    }
                    catch (Error e) {
                        mainCtrl.showAlert(languageManager.getResourceBundle().getString("error.event.nonexistent"));
                    }
                }
            }
        });
    }

    /**
     * updates the flag the user chooses.
     */
    public void updateLanguage() {
        // Show current language
        Pair<String, Image> currentLanguage = languageManager.getLanguage();
    }

    /**
     * sets all the text in the file to the correct language chosen by the user.
     * @param resourceBundle the language.
     */
    public void setLanguageText(ResourceBundle resourceBundle) {
        createTextLabel.setText(resourceBundle.getString("create.event"));
        createButton.setText(resourceBundle.getString("create.button"));
        joinButton.setText(resourceBundle.getString("join.button"));
        joinEventText.setText(resourceBundle.getString("join.event"));
        recentlyViewedText.setText(resourceBundle.getString("recently.viewed"));
        createTextField.setPromptText(resourceBundle.getString("create.event.placeholder"));
        joinTextField.setPromptText(resourceBundle.getString("join.event.placeholder"));
    }

    /**
     * acts on the keyevent.
     * @param e the keyevent that happened.
     */
    public void keyPressed(KeyEvent e) {
        TextField source = (TextField) e.getSource();
        if (e.getCode() == KeyCode.ENTER) {
            if (source == createTextField) {
                showEventPage();
            } else if (source == joinTextField) {
                joinEvent();
            }
        }
    }

    /**
     * shows settings page after clicking the settings icon.
     */
    public void showSettingsPage(){
        mainCtrl.showStartSettings();
    }

    /**
     * refreshes the list of recent events.
     */
    void refreshListView() {
        listView.getItems().clear();
        for (Event e : recentEvents) {
            listView.getItems().add(e);
        }
    }

    /**
     * initializes the language in the language dropdown menu.
     */
    void initializeLanguages() {
        server.initializeLanguages(languageSelector);
    }

    /**
     * sets the correct language chosen by the user.
     */
    public void setLanguageSelector() {
        server.setLanguageSelector(languageSelector);
    }

    /**
     * initializes the buttons and pages corresponding.
     * @param url the event URL.
     * @param resourceBundle the language.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listView.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    try {
                        String token = listView.getSelectionModel().getSelectedItem().getToken();
                        Event event = server.getEventByToken(token);
                        mainCtrl.showEventPage(event.getId());
                    }
                    catch (Error e) {
                        mainCtrl.showAlert(languageManager.getResourceBundle().getString("error.event.nonexistent"));
                    }
                }
            }
        });
        server.registerForMessages("/topic/event", Object.class, object-> {
            System.out.println("This is also activated");
            for (Event e : recentEvents) {
                e.setTitle(server.getEventByID(e.getId()).getTitle());
            }
            refreshListView();
        });
    }
}
