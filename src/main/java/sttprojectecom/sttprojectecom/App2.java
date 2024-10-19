package sttprojectecom.sttprojectecom;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class App2 {
    WebDriver driver;
    WebDriverWait wait;
    String url = "https://www.saucedemo.com";
    String user = "standard_user";
    String pass = "secret_sauce";
    String first_name = "John";
    String last_name = "Xess";
    String pincode = "734002";

    // Browser setup
    @BeforeSuite
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(url);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        login(user, pass); // Perform login once
    }

    // Login function
    public void login(String username, String password) {
        WebElement userbox = driver.findElement(By.id("user-name"));
        WebElement passbox = driver.findElement(By.id("password"));
        userbox.sendKeys(username);
        passbox.sendKeys(password);
        driver.findElement(By.id("login-button")).click();
    }

    // Method to take a screenshot and store it in the "screenshots" folder
    public void takeScreenshot(String filename) {
        TakesScreenshot screenshot = (TakesScreenshot) driver;
        File srcFile = screenshot.getScreenshotAs(OutputType.FILE);

        // Create folder if it doesn't exist
        File destDir = new File("./screenshots");
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        // Store the screenshot in the screenshots folder
        try {
            FileHandler.copy(srcFile, new File(destDir, filename));
            System.out.println("Screenshot saved: " + filename); // Log confirmation
        } catch (IOException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage()); // Log error
        }
    }

    // Add items to cart, take screenshots, and proceed to checkout
    @Test
    public void addItemToCartAndTakeScreenshots() {
        // Add a backpack to the cart
        addItemToCart("sauce-labs-backpack");

        // Go to cart
        driver.findElement(By.xpath("//*[@id=\"shopping_cart_container\"]/a")).click();

        // Wait for the cart page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("cart_list")));
        // Take a screenshot after adding the backpack
        takeScreenshot("cart_with_backpack.png");

        // Add two more products
        driver.findElement(By.id("continue-shopping")).click();
        addItemToCart("sauce-labs-bolt-t-shirt");
        addItemToCart("sauce-labs-fleece-jacket");

        // Go back to the cart and take another screenshot
        driver.findElement(By.xpath("//*[@id=\"shopping_cart_container\"]/a")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("cart_list"))); // Wait for the cart to be visible
        takeScreenshot("cart_with_multiple_items.png");

        // Proceed to checkout
        checkout(first_name, last_name, pincode);
        completeCheckout(); // Complete the checkout process
    }

    // Add item to cart
    public void addItemToCart(String itemId) {
        String addItemButtonId = "add-to-cart-" + itemId;
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(addItemButtonId)));
        driver.findElement(By.id(addItemButtonId)).click();
    }

    // Checkout process
    public void checkout(String firstName, String lastName, String postalCode) {
        driver.findElement(By.id("checkout")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("first-name")));
        driver.findElement(By.id("first-name")).sendKeys(firstName);
        driver.findElement(By.id("last-name")).sendKeys(lastName);
        driver.findElement(By.id("postal-code")).sendKeys(postalCode);
        driver.findElement(By.id("continue")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("finish")));
    }

    // Complete checkout, print details, and take a screenshot
    public void completeCheckout() {
        // Click on the "Finish" button to complete the checkout
        driver.findElement(By.id("finish")).click();

        // Wait until the confirmation page is displayed
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"checkout_complete_container\"]/h2")));

        // Print the confirmation details to the console
        String confirmationMessage = driver.findElement(By.xpath("//*[@id=\"checkout_complete_container\"]/h2")).getText();
        String confirmationDetails = driver.findElement(By.xpath("//*[@id=\"checkout_complete_container\"]/div")).getText();
        System.out.println("Order Confirmation: " + confirmationMessage);
        System.out.println("Details: " + confirmationDetails);

        // Take a screenshot of the order confirmation page
        takeScreenshot("order_confirmation.png");

        // Navigate back to the products page
        driver.findElement(By.id("back-to-products")).click();
    }

    // Sign-out
    public void sign_out() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("react-burger-menu-btn")));
            driver.findElement(By.id("react-burger-menu-btn")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logout_sidebar_link")));
            driver.findElement(By.id("logout_sidebar_link")).click();
        } catch (NoSuchElementException e) {
            System.out.println("Sign-out button not found, maybe already logged out.");
        }
    }

    // Close browser
    @AfterSuite
    public void tear_down() {
        sign_out();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name")));
        driver.quit();
    }
}
