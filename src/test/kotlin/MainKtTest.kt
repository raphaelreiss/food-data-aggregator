import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MainKtTest {

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

        assertEquals(urls.size, products.size)
        products.forEach {
            assert(!it.name.isEmpty())
            assert(!it.price.isEmpty())
        }



    }
}