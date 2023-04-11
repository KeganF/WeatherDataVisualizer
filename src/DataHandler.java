// Dependencies
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Scanner;

// Class for reading, writing, and updating CSV files with weather data
public class DataHandler {
    
    final private String pathPrefix = "src/dataFiles/";
    final private int dataCategoriesCount = 6;
    private String[][] dataSet;

    DataHandler() {
        
        dataSet = new String[dataCategoriesCount][getDaysInMonth() + 1];
        
        if (getDayOfMonth() != 1) {
            System.out.println("Today is not the 1st of the month. No file will be created.");
        }
        else {
            createNewFile();
        }
        
        dataSet = populateDataSet(dataSet, getActiveFilepath());
        dataSet = updateCurrentData(dataSet);
    }


    // Returns an int for the number of days in the current month
    public int getDaysInMonth() {

        LocalDate today = LocalDate.now();
        return today.lengthOfMonth();
    }


    // Returns an int for the current day of the month
    public int getDayOfMonth() {
        
        LocalDate today = LocalDate.now();
        return today.getDayOfMonth();
    }


    // Returns a String for the filepath to the csv file for the active month of weather data
    public String getActiveFilepath() {
        
        LocalDate today = LocalDate.now();
        return pathPrefix + today.getMonth().toString() + "_" + today.getYear() + ".csv";
    }


    // Returns a String for the name of the month
    public String getActiveMonth() {
        
        LocalDate today = LocalDate.now();
        return today.getMonth().toString();
    }


    // Creates a new file to store weather data at the start of the month
    public void createNewFile() {
        
        try {
            LocalDate date = LocalDate.now();
            String filepath = pathPrefix + date.getMonth().toString() + "_" + date.getYear();
            File file = new File(filepath);

            if (file.createNewFile()) {
                System.out.println("A new file has been created for " + filepath + ".");
                initFile(filepath);
            }
        }
        catch (IOException e) {
            System.out.println("Something went wrong creating a new file.");
            e.printStackTrace();
        }
    }


    // Initializes a new file with default values
    public void initFile(String filepath) {
        
        try {
            FileWriter writer = new FileWriter(filepath);
            
            writer.write("Day,Conditions,Temperature,FeelsLike,High,Low\n");
            for (int i = 1; i <= getDaysInMonth(); i++) {
                writer.write(i + ",0,0,0,0,0\n");
            }
            writer.close();
            
            System.out.println("Successfully initialized the new file " + filepath + ".");
        }
        catch(IOException e) {
            System.out.println("Something went wrong initializing the new file " + filepath + ".");
            e.printStackTrace();
        }
    }


    // Fills the data set with the values from the csv file
    public String[][] populateDataSet(String[][] dataSet, String filepath) {
        
        try {
            File dataFile = new File(filepath);
            Scanner scanner = new Scanner(dataFile);
        
            // Empty string to hold unformatted data from the csv file
            String dataString = "";
            // Array to hold the segments from dataString after being split 
            String[] dataSegments;

            // Reads all data from the csv file into dataString
            while (scanner.hasNextLine()) {
                dataString += scanner.nextLine() + "\n";
            }
            scanner.close();

            // Separates each value within dataString by splitting at , and \n
            dataSegments = dataString.split(",|\n");

            // Formats the elements of dataSegments to a 2D array for the final data set
            int index = 0;
            for (int i = 0; i < getDaysInMonth() + 1; i++) {
                for (int j = 0; j < dataCategoriesCount; j++) {
                    dataSet[j][i] = dataSegments[index];
                    index++;
                }
            }

            System.out.println("Successfully populated the dataset from " + filepath + ".");
        }
        catch(FileNotFoundException e) {
            System.out.println("Something went wrong reading from " + filepath + ".");
            e.printStackTrace();
        }

        return dataSet;
    }


    // Updates the dataset with the current weather data
    public String[][] updateCurrentData(String[][] dataSet) {
        
        WeatherAPI w = new WeatherAPI();

        // Set the values for the current day with the current weather data
        for (int i = 1; i <= getDaysInMonth(); i++) {
            if (Integer.parseInt(dataSet[0][i]) == getDayOfMonth()) {
                dataSet[1][i] = w.getConditions();
                dataSet[2][i] = w.getTemperature();
                dataSet[3][i] = w.getFeelsLike();

                // Setting initial high and low values if they haven't been recorded yet
                if (dataSet[4][i].equals("0") && dataSet[5][i].equals("0")) {
                    dataSet[4][i] = w.getTemperature();
                    dataSet[5][i] = w.getTemperature();
                }
                // Updating high and low values if they have already been recorded
                else {
                    String high = dataSet[4][i];
                    String low = dataSet[5][i];
                    String temp = w.getTemperature();
                    // TODO: Record the time of the day the highest and lowest values were recorded?
                    // TODO: Display the times and temperatures when hovering over the data on the chart
                    if (Float.parseFloat(temp) > Float.parseFloat(high)) {
                        dataSet[4][i] = temp;
                    }
                    else if (Float.parseFloat(temp) < Float.parseFloat(low)) {
                        dataSet[5][i] = temp;
                    }
                }
            }
        }
        writeDataSetToFile(getActiveFilepath());

        return dataSet;
    }


    // Overwrites the csv file with the values from the dataset after being updated with new weather data
    public void writeDataSetToFile(String filepath) {

        try {
            FileWriter writer = new FileWriter(filepath);

            for (int i = 0; i < getDaysInMonth() + 1; i++) {
                for (int j = 0; j < dataCategoriesCount; j++) {
                    writer.write(dataSet[j][i]);

                    // Inserts a , after each value in a row except the last
                    if (j < 5) {
                        writer.write(",");
                    }
                }
                // Inserts a new line after each row of values
                writer.write("\n");
            }
            writer.close();
            
            System.out.println("Successfully wrote to file " + filepath + ".");
        }
        catch (IOException e) {
            System.out.println("Something went wrong writing to " + filepath + ".");
            e.printStackTrace();
        }
    }


    // Returns an integer for the number of occurences for a given weather condition throughout the week
    public int countConditions(String conditions) {

        int count = 0;

        for (int i = 1; i <= getDaysInMonth(); i++) {
            if (dataSet[1][i].equals(conditions)) {
                count++;
            }
        }
        
        return count;
    }


    // Returns data from the requested category at the requested day
    public float getData(String dataCategory, int day) {
    
        // TODO: Possibly change the way the data from the csv is stored so that categories don't have to be resolved to a number? (Key Values Pairs?)
        int categoryId = 0;
        switch(dataCategory) {
            case "Temperature":
                categoryId = 2;
                break;
            case "FeelsLike":
                categoryId = 3;
                break;
            case "High":
                categoryId = 4;
                break;
            case "Low":
                categoryId = 5;
                break;
        }

        float data = 0;

        for (int i = 1; i <= getDaysInMonth(); i++) {
            if (Integer.parseInt(dataSet[0][i]) == day) {
                data = Float.parseFloat(dataSet[categoryId][i]);
            }
        }
        
        return data;
    }
}


