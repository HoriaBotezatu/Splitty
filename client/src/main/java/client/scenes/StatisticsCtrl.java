package client.scenes;

import client.utils.LanguageSingleton;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Transaction;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class StatisticsCtrl implements Initializable {
    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    @FXML // fx:id="statsPieChart"
    private PieChart statsPieChart;

    @FXML // fx:id="statsTotalExpenses"
    private Label statsTotalExpenses;
    @FXML // fx:id="goBackButton"
    private Button goBackButton; // Value injected by FXMLLoader

    private Event selectedEvent;

    /**
     * Constructor for the statistics controller.
     * @param mainCtrl - reference to the main controller.
     */
    @Inject
    public StatisticsCtrl(MainCtrl mainCtrl, ServerUtils server) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    public void initializeStatistics() {
        assert statsPieChart != null : "fx:id=\"statsPieChart\" was not injected: check your FXML file 'Statistics.fxml'.";
        assert statsTotalExpenses != null : "fx:id=\"statsTotalExpenses\" was not injected: check your FXML file 'Statistics.fxml'.";

        Event selectedEvent = server.getEventByID(mainCtrl.getCurrentEventID());

        if (selectedEvent == null) {
            statsTotalExpenses.setText(LanguageSingleton.getInstance().getResourceBundle().getString("error.server.eventNotFound"));
        } else {
            List<Transaction> transactions = selectedEvent.getTransactions();

            double totalExpenses = 0.0;
            Map<String, Double> expensesData = new HashMap<>();

            for (Transaction transaction : transactions) {
                if (transaction.isHandOff()) {
                    totalExpenses += transaction.getMoney();

                    double currentTotal;
                    if (expensesData.get(transaction.getExpenseType()) == null) {
                        currentTotal = 0.0;
                    }
                    else {
                        currentTotal = expensesData.get(transaction.getExpenseType());
                    }

                    expensesData.put(transaction.getExpenseType(), currentTotal + transaction.getMoney());
                }
            }

            ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
            for (Map.Entry<String, Double> entry : expensesData.entrySet()) {
                chartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }

            statsPieChart.setData(chartData);

            String totalExpensesString = (LanguageSingleton.getInstance().getResourceBundle().getString("total.expenses"));
            statsTotalExpenses.setText(totalExpensesString + " " + totalExpenses + " EUR");
        }
    }

    public void setLanguageText(ResourceBundle resourceBundle) {

    }

    /**
     * Method for the go back button in the statistics page.
     */
    public void goBack() {
        mainCtrl.showEventPage(mainCtrl.getCurrentEventID());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        server.registerForMessages("/topic/event", Object.class, object -> {
            if (mainCtrl.getCurrentEventID() != 0) {
                if (checkNewData()) {
                    Platform.runLater(this::initializeStatistics);
                }
            }
        });
    }


    public boolean checkNewData() {
        Event currentEvent = server.getEventByID(mainCtrl.getCurrentEventID());
        return currentEvent.getTransactions() != selectedEvent.getTransactions();
    }
}
