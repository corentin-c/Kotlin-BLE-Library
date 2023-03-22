package no.nordicsemi.android.kotlin.ble.profile.gls.data

enum class Meal(internal val value: Int) {
    RESERVED(0),
    PREPRANDIAL(1),
    POSTPRANDIAL(2),
    FASTING(3),
    CASUAL(4),
    BEDTIME(5);

    companion object {
        fun create(value: Int): Meal {
            return values().firstOrNull { it.value == value }
                ?: throw IllegalArgumentException("Cannot create Meal for value $value")
        }
    }
}