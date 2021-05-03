package ipvc.ei20799.smartcity.dataclasses

import java.math.BigDecimal

data class Report (
    val id: String,
    val user_id: String,
    val type_id: String,
    val title: String,
    val description: String,
    val time: String,
    val image: String,
    val latitude: BigDecimal,
    val longitude: BigDecimal
)