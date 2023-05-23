import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import kotlin.test.assertNotEquals

class MainKtTest {

    @Test
    fun restructureGetProducts() {

        val driver = getChromeDriver()
        val url = "https://www.coop.ch/fr/nourriture/pains-viennoiseries/pain-de-la-boulangerie/c/m_0119?pageSize=2"

        val products = driver.getProductsFromUrl(url)

        val prod1 = products[0]
        val prod2 = products[1]

        assertNotEquals(prod1.id, prod2.id)
        assertNotEquals(prod1.url, prod2.url)
        assertNotEquals(prod1.name, prod2.name)
        assertNotEquals(prod1.price, prod2.price)
        assertNotEquals(prod1.pricePerWeight, prod2.pricePerWeight)
        assertNotEquals(prod1.quantity, prod2.quantity)

    }

    @Test
    fun extractInformationFromURLsTest() {

        val pageSize = 1
        val urls = listOf(
            "https://www.coop.ch/fr/nourriture/viandes-poissons/viande-de-la-boucherie/c/m_2333?pageSize=$pageSize",
            "https://www.coop.ch/fr/nourriture/viandes-poissons/poisson-de-la-poissonnerie/c/m_1893?pageSize=$pageSize",
            "https://www.coop.ch/fr/nourriture/viandes-poissons/viandes-fraiches-emballees/c/m_0088?pageSize=$pageSize",
            "https://www.coop.ch/fr/nourriture/viandes-poissons/charcuteries-saucisses/c/m_0097?page=2&pageSize=$pageSize",
            "https://www.coop.ch/fr/nourriture/produits-laitiers-oeufs/fromage-de-la-fromagerie/c/m_1909?pageSize=$pageSize",
            "https://www.coop.ch/fr/nourriture/produits-laitiers-oeufs/fromages-emballes/c/m_0075?pageSize=$pageSize",
            "https://www.coop.ch/fr/nourriture/produits-laitiers-oeufs/beurre-margarine/c/m_0084?pageSize=$pageSize",
            "https://www.coop.ch/fr/nourriture/produits-laitiers-oeufs/yogourts/c/m_0066?pageSize=$pageSize",
            "https://www.coop.ch/fr/nourriture/produits-laitiers-oeufs/oeufs/c/m_0083?pageSize=$pageSize",
            "https://www.coop.ch/fr/nourriture/pains-viennoiseries/pain-de-la-boulangerie/c/m_0119?pageSize=$pageSize",
            "https://www.coop.ch/fr/nourriture/friandises-snacks/chocolat-sucreries/c/m_0191?pageSize=$pageSize"
        )

        val driver = getChromeDriver()
        val products = extractInformationFromURLs(urls, driver)

        assertEquals(urls.size * pageSize, products.size)
        products.forEach {
            assert(it.name.isNotEmpty())
            assert(it.price.isNotEmpty())
        }
    }
}