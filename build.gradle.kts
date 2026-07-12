plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.hibernate.orm)
    alias(libs.plugins.graalvm.native)
    alias(libs.plugins.kotlin.jpa)
    pmd  // Gradle 내장 core 플러그인 — 버전 표기 불필요
}

group = "dev.haja"
version = "0.0.1-SNAPSHOT"
description = "spring-template-simple-kotlin"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

// PMD: Java 소스 정적분석. 현재 Kotlin 전용이라 pmdMain은 NO-SOURCE로 스킵되며,
// 향후 Java 소스가 추가되면 자동으로 룰이 적용된다.
pmd {
    toolVersion = libs.versions.pmd.get()           // Gradle 9.6.1 공식 지원 상한
    ruleSetFiles = files(".github/pmd/ruleset.xml")
    ruleSets = listOf()                             // 기본 룰셋(errorprone) 비활성화 명시
    sourceSets = listOf(project.sourceSets["main"]) // test/aot/aotTest 제외 — main만 check에 연결
    isConsoleOutput = true
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.spring.boot.h2console)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.module.kotlin)
    developmentOnly(libs.spring.boot.devtools)
    runtimeOnly(libs.h2)
    annotationProcessor(libs.spring.boot.configuration.processor)
    testImplementation(libs.spring.boot.starter.data.jpa.test)
    testImplementation(libs.spring.boot.starter.webmvc.test)
    testImplementation(libs.kotlin.test.junit5)
    // 아키텍처 테스트: ArchUnit(바이트코드 구조 규칙) + Konsist(코틀린 소스 컨벤션 규칙)
    testImplementation(libs.archunit.junit5)
    testImplementation(libs.konsist)
    testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}


allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
// 일반 Java 컴파일에서는 경고 활성화
tasks.named("compileJava", JavaCompile::class) {
    options.compilerArgs.add("-Xlint:unchecked")
}

// AOT 컴파일 태스크에서는 생성된 코드의 경고 완전 제거
tasks.named("compileAotJava", JavaCompile::class) {
    options.compilerArgs.addAll(listOf(
        "-Xlint:none"  // 모든 경고 완전 제거
    ))
}

// NOTE: processTestAot는 활성화 유지.
// 네이티브 테스트(nativeTest)에서 Spring TestContext 프레임워크가 동작하려면
// 테스트 AOT가 생성하는 리플렉션/리소스 메타데이터가 필요하다.
// (비활성화 시 BootstrapUtils 초기화 실패 → WebAppConfiguration ClassNotFoundException)


// GraalVM 네이티브 이미지: Hibernate ByteBuddy BytecodeProvider 서비스 디스크립터를 이미지에서 제외.
// 최신 GraalVM(JDK 25)은 서비스 디스크립터 리소스를 무조건 이미지에 포함하는데, spring-orm은
// ServiceLoaderFeature 등록만 배제하므로 런타임 ServiceLoader가 디스크립터는 읽되 클래스는 못 찾아
// "BytecodeProviderImpl not found"로 JPA 컨텍스트 로드가 실패한다(spring-framework#35118).
// 리소스 자체를 제외하면 ServiceLoader 결과가 비고, Hibernate 7.x가 no-op(none) BytecodeProvider로
// 폴백한다(BytecodeProviderInitiator.getBytecodeProvider: 빈 iterator → new none.BytecodeProviderImpl).
// 네이티브 런타임은 런타임 바이트코드 생성이 불가하므로 none provider가 정상 경로다.
graalvmNative {
    binaries.all {
        buildArgs.add("-H:ExcludeResources=META-INF/services/org\\.hibernate\\.bytecode\\.spi\\.BytecodeProvider")
    }
}
