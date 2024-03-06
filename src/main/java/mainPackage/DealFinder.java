package mainPackage;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DealFinder {

  private Utilities utl = new Utilities();

  public void webScrapper(String cityName) {

    System.setProperty("webdriver.edge.driver", "/Users/rohansethi/Downloads/FitFinder (2) 2/FitFinder/FitFinder/msedgedriver");
    WebDriver edgeWebDriver = new EdgeDriver();

    //minimized edge browser is being enlarged
    edgeWebDriver.manage().window().maximize();

    //Implicit Wait is implemented to avoid getting captcha from website
    edgeWebDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));

    JavascriptExecutor js = (JavascriptExecutor) edgeWebDriver;

    edgeWebDriver.get("https://www.fitnessworld.ca/explore-memberships/");     

    String userInput = cityName;

    WebDriverWait wait = new WebDriverWait(edgeWebDriver,Duration.ofSeconds(50) );

    // Specify the locator strategy (e.g., by ID, by XPath, etc.) for the element you're waiting for
    By elementLocator = By.className("close-popup");

    try {
        // Wait until the element is present on the page
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(elementLocator));
        
        By closeButtonAvailability = By.className("close-popup");
        //Email Signup popup is being selected

        if (isElementPresent(edgeWebDriver, closeButtonAvailability)) {
          //condition checks whether the button is present on screen or not
          WebElement closeButton = edgeWebDriver.findElement(By.className("close-popup"));
          //if button is present on screen than it clicked and pop is closed
          closeButton.click();
          System.out.println("Email Signup popup Cancelled clicked successfully!");
        }
    } 
    catch (Exception e) {  
        //System.out.println("Element not found within the specified time.");
    }  

    WebElement parentElement = edgeWebDriver.findElement(By.className("club-container"));;

    // Scroll to the specific element
    js.executeScript("arguments[0].scrollIntoView(true);", parentElement);

    List < WebElement > maincontainers = edgeWebDriver.findElements(By.className("club-option__benefits"));

    String ftcsvFilePath = "ftoutput.csv";

    try (FileWriter ftcsvWriter = new FileWriter(ftcsvFilePath)) {
      ftcsvWriter.write("Fitness World"+"\n");
      for (WebElement childEle: maincontainers) {

        String text = childEle.findElement(By.tagName("h3")).getText().trim();
        text=text.replaceAll("BI-WEEKLY", "Per month");
  		text=text.replaceAll("$", "");
        ftcsvWriter.write(text);

        List < WebElement > texts = childEle.findElements(By.tagName("span"));
        for (WebElement insidetext: texts) {
          //System.out.println(insidetext.getText());
          ftcsvWriter.write(System.getProperty("line.separator") + insidetext.getText().trim());

        }
        ftcsvWriter.append("\n");
        ftcsvWriter.write(System.getProperty("line.separator"));
      }

      WebElement searchElement = edgeWebDriver.findElement(By.id("address"));;
      // Scroll to the specific element
      js.executeScript("arguments[0].scrollIntoView(true);", searchElement);
      //String input="vancouver";
      searchElement.sendKeys(userInput);

      WebElement FtLocationContainer = edgeWebDriver.findElement(By.className("join__locations"));
      List < WebElement > Fit4LessLocations = FtLocationContainer.findElements(By.tagName("a"));

      Queue < String > ftLinkstoVisit = new LinkedList < > ();
      int linksCount = 0;
      for (WebElement ele: Fit4LessLocations) {
        String link = ele.getAttribute("href");
        if (linksCount < 3) {
          ftLinkstoVisit.add(link);
          linksCount++;
        }
      }
      while (!ftLinkstoVisit.isEmpty()) {
        edgeWebDriver.get(ftLinkstoVisit.poll());
        WebElement fttitle = edgeWebDriver.findElement(By.className("title"));
        WebElement ftAddress = edgeWebDriver.findElement(By.className("address"));
        WebElement ftphone = edgeWebDriver.findElement(By.className("phone"));
        ftcsvWriter.write(fttitle.getText() + ",");
        ftcsvWriter.write(ftAddress.getText() + ",");
        ftcsvWriter.write(ftphone.getText());
        ftcsvWriter.write(System.getProperty("line.separator"));
        //System.out.println(fttitle.getText()+" "+ftAddress.getText()+" "+ftphone.getText());
        //edgeWebDriver.navigate().back();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    edgeWebDriver.get("https://www.goodlifefitness.com/membership.html");

    WebElement performanceLabel = edgeWebDriver.findElement(By.cssSelector("label[for='performance-membership']"));

    js.executeScript("arguments[0].scrollIntoView(true);", performanceLabel);

    WebElement ultimateLabel = edgeWebDriver.findElement(By.cssSelector("label[for='ultimate-membership']"));
    WebElement premiumLabel = edgeWebDriver.findElement(By.cssSelector("label[for='all-clubs-membership']"));

    WebElement GLMainContainer = edgeWebDriver.findElement(By.className("c-membership-types-container"));
    List < WebElement > prices = GLMainContainer.findElements(By.className("c-card--membership-price__dollar"));
    //List<WebElement> features=edgeWebDriver.findElements(By.className("c-list--checkmark__item"));

    List < WebElement > features = edgeWebDriver.findElements(By.cssSelector("tr.c-pricing-mobile__row"));

    String GLcsvFilePath = "GLoutput.csv";

    try (FileWriter GLcsvWriter = new FileWriter(GLcsvFilePath)) {
      //GLcsvWriter.append("GoodLife Fitness" + "\n");
      GLcsvWriter.append(premiumLabel.getText() + "," + ultimateLabel.getText() + "," + performanceLabel.getText() + "\n");

      StringBuilder rowString = new StringBuilder();
      for (WebElement price: prices) {
        if (!price.getText().trim().isEmpty())
          try {
            float result = convertStringToInt(price.getText() + ".99");
            rowString.append(result + " /per month" + ",");
            //        	            System.out.println("Converted integer: " + result);
          } catch (NumberFormatException e) {
            System.err.println("Error: Unable to convert the string to an integer.");
          }
      }
      GLcsvWriter.append(rowString.toString().replaceAll(",$", "")).append("\n");

      LinkedHashSet < String > unhighlightedList1 = new LinkedHashSet < > ();
      LinkedHashSet < String > HighlightedList = new LinkedHashSet < > ();
      LinkedHashSet < String > unhighlightedList2 = new LinkedHashSet < > ();
      for (WebElement eb: features) {
        List < WebElement > unhighlightedcell = eb.findElements(By.cssSelector("td.c-pricing-mobile__available"));
        WebElement highlightedcell = eb.findElement(By.cssSelector("td.col.c-pricing-mobile__available.highlight"));
        //System.out.println(unhighlightedcell.size()+",");

        List < WebElement > unhighlighted1 = unhighlightedcell.get(4).findElements(By.tagName("li"));
        for (WebElement cellun: unhighlighted1) {
          if (!cellun.getText().trim().isEmpty()) {
            //System.out.print("inside"+cellun.getText()+"\n");
            unhighlightedList1.add(cellun.getText());
          }
        }

        List < WebElement > unhighlighted2 = unhighlightedcell.get(6).findElements(By.tagName("li"));
        for (WebElement cellun: unhighlighted2) {
          if (!cellun.getText().trim().isEmpty()) {
            unhighlightedList2.add(cellun.getText());
          }
        }

        List < WebElement > highlighted = highlightedcell.findElements(By.tagName("li"));
        for (WebElement cellun: highlighted) {
          if (!cellun.getText().trim().isEmpty()) {
            HighlightedList.add(cellun.getText());
          }
        }
      }

      // Create iterators for each set
      Iterator < String > iterator1 = unhighlightedList1.iterator();
      Iterator < String > iterator2 = HighlightedList.iterator();
      Iterator < String > iterator3 = unhighlightedList2.iterator();
      while (iterator1.hasNext() || iterator2.hasNext() || iterator3.hasNext()) {
        // Write elements to the CSV file, using an empty string if the set is exhausted
        GLcsvWriter.append(iterator1.hasNext() ? iterator1.next() : "").append(",");
        GLcsvWriter.append(iterator2.hasNext() ? iterator2.next() : "").append(",");
        GLcsvWriter.append(iterator3.hasNext() ? iterator3.next() : "").append(",");
        GLcsvWriter.append("\n");
      }
      edgeWebDriver.get("https://www.goodlifefitness.com/clubs.html");
      WebElement LocationSearchInput = edgeWebDriver.findElement(By.id("club-search"));
      LocationSearchInput.sendKeys(userInput);

      try {
        // Sleep for 3 seconds (3000 milliseconds)
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        // Handle the exception if necessary
        e.printStackTrace();
      }

      WebElement LocationSearchButton = edgeWebDriver.findElement(By.className("c-search__button"));
      LocationSearchButton.findElement(By.tagName("button")).click();

      By GLLocationAvailability = By.id("js-card-list__0");
      //Email Signup popup is being selected

      if (isElementPresent(edgeWebDriver, GLLocationAvailability)) {
        try {
          // Sleep for 3 seconds (3000 milliseconds)
          Thread.sleep(10000);
        } catch (InterruptedException e) {
          // Handle the exception if necessary
          e.printStackTrace();
        }
        //condition checks whether the button is present on screen or not
        WebElement GLLocation1 = edgeWebDriver.findElement(By.id("js-card-list__0"));
        WebElement GLLocation2 = edgeWebDriver.findElement(By.id("js-card-list__1"));
        WebElement GLLocation3 = edgeWebDriver.findElement(By.id("js-card-list__2"));

        String Title1 = GLLocation1.findElement(By.cssSelector("h3.c-card__title")).getText();
        String Title2 = GLLocation2.findElement(By.cssSelector("h3.c-card__title")).getText();
        String Title3 = GLLocation3.findElement(By.cssSelector("h3.c-card__title")).getText();

        String Address1 = GLLocation1.findElement(By.className("c-card__contact")).getText();
        String Address2 = GLLocation2.findElement(By.className("c-card__contact")).getText();
        String Address3 = GLLocation3.findElement(By.className("c-card__contact")).getText();

        String Phone1 = GLLocation1.findElement(By.className("c-card__phone")).getText();
        String Phone2 = GLLocation2.findElement(By.className("c-card__phone")).getText();
        String Phone3 = GLLocation3.findElement(By.className("c-card__phone")).getText();

        Pattern pattern = Pattern.compile("(.+?)\\n");
        Matcher matcher1 = pattern.matcher(Address1);

        if (matcher1.find()) {
          Address1 = matcher1.group(1);
        } else {
          System.out.println("Pattern not found in the input string.");
        }

        Matcher matcher2 = pattern.matcher(Address2);
        if (matcher2.find()) {
          Address2 = matcher2.group(1);
        } else {
          System.out.println("Pattern not found in the input string.");
        }

        Matcher matcher3 = pattern.matcher(Address3);
        if (matcher3.find()) {
          Address3 = matcher3.group(1);
        } else {
          System.out.println("Pattern not found in the input string.");
        }

        GLcsvWriter.append(Title1 + "," + Address1 + "," + Phone1.trim() + "\n");
        GLcsvWriter.append(Title2 + "," + Address2 + "," + Phone2.trim() + "\n");
        GLcsvWriter.append(Title3 + "," + Address3 + "," + Phone3.trim() + "\n");

      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    //website is being fetched
    edgeWebDriver.get("https://www.planetfitness.ca/");

    String Price1 = edgeWebDriver.findElement(By.xpath("//*[@id=\"__next\"]/div/div/div[2]/div[2]/div/div[2]/div[1]/div/div/div[1]/div[3]/p[1]")).getText();
    String Price2 = edgeWebDriver.findElement(By.xpath("//*[@id=\"__next\"]/div/div/div[2]/div[2]/div/div[2]/div[2]/div/div/div[1]/div[3]/p[1]")).getText();

    WebElement inputElement = edgeWebDriver.findElement(By.xpath("//*[@id=\":R1lb5lel6:\"]"));
  //  WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=':R1lb5lel6:']")));
    inputElement.sendKeys(userInput);

    WebElement joinUsButton = edgeWebDriver.findElement(By.className("MuiButton-containedSecondary"));

    // Click the "Join Us" button
    joinUsButton.click();

    Queue < String > LinkstoVisit = new LinkedList < > ();
    WebElement mainDiv = edgeWebDriver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div"));

    // Find all elements inside the main div
    List < WebElement > elementsInsideDiv = mainDiv.findElements(By.cssSelector("*"));
    ArrayList < String > tabs = new ArrayList < > (edgeWebDriver.getWindowHandles());
    //System.out.println(edgeWebDriver.getWindowHandles());
    // Print the text of each element inside the main div
    int count = 0;
    for (WebElement element: elementsInsideDiv) {
      if ("a".equals(element.getTagName())) {
        // Print the link text
        //System.out.println("Link Text: " + element.getText());

        // Get the href attribute value (link URL)
        String linkUrl = element.getAttribute("href");
        //System.out.println("Link URL: " + linkUrl);

        // Check if the link opens in a new tab
        if (element.getText().equals("Club Details") && count < 3) {
          //System.out.println(linkUrl);
          LinkstoVisit.add(linkUrl);
          count++;
        }
      }

    }

    WebElement membershipsLink = edgeWebDriver.findElement(By.cssSelector("a[href='/gym-memberships']"));

    // Click the element
    membershipsLink.click();

    WebElement table = edgeWebDriver.findElement(By.cssSelector("table.MuiTable-root"));
    List < WebElement > rows = table.findElements(By.cssSelector("tbody tr"));
    String csvFilePath = "output.csv";

    try (FileWriter csvWriter = new FileWriter(csvFilePath)) {
      // Write header
      csvWriter.append("Explore our Membership options,PF Black Card,Classic\n");
      double originalAmount1 = extractAmount(Price1);
      double originalAmount2 = extractAmount(Price2);

      double conversionRate = 1.41;

      double cadAmount1 = originalAmount1 * conversionRate;
      double cadAmount2 = originalAmount2 * conversionRate;

       Price1 = String.format("%.2f", cadAmount1) + " /month";
      Price2=String.format("%.2f", cadAmount2) + " /month";
      csvWriter.append("," + Price1 + "," + Price2 + "\n");

      for (WebElement row: rows) {
        List < WebElement > columns = row.findElements(By.cssSelector("td"));
        StringBuilder rowString = new StringBuilder();

        for (WebElement column: columns) {
          String cellText = column.getText().trim();
          List < WebElement > paths = column.findElements(By.cssSelector("svg path"));

          if (!cellText.isEmpty()) {
            // If text exists, include the text in CSV
            rowString.append(cellText).append(",");
          } else if (!paths.isEmpty()) {
            // If path exists and text doesn't, put "yes" in CSV
            rowString.append("yes,");
          } else {
            // If both text and path don't exist, put "no" in CSV
            rowString.append("no,");
          }
        }

        csvWriter.append(rowString.toString().replaceAll(",$", "")).append("\n");

      }
      while (!LinkstoVisit.isEmpty()) {
        edgeWebDriver.get(LinkstoVisit.poll());
        WebElement clubname = edgeWebDriver.findElement(By.xpath("//*[@id=\"main-content\"]/div[1]/div[1]/h1"));
        WebElement clubaddress = edgeWebDriver.findElement(By.xpath("//*[@id=\"main-content\"]/div[1]/div[2]/p[1]"));
        WebElement clubphone = edgeWebDriver.findElement(By.xpath("//*[@id=\"main-content\"]/div[1]/div[2]/div[2]/a"));
        csvWriter.append(clubname.getText().replaceAll(","," ") + "," + clubaddress.getText().replaceAll(","," ") + "," + clubphone.getText() + "\n");
        //edgeWebDriver.navigate().back();
      }
      System.out.println("CSV file has been created successfully.");

    } catch (IOException e) {
      //e.printStackTrace();
    }

    edgeWebDriver.quit();

  }

  public float convertStringToInt(String input) throws NumberFormatException {
    // The Integer.parseInt() method can throw NumberFormatException
    return Float.parseFloat(input) * 2;
  }

  private boolean isElementPresent(WebDriver driver, By by) {
    try {
      driver.findElement(by);
      //checks whether element is present on page.
      return true;
    } catch (org.openqa.selenium.NoSuchElementException e) {
      return false;
    }
  }
  
  private static double extractAmount(String inputString) {
      // Remove non-numeric characters from the string
      String numericString = inputString.replaceAll("[^0-9.]", "");

      // Parse the numeric string to a double value
      return Double.parseDouble(numericString);
  }

}