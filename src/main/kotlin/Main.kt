import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import java.io.File
import java.io.IOException
import java.time.LocalDate
import kotlin.coroutines.suspendCoroutine

@Serializable
data class Product(val name: String, val price: String)

suspend fun main() {

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
    val driver = ChromeDriver(options)
    return driver
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
): MutableList<Product> = runBlocking {
    val products = mutableListOf<Product>()
    urls.map { endpoint ->
        launch {
            println("Start scraping $endpoint")
            val productsBatch = driver.getProductsFromUrl(endpoint)
            println("${productsBatch.size} products scraped from $endpoint")
            products.addAll(productsBatch)
        }
    }

    return@runBlocking products
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

suspend fun ChromeDriver.getProductsFromUrl(url: String): List<Product> {
    get(url)
    val productsNames = findElements(By.className("productTile-details__name-value"))
    val productsPrices = findElements(By.className("productTile__price-value-lead-price"))
    val products = productsNames.zip(productsPrices)
        .map {
            Product(it.first.text, it.second.text)
        }
    return suspendCoroutine { products }
}
