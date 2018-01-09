package xin.monus.checkit.data.entity

data class User(
        val username: String,
        var password: String,
        var nickname: String,
        var height: Double = 0.0,
        var weight: Double = 0.0,
        var daily_calorie: Double = 0.0,
        var status: Int = 0,
        var timestamp: String = ""
)