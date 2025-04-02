plugins {
    id("java")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "ch"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

buildscript {
    repositories {
        maven {
            setUrl("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("org.openjfx:javafx-plugin:0.1.0")
    }
}
apply(plugin = "org.openjfx.javafxplugin")

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

javafx{
    version = "17"
    modules("javafx.controls", "javafx.fxml")
}


tasks.test {
    useJUnitPlatform()
}