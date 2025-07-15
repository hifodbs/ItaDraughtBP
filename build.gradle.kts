plugins {
    id("java")
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass = "units.berettapillinini.draught/Main"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "units.berettapillinini.draught.Main"
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<JavaExec>("run"){
    standardInput = System.`in`
}