import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

const val elementXPATH = "//div[@class='productTile__wrapper productTile__wrapper--noEqualHeights ']"
const val nameXPATH = "//p[@class='productTile-details__name-value']"
const val priceXPATH = "//p[@class='productTile__price-value-lead-price']"
const val pricePerWeightXPATH = "//div[@class='productTile__price-value-per-weight-text inline']"
const val quantityXPATH = "//span[@class='productTile__quantity-text']"

fun getChromeDriver(): ChromeDriver {
    val options = ChromeOptions()
    options.addArguments("--headless")
    return ChromeDriver(options)
}

fun ChromeDriver.getProductsFromUrl(url: String): List<Product> {
    get(url)
    return findElements(By.xpath(elementXPATH))
        .withIndex()
        .map { (idx, element) ->
            val productRaw = element.findElement(By.tagName("a"))
            val id = productRaw.getAttribute("id")
                ?: error("Product index cannot be null. See the error on the element: ${productRaw.text}")
            val productUrl = productRaw.getAttribute("href")
                ?: error("Product url cannot be null. See the error on the element: ${productRaw.text}")
            val name =
                productRaw.findElements(By.xpath(nameXPATH))[idx].text ?: ""
            val price =
                productRaw.findElements(By.xpath(priceXPATH))[idx].text ?: ""
            val pricePerWeight =
                productRaw.findElements(By.xpath(pricePerWeightXPATH))[idx].text
                    ?: ""
            val quantity =
                productRaw.findElements(By.xpath(quantityXPATH))[idx].text ?: ""
            Product(id, productUrl, name, price, pricePerWeight, quantity)
        }
}