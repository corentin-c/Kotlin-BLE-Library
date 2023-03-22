package no.nordicsemi.android.kotlin.ble.profile.gls.data

enum class Carbohydrate(internal val value: Int) {
    RESERVED(0),
    BREAKFAST(1),
    LUNCH(2),
    DINNER(3),
    SNACK(4),
    DRINK(5),
    SUPPER(6),
    BRUNCH(7);

    companion object {
        fun create(value: Int): Carbohydrate {
            return values().firstOrNull { it.value == value }
                ?: throw IllegalArgumentException("Cannot create Carbohydrate for value $value")
        }
    }
}