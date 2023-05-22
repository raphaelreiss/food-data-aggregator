import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import java.io.File
import java.io.IOException
import java.time.LocalDate

const val elementXPATH = "//li[@class='list-page__item col-12 col-sm-6 col-lg-4 col-xl-3 ']"
const val nameXPATH = "//p[@class='productTile-details__name-value']"
const val priceXPATH = "//p[@class='productTile__price-value-lead-price']"
const val pricePerWeightXPATH = "//div[@class='productTile__price-value-per-weight-text inline']"
const val quantityXPATH = "//span[@class='productTile__quantity-text']"


fun main() {

    val driver = getChromeDriver()
    val urls = listOf(
        "https://www.coop.ch/fr/nourriture/viandes-poissons/viande-de-la-boucherie/c/m_2333?pageSize=100",
        "https://www.coop.ch/fr/nourriture/viandes-poissons/poisson-de-la-poissonnerie/c/m_1893?pageSize=100",
        "https://www.coop.ch/fr/nourriture/viandes-poissons/viandes-fraiches-emballees/c/m_0088?pageSize=300",
        "https://www.coop.ch/fr/nourriture/viandes-poissons/charcuteries-saucisses/c/m_0097?page=2&pageSize=600",
        "https://www.coop.ch/fr/nourriture/produits-laitiers-oeufs/fromage-de-la-fromagerie/c/m_1909?pageSize=100",
        "https://www.coop.ch/fr/nourriture/produits-laitiers-oeufs/fromages-emballes/c/m_0075?pageSize=500",
        "https://www.coop.ch/fr/nourriture/produits-laitiers-oeufs/beurre-margarine/c/m_0084?pageSize=120",
        "https://www.coop.ch/fr/nourriture/produits-laitiers-oeufs/yogourts/c/m_0066?pageSize=300",
        "https://www.coop.ch/fr/nourriture/produits-laitiers-oeufs/oeufs/c/m_0083?pageSize=60",
        "https://www.coop.ch/fr/nourriture/pains-viennoiseries/pain-de-la-boulangerie/c/m_0119?pageSize=48",
        "https://www.coop.ch/fr/nourriture/friandises-snacks/chocolat-sucreries/c/m_0191?pageSize=1500"
    )

    val file = getRecordFile()
    run(urls, driver, file)

}

fun getChromeDriver(): ChromeDriver {
    val options = ChromeOptions()
    options.addArguments("--headless")
    return ChromeDriver(options)
}

private fun run(
    urls: List<String>,
    driver: ChromeDriver,
    file: File
) {
    try {

        val products = extractInformationFromURLs(urls, driver)
        val jsonString = Json.encodeToString(products)
        writeToFile(file, jsonString)

    } finally {
        driver.quit()
    }
}

fun extractInformationFromURLs(
    urls: List<String>,
    driver: ChromeDriver
): MutableList<Product> {
    val products = mutableListOf<Product>()
    for (url in urls) {
        val productsBatch = driver.getProductsFromUrl(url)
        println("${productsBatch.size} products scraped from $url")
        products.addAll(productsBatch)
    }
    return products
}

private fun getRecordFile(): File {
    val basePath = System.getProperty("user.dir")
    val dataFolder = File(basePath, "data")
    if (!dataFolder.exists()) createFolder(dataFolder)
    val todayFolder = File(dataFolder, LocalDate.now().toString())
    if (!todayFolder.exists()) createFolder(todayFolder)
    return File(todayFolder, "records.json")
}

private fun writeToFile(file: File, json: String) {
    try {
        file.writeText(json)
        println("Successfully write ${file.absolutePath}")
    } catch (e: IOException) {
        println("Failed to write digest file")
    }
}

fun createFolder(todayFolder: File) {
    val created = todayFolder.mkdir()
    val message = if (created) {
        "Folder created successfully at: ${todayFolder.absolutePath}"
    } else {
        "Folder already exists at: ${todayFolder.absolutePath}"
    }
    println(message)
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
