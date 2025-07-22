plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
    alias(libs.plugins.serialization)

    application
}

dependencies {

    // Internal
    implementation(project(":pokemon:api"))
    implementation(project(":pokemon:data"))
    implementation(project(":database"))

    implementation(libs.coroutines)

    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.json)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.host.common)
    implementation(libs.ktor.server.status.pages)

    implementation(libs.koin.core)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logging)
    implementation(libs.koin.annotations)
    ksp(libs.koin.compiler)

    implementation(libs.logback.classic)

    implementation(libs.config4k)
}

application {
    mainClass = "com.patrickhoette.pokebe.api.ApplicationKt"
}
