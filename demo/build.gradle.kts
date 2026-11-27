plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":examples"))
    implementation(project(":display"))
}

javafx {
    version = "21.0.5"
    modules("javafx.controls", "javafx.web")
}

application {
    // mainClass set when the JavaFX Application exists
    mainClass.set("cellularautomata.demo.DemoApp")
}

tasks.register<JavaExec>("runHodgePodge") {
    group = "application"
    description = "Run the HodgePodge standalone JavaFX app"
    val runtimeCp = sourceSets["main"].runtimeClasspath
    val javafxJars = runtimeCp.filter { it.name.startsWith("javafx-") }
    classpath = runtimeCp.filter { !it.name.startsWith("javafx-") }
    mainClass.set("cellularautomata.demo.HodgePodgeApp")
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    jvmArgumentProviders.add(CommandLineArgumentProvider {
        listOf("--module-path", javafxJars.asPath, "--add-modules", "javafx.controls")
    })
}

tasks.register<JavaExec>("runEmoticonHodge") {
    group = "application"
    description = "Run the Emoticon HodgePodge standalone JavaFX app"
    val runtimeCp = sourceSets["main"].runtimeClasspath
    val javafxJars = runtimeCp.filter { it.name.startsWith("javafx-") }
    classpath = runtimeCp.filter { !it.name.startsWith("javafx-") }
    mainClass.set("cellularautomata.demo.EmoticonHodgeApp")
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    jvmArgumentProviders.add(CommandLineArgumentProvider {
        listOf("--module-path", javafxJars.asPath, "--add-modules", "javafx.controls,javafx.web")
    })
}
