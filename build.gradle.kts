plugins {
    kotlin("jvm") version "1.8.0"
    id("org.openjfx.javafxplugin") version "0.0.13" // Updated version
}

group = "ch"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // JavaFX dependency
    implementation("org.openjfx:javafx-controls:17")
}

javafx {
    version = "17"
    modules("javafx.controls", "javafx.fxml")
}

// Run the JavaFX application using Gradle task
tasks.register<JavaExec>("run") {
    group = "application"
    description = "Run JavaFx application"
    mainClass.set("ch.Ui")  // Change this to your main Java class
    classpath = sourceSets["main"].runtimeClasspath
    jvmArgs = listOf(
        "--module-path", classpath.asPath,
        "--add-modules", "javafx.controls,javafx.fxml"
    )
}

tasks.test {
    useJUnitPlatform()
}