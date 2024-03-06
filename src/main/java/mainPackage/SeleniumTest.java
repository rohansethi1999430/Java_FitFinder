package mainPackage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;

public class SeleniumTest {
    public static void main(String[] args) {
        // Set the path to the msedgedriver executable
        System.setProperty("webdriver.edge.driver", "/Users/rohansethi/Downloads/FitFinder (2) 2/FitFinder/FitFinder/msedgedriver");

        // Initialize the EdgeDriver
        WebDriver driver = new EdgeDriver();

        // Open a URL
        driver.get("https://www.example.com");

        // Get the title of the page
        System.out.println("Page title is: " + driver.getTitle());

        // Close the browser
        driver.quit();
    }
}