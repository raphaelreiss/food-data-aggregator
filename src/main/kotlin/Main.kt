import com.google.cloud.bigquery.*
import db.getOrCreateDataset
import db.getOrCreateTable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.openqa.selenium.chrome.ChromeDriver
import java.io.File


fun main() {

    val driver = getChromeDriver()
    val pageSize = 10
    val urls = listOf(
        "https://www.coop.ch/fr/nourriture/viandes-poissons/viande-de-la-boucherie/c/m_2333?pageSize=$pageSize",
        "https://www.coop.ch/fr/nourriture/viandes-poissons/poisson-de-la-poissonnerie/c/m_1893?pageSize=$pageSize",
        "https://www.coop.ch/fr/nourriture/viandes-poissons/viandes-fraiches-emballees/c/m_0088?pageSize=$pageSize",
        "https://www.coop.ch/fr/nourriture/viandes-poissons/charcuteries-saucisses/c/m_0097?pageSize=$pageSize",
        "https://www.coop.ch/fr/nourriture/produits-laitiers-oeufs/fromage-de-la-fromagerie/c/m_1909?pageSize=$pageSize",
        "https://www.coop.ch/fr/nourriture/produits-laitiers-oeufs/fromages-emballes/c/m_0075?pageSize=$pageSize",
        "https://www.coop.ch/fr/nourriture/produits-laitiers-oeufs/beurre-margarine/c/m_0084?pageSize=$pageSize",
        "https://www.coop.ch/fr/nourriture/produits-laitiers-oeufs/yogourts/c/m_0066?pageSize=$pageSize",
        "https://www.coop.ch/fr/nourriture/produits-laitiers-oeufs/oeufs/c/m_0083?pageSize=$pageSize",
        "https://www.coop.ch/fr/nourriture/pains-viennoiseries/pain-de-la-boulangerie/c/m_0119?pageSize=$pageSize",
        "https://www.coop.ch/fr/nourriture/friandises-snacks/chocolat-sucreries/c/m_0191?pageSize=$pageSize"
    )

    val datasetName = "food_data"
    val tableName = "products"
    val location = "EU"

    getOrCreateDataset(datasetName, location)

    val productSchema = Schema.of(
        Field.of("id", StandardSQLTypeName.STRING),
        Field.of("url", StandardSQLTypeName.STRING),
        Field.of("name", StandardSQLTypeName.STRING),
        Field.of("price", StandardSQLTypeName.STRING),
        Field.of("price_per_weight", StandardSQLTypeName.STRING),
        Field.of("quantity", StandardSQLTypeName.STRING),
    )

    getOrCreateTable(datasetName, tableName, productSchema)


//    runRemote(urls, driver, table)
//    val file = getRecordFile()
//    runLocal(urls, driver, file)


}

fun runRemote(urls: List<String>, driver: ChromeDriver, table: Table) {
    try {
        val products = extractInformationFromURLs(urls, driver)
        val jsonString = Json.encodeToString(products)
        writeBatchStream(table, jsonString)
    } finally {
        driver.quit()
    }
}

fun writeBatchStream(table: Table, jsonString: String) {

    TODO("""
        Write the jsonString to the table using the BigQuery Batch Stream API.
        See https://cloud.google.com/bigquery/docs/reference/rest/v2/tabledata/insertAll
        and https://cloud.google.com/bigquery/docs/reference/rest/v2/tabledata/insertAll#request-body
    """.trimIndent())
}

private fun runLocal(
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

