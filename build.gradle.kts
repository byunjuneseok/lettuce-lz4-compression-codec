plugins {
    id("java")
    id("com.vanniktech.maven.publish") version "0.34.0"
}

group = "com.binaryflavor"
version = getVersionFromGit()

fun getVersionFromGit(): String {
    return try {
        val process = ProcessBuilder("git", "describe", "--tags", "--exact-match", "HEAD")
            .redirectErrorStream(true)
            .start()
        val result = process.inputStream.bufferedReader().readText().trim()
        process.waitFor()

        if (process.exitValue() == 0 && result.isNotEmpty()) {
            result.removePrefix("v")
        } else {
            val commitProcess = ProcessBuilder("git", "rev-parse", "--short", "HEAD")
                .redirectErrorStream(true)
                .start()
            val commitHash = commitProcess.inputStream.bufferedReader().readText().trim()
            commitProcess.waitFor()

            if (commitProcess.exitValue() == 0 && commitHash.isNotEmpty()) {
                "0.0.0-$commitHash-SNAPSHOT"
            } else {
                "0.0.0-SNAPSHOT"
            }
        }
    } catch (e: Exception) {
        "0.0.0-SNAPSHOT"
    }
}

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

tasks.register("checkVersion") {
    group = "help"
    description = "Check the current project version"
    doLast {
        println("Project version: ${project.version}")
    }
}
