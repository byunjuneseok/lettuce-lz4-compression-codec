plugins {
    id("java")
    id("com.vanniktech.maven.publish") version "0.34.0"
}

group = "com.binaryflavor"
version = "1.0.0"

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

mavenPublishing {
    coordinates(
        groupId = "com.binaryflavor",
        artifactId = "lettuce-lz4-compression-codec",
        version = version.toString()
    )

    pom {
        name.set("Lettuce LZ4 Compression Codec")
        description.set("A custom codec for Lettuce that uses LZ4 compression.")
        url.set("https://github.com/byunjuneseok/lettuce-lz4-compression-codec")

        licenses {
            license {
                name.set("The Apache Software License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("byunjuneseok")
                name.set("Juneseok Byun")
                email.set("byunjuneseok@gmail.com")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/byunjuneseok/lettuce-lz4-compression-codec.git")
            developerConnection.set("scm:git:ssh://github.com/byunjuneseok/lettuce-lz4-compression-codec.git")
            url.set("https://github.com/byunjuneseok/lettuce-lz4-compression-codec")
        }
    }
    publishToMavenCentral()
    signAllPublications()
}


tasks.test {
    useJUnitPlatform()
}
