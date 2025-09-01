plugins {
    id("java")
}

group = "com.binaryflavor"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // redis
    implementation("io.lettuce:lettuce-core:6.8.0.RELEASE")

    // compression
    implementation("org.lz4:lz4-java:1.8.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
