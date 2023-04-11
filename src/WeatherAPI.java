// Java dependencies
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

// External library dependencies for JSON-Simple
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

// Class for connecting to the OpenWeather API and requesting current weather data
public class WeatherAPI {
    private final String city = ""; // <- city name, replace spaces with "+"
    private final String apiKey = ""; // <- API key
    private final String weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=" + apiKey;

    public JSONObject weatherData;

    WeatherAPI() {
        getWeatherData();
    }

    public void getWeatherData() {
        
        try {
            // Connect to the API endpoint
            URL url = new URL(weatherUrl);

            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.connect();

            // Get response code
            int responseCode = con.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }
            else {
                // Read and store response data
                String line = "";
                Scanner scanner = new Scanner(url.openStream());

                while (scanner.hasNext()) {
                    line += scanner.nextLine();
                }
                scanner.close();

                // Parse response data into json object
                JSONParser parser = new JSONParser();
                weatherData = (JSONObject)parser.parse(line);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Returns the String for Conditions
    public String getConditions() {
        
        JSONArray weatherArray = (JSONArray)weatherData.get("weather");
        JSONObject weatherWeather = (JSONObject)weatherArray.get(0);
        return weatherWeather.get("main").toString();
    }

    // Returns the String value for Temperature
    public String getTemperature() {
        
        JSONObject weatherMain = (JSONObject)weatherData.get("main");
        return weatherMain.get("temp").toString();
    }

    // Returns the String value for FeelsLike    
    public String getFeelsLike() {
        
        JSONObject weatherMain = (JSONObject)weatherData.get("main");
        return weatherMain.get("feels_like").toString();
    }

}
