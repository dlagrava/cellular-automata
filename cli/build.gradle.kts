plugins {
    application
}

dependencies {
    implementation(project(":core"))
    implementation(project(":examples"))
    implementation(project(":display"))
}

application {
    mainClass.set("cellularautomata.cli.BatchCli")
}
