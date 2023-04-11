// Java dependencies
import java.io.IOException;

// External library dependencies for JavaFX
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.stage.Stage;

public class App extends Application {
    
    @FXML LineChart<String, Number> TemperatureChart;
    @FXML LineChart<String, Number> HighLowChart;
    @FXML PieChart ConditionsChart;

    // Returns an XYChart.Series for the requested category of data up to the current day of the month
    public static XYChart.Series<String, Number> getDataSeries(String dataCategory, String seriesName, DataHandler dh) {
        
        XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
        series.setName(seriesName);
        
        for (int i = 1; i <= dh.getDayOfMonth(); i++) {
            series.getData().add(new XYChart.Data<String, Number>(String.valueOf(i), dh.getData(dataCategory, i)));
        }
        return series;
    }

    // Returns an ObservableList of pie chart data
    public static ObservableList<PieChart.Data> getPieChartData(DataHandler dh) {
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList(
            new PieChart.Data("Clouds", dh.countConditions("Clouds")),
            new PieChart.Data("Snow", dh.countConditions("Snow")),
            new PieChart.Data("Rain", dh.countConditions("Rain")),
            new PieChart.Data("Drizzle", dh.countConditions("Drizzle")),
            new PieChart.Data("Thunderstorm", dh.countConditions("Thunderstorm")),
            new PieChart.Data("Clear", dh.countConditions("Clear"))
        );
        return chartData;
    }


    // Defines JavaFX stage and scene and loads FXML GUI
    @Override
    public void start(Stage stage) throws IOException {
        
        DataHandler dh = new DataHandler();
       
        // Load FXML GUI
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DataVisualizer.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        
        // Setting up charts
        TemperatureChart.setTitle("Temperature Data for " + dh.getActiveMonth());
        TemperatureChart.getXAxis().setLabel("Day of the Month");
        TemperatureChart.getYAxis().setLabel("Temperature (C)");
        TemperatureChart.setCreateSymbols(false);
        TemperatureChart.getData().add(getDataSeries("Temperature", "Temp.", dh));
        TemperatureChart.getData().add(getDataSeries("FeelsLike", "Feels Like", dh));

        HighLowChart.setTitle("High and Low Temperatures for " + dh.getActiveMonth());
        HighLowChart.getXAxis().setLabel("Day of the Month");
        HighLowChart.getYAxis().setLabel("Temperature (C)");
        HighLowChart.setCreateSymbols(false);
        HighLowChart.getData().add(getDataSeries("High", "Highest Temp.", dh));
        HighLowChart.getData().add(getDataSeries("Low", "Lowest Temp.", dh));

        ConditionsChart.setTitle("Weather Conditions for " + dh.getActiveMonth());
        ConditionsChart.setLegendSide(Side.LEFT);
        ConditionsChart.setData(getPieChartData(dh));
        
        // Set up scene and stage
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Weather Data V1.0");
        stage.show();
    }

    public static void main(String[] args) {
        
        launch(args);
    }
}
