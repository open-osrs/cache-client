/*
 * Copyright (c) 2019 Owain van Brakel <https://github.com/Owain94>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    java
    `maven-publish`
}

group = "com.openosrs"
version = "1.0-SNAPSHOT"
var rsversion = "191"

repositories {
    jcenter()
    mavenCentral()
    mavenLocal()
    maven(url = "https://raw.githubusercontent.com/open-osrs/hosting/master")
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.10")

    compileOnly("org.projectlombok:lombok:1.18.10")

    implementation("com.google.guava:guava:28.2-jre")
    implementation("org.slf4j:slf4j-simple:1.7.30")
    implementation("io.netty:netty-all:4.1.45.Final")
    implementation("com.openosrs:protocol:1.0-SNAPSHOT")
    implementation("com.openosrs:protocol-api:1.0-SNAPSHOT")
    implementation("com.openosrs:cache:187.0-SNAPSHOT")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
    register<JavaExec>("download") {
        dependsOn("build")

        classpath = project.sourceSets.main.get().runtimeClasspath
        main = "net.runelite.cache.client.CacheClient"
        args(listOf(rsversion))
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

publishing {
    repositories {
        maven {
            url = uri("$buildDir/repo")
        }
    }
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar.get())
        }
    }
}
