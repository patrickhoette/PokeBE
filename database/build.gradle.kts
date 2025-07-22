plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.jvm)
}

dependencies {

    api(project(":core:domain"))

    implementation(libs.koin.core)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logging)
    implementation(libs.koin.annotations)
    ksp(libs.koin.compiler)
}
