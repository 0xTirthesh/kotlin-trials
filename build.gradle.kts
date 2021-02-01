import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.3.61"
}

group = "tech.local.trials"
version = "0.1.2-SNAPSHOT"

repositories {
  mavenCentral()
  mavenLocal()
}

dependencies {
  compile(kotlin("stdlib-jdk8"))

  compile("ch.qos.logback", "logback-classic", "1.2.3")

  "2.9.8".let {
    compile("com.fasterxml.jackson.module", "jackson-module-kotlin", it)
    compile("com.fasterxml.jackson.core", "jackson-databind", it)
    compile("com.fasterxml.jackson.datatype", "jackson-datatype-jdk8", it)
    compile("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", it)

    // required for json validations
    compile("org.hibernate", "hibernate-validator", "6.0.16.Final")
    compile("org.glassfish", "javax.el", "3.0.1-b09")

  }


  compile("com.ryanharter.ktor", "ktor-moshi", "1.0.1")
  compile("org.jasypt", "jasypt", "1.9.2")

  "0.10.3".let {
    compile("io.arrow-kt", "arrow-core", it)
    compile("io.arrow-kt", "arrow-instances-core", it)
  }

  "1.1.1".let {
    compile("io.ktor", "ktor-server-core", it)
    compile("io.ktor", "ktor-server-test-host", it)
    compile("io.ktor", "ktor-auth", it)
  }

  "5.3.2".let {
    testApi("org.junit.jupiter", "junit-jupiter-api", it)
    testImplementation("org.junit.jupiter", "junit-jupiter-engine", it)
  }
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    jvmTarget = "1.8"
  }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
  freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
}
