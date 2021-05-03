package ipvc.ei20799.smartcity.dataclasses

import java.math.BigDecimal

data class NewReport (
        val idUser: String,
        val idType: String,
        val title: String,
        val description: String,
        val latitude: BigDecimal,
        val longitude: BigDecimal
)