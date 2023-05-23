import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.openqa.selenium.chrome.ChromeDriver
import java.io.File


fun main() {

    val driver = getChromeDriver()
    val urls = listOf(
        "https://www.coop.ch/fr/nourriture/viandes-poissons/viande-de-la-boucherie/c/m_2333?pageSize=100",
        "https://www.coop.ch/fr/nourriture/viandes-poissons/poisson-de-la-poissonnerie/c/m_1893?pageSize=100",
        "https://www.coop.ch/fr/nourriture/viandes-poissons/viandes-fraiches-emballees/c/m_0088?pageSize=300",
        "https://www.coop.ch/fr/nourriture/viandes-poissons/charcuteries-saucisses/c/m_0097?pageSize=600",
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

