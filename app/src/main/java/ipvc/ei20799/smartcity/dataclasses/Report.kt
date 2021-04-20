package ipvc.ei20799.smartcity.dataclasses

import java.math.BigDecimal

data class Report (
    val id: String,
    val idUser: String,
    val idType: String,
    val title: String,
    val description: String,
    val time: String,
    val image: String,
    val latitude: BigDecimal,
    val longitude: BigDecimal
)