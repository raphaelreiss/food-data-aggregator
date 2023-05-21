import kotlinx.serialization.Serializable

@Serializable
data class Product(val id: String, val url: String, val name: String, val price: String, val pricePerWeight: String, val quantity: String)