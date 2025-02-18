plugins {
	java
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
}

fun getGitHash(): String {
	return providers.exec {
		commandLine("git", "rev-parse", "--short", "HEAD")
	}.standardOutput.asText.get().trim()
}

group = "kr.hhplus.be"
version = getGitHash()

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
	}
}

dependencies {
    // Spring
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")

	//spring Doc
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

	//redis client redisson 추가
	implementation("org.redisson:redisson-spring-boot-starter:3.37.0")

	//caffeine 캐시 추가
	implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")


	// Lombok 의존성 추가
	implementation("org.projectlombok:lombok") // 최신 버전 사용
	annotationProcessor("org.projectlombok:lombok")

    // DB
	runtimeOnly("com.mysql:mysql-connector-j")

	//kafka
	implementation("org.springframework.kafka:spring-kafka")

	//kafkaTest
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testImplementation("org.testcontainers:kafka")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:mysql")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
	systemProperty("user.timezone", "UTC")
}
