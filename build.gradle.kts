plugins {
	id("idea")
	java
	id("org.springframework.boot") version "4.0.3"
	id("io.spring.dependency-management") version "1.1.7"
	jacoco
}

group = "com.algaworks.algashop"
version = "0.0.1-SNAPSHOT"
description = "billing microservice"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

val mockitoAgent: Configuration = configurations.create("mockitoAgent")
configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

val mapstructVersion = "1.6.3"

dependencies {
	implementation("com.fasterxml.uuid:java-uuid-generator:5.2.0")
	implementation("commons-validator:commons-validator:1.10.1")
	implementation("org.mapstruct:mapstruct:${mapstructVersion}")
	implementation("org.springframework.boot:spring-boot-h2console")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")

	compileOnly("org.projectlombok:lombok")

	runtimeOnly("com.h2database:h2")

	annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
	annotationProcessor("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
	annotationProcessor("org.hibernate.orm:hibernate-processor")

	testCompileOnly("org.projectlombok:lombok")

	testAnnotationProcessor("org.projectlombok:lombok")

	testImplementation("net.datafaker:datafaker:2.5.4")
	testImplementation("org.assertj:assertj-core:3.27.7")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")

	mockitoAgent("org.mockito:mockito-core"){
		isTransitive = false
	}

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.register<Test>("integrationTest"){
	description = "Run unit tests."
	group = "verification"

	testClassesDirs = tasks.test.get().testClassesDirs
	classpath = tasks.test.get().classpath

	useJUnitPlatform{
		includeTags("IntegrationTest")
	}
	systemProperty("test.seed", System.getProperty("test.seed") ?: "")
}

tasks.register<Test>("unitTest"){
	description = "Run unit tests."
	group = "verification"

	testClassesDirs = tasks.test.get().testClassesDirs
	classpath = tasks.test.get().classpath

	useJUnitPlatform{
		includeTags("UnitTest")
	}
	systemProperty("test.seed", System.getProperty("test.seed") ?: "")
}

tasks.jacocoTestReport {
	reports {
		xml.required = false
		csv.required = false
		html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
	}
}