plugins {
    kotlin("jvm") version "2.3.21"
    kotlin("plugin.serialization") version "2.3.21"
    id("io.ktor.plugin") version "3.6.1"
}

group = "com.rentech"
version = "1.0.0"

application {
    mainClass.set("com.rentech.server.AplikasiKt")
}

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}

val exposed = "1.6.1"
val flyway = "13.13.0"

dependencies {
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-auth-jwt")
    implementation("ch.qos.logback:logback-classic:1.6.8")

    implementation("org.jetbrains.exposed:exposed-core:$exposed")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed")
    implementation("org.flywaydb:flyway-core:$flyway")
    implementation("org.flywaydb:flyway-database-postgresql:$flyway")
    implementation("com.zaxxer:HikariCP:7.1.0")
    implementation("org.postgresql:postgresql:42.7.15")
    implementation("at.favre.lib:bcrypt:0.10.2")

    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("io.ktor:ktor-client-content-negotiation")
    testImplementation("io.zonky.test:embedded-postgres:2.2.3")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<JavaExec>("seed") {
    group = "rentech"
    description = "Migrasi lalu isi data contoh. Memakai DATABASE_URL, JWT_SECRET, ADMIN_EMAIL, ADMIN_SANDI, FOTO_DIR."
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.rentech.server.seed.SeedKt")
}
