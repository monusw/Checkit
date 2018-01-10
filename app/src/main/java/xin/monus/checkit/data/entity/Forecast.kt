package xin.monus.checkit.data.entity

data class Forecast (
        var type: ForecastType,
        var content: String,
        var deadline: String
)

enum class ForecastType {
    INBOX,
    PROJECT,
    ACTION
}