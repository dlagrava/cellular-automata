plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

dependencies {
    implementation(project(":core"))
}

javafx {
    version = "21.0.5"
    modules("javafx.controls")
}

application {
    // mainClass set when the JavaFX Application exists
    mainClass.set("cellularautomata.demo.DemoApp")
}
