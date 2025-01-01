plugins {
    id("java")
}

version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("libs/poseidon.jar"))
    implementation(files("libs/Essentials.jar"))
    implementation("javassist:javassist:3.12.1.GA")
}