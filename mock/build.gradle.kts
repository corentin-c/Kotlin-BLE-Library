plugins {
    alias(libs.plugins.nordic.feature)
    alias(libs.plugins.nordic.hilt)
    alias(libs.plugins.kotlin.parcelize)
}

group = "no.nordicsemi.android.kotlin.ble.mock"

android {
    namespace = "no.nordicsemi.android.kotlin.ble.mock"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":client-api"))
    implementation(project(":server-api"))

    implementation(libs.nordic.core)
}