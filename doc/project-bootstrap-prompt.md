# 프로젝트 부트스트랩 프롬프트

> 아래 전체를 LLM(Claude Opus/Sonnet 등)에게 프롬프트로 전달하면, 빈 디렉토리에
> `spring-template-simple-kotlin` 템플릿과 동일한 구성의 새 프로젝트를 생성한다.
> `{{PROJECT_NAME}}`에 새 프로젝트 이름을 넣어 사용할 것.

---

당신은 빈 디렉토리에서 Spring Boot Kotlin 프로젝트를 생성하는 작업을 수행한다.
아래 명세를 **한 글자도 임의로 바꾸지 말고** 그대로 따라라.

## 0. ⚠️ 가장 중요한 규칙

이 프로젝트는 **Spring Boot 4.1.0 / Kotlin 2.4.0 / Gradle 9.6.1 / JDK 25** 기반이며,
당신의 학습 데이터보다 최신일 수 있다. 아래 파일 내용에는 당신이 "틀렸다"고 느낄 수 있는
최신 명칭이 포함되어 있으나 **전부 올바른 것**이다. 절대 다음과 같이 "교정"하지 마라:

- `spring-boot-starter-webmvc` → ~~`spring-boot-starter-web`~~ (금지)
- `spring-boot-h2console`, `spring-boot-starter-webmvc-test`, `spring-boot-starter-data-jpa-test` — 실존하는 Boot 4.x 모듈
- `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest` → ~~`org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest`~~ (금지)
- `org.springframework.test.context.bean.override.mockito.MockitoBean` → ~~`@MockBean`~~ (금지)
- `tools.jackson.module:jackson-module-kotlin` → ~~`com.fasterxml.jackson.module`~~ (금지, Jackson 3)

파일 내용은 아래 코드블록을 **placeholder 치환만 하고 그대로 복사**하라.

## 1. 입력 파라미터

| 토큰 | 의미 | 규칙 | 예시 |
|---|---|---|---|
| `{{PROJECT_NAME}}` | **필수.** 프로젝트 이름 (kebab-case) | 소문자·숫자·하이픈, 문자로 시작 | `demo-service` |
| `{{GROUP}}` | 선택. Gradle group / 패키지 접두어 | 지정 없으면 `dev.haja` | `dev.haja` |
| `{{SONAR_ORG}}` | 선택. SonarCloud organization | 지정 없으면 `rkaehdaos` | `rkaehdaos` |

파생 토큰 (직접 계산하라):

| 토큰 | 파생 규칙 | 예시 (`demo-service` 기준) |
|---|---|---|
| `{{PACKAGE_SEGMENT}}` | PROJECT_NAME에서 하이픈 제거 | `demoservice` |
| `{{CLASS_PREFIX}}` | PROJECT_NAME을 PascalCase 변환 | `DemoService` |
| `{{BASE_PACKAGE}}` | `{{GROUP}}.{{PACKAGE_SEGMENT}}` | `dev.haja.demoservice` |

참고 — 원본 템플릿의 값: `spring-template-simple-kotlin` / `dev.haja` /
`springtemplatesimplekotlin` / `SpringTemplateSimpleKotlin`

## 2. 기술 스택 요약

| 항목 | 값 |
|---|---|
| 언어 | Kotlin 2.4.0 (JVM toolchain 25) |
| 프레임워크 | Spring Boot 4.1.0 + spring-dependency-management 1.1.7 |
| 빌드 | Gradle 9.6.1 (Kotlin DSL, 버전 카탈로그) |
| JDK | Oracle GraalVM 25.0.3 (mise로 관리) |
| DB | H2 + Spring Data JPA (Hibernate ORM plugin 7.4.3.Final) |
| 네이티브 | GraalVM Native Build Tools 1.1.4 |
| 테스트 | JUnit5, ArchUnit 1.4.2, Konsist 0.17.3, Kotest 6.2.2 |
| 정적분석 | PMD 7.24.0 (커스텀 룰셋) + SonarCloud (org.sonarqube 7.3.1.8318) |
| 커버리지 | Kover 0.9.8 (라인 최소 30% 강제) |
| CI | GitHub Actions (`.github/workflows/build.yml`) |

## 3. 최종 디렉토리 구조

```
{{PROJECT_NAME}}/
├── CLAUDE.md
├── mise.toml                      # .gitignore 대상 (커밋 안 됨 — 정상)
├── settings.gradle.kts
├── build.gradle.kts
├── .gitignore
├── .gitattributes
├── .gitmessage.txt
├── .github/
│   ├── PULL_REQUEST_TEMPLATE.md
│   ├── pmd/ruleset.xml
│   └── workflows/build.yml
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/                   # Step 3에서 명령으로 생성
├── gradlew / gradlew.bat          # Step 3에서 명령으로 생성
└── src/
    ├── main/kotlin/{{GROUP 경로}}/{{PACKAGE_SEGMENT}}/
    │   ├── {{CLASS_PREFIX}}Application.kt
    │   ├── controller/MemoController.kt
    │   ├── domain/Memo.kt
    │   ├── repository/MemoRepository.kt
    │   └── service/MemoService.kt
    ├── main/resources/application.yaml
    └── test/kotlin/{{GROUP 경로}}/{{PACKAGE_SEGMENT}}/
        ├── {{CLASS_PREFIX}}ApplicationTests.kt
        ├── architecture/ArchitectureTest.kt
        ├── architecture/KonsistTest.kt
        └── controller/MemoControllerTest.kt
```

`{{GROUP 경로}}` = GROUP의 `.`을 `/`로 바꾼 것 (예: `dev/haja`).
`Memo`는 **샘플 도메인 이름**이며 프로젝트 이름이 아니다 — 치환하지 마라.

## 4. Step 1 — mise.toml 및 JDK 설치

`mise.toml`:

```toml
[tools]
java = "oracle-graalvm-25.0.3"
```

이후 실행: `mise install` → `mise exec -- java -version`으로 GraalVM 25 확인.

## 5. Step 2 — Gradle 빌드 파일

`settings.gradle.kts`:

```kotlin
rootProject.name = "{{PROJECT_NAME}}"
```

`build.gradle.kts` (주석 포함 그대로):

```kotlin
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.hibernate.orm)
    alias(libs.plugins.graalvm.native)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.kover)
    alias(libs.plugins.sonarqube)
    pmd  // Gradle 내장 core 플러그인 — 버전 표기 불필요
}

group = "{{GROUP}}"
version = "0.0.1-SNAPSHOT"
description = "{{PROJECT_NAME}}"

// PMD: Java 소스 정적분석. 현재 Kotlin 전용이라 pmdMain은 NO-SOURCE로 스킵되며,
// 향후 Java 소스가 추가되면 자동으로 룰이 적용된다.
pmd {
    toolVersion = libs.versions.pmd.get()           // Gradle 9.6.1 공식 지원 상한
    ruleSetFiles = files(".github/pmd/ruleset.xml")
    ruleSets = listOf()                             // 기본 룰셋(errorprone) 비활성화 명시
    sourceSets = listOf(project.sourceSets["main"]) // test/aot/aotTest 제외 — main만 check에 연결
    isConsoleOutput = true
}

// Kover: 코틀린 코드 커버리지 — XML(Sonar 연동)/HTML 리포트 + 최소 기준 검증(check에 자동 연결)
kover {
    reports {
        filters {
            includes {
                classes("{{BASE_PACKAGE}}.*")
            }
            excludes {
                // 부트스트랩 클래스는 커버리지 대상에서 제외
                classes("{{BASE_PACKAGE}}.{{CLASS_PREFIX}}Application*")
                // Spring AOT 생성 클래스(…__BeanDefinitions, …__TestContext*, …__AotRepository 등) 제외
                classes("*__*")
            }
        }
        verify {
            rule {
                minBound(30) // 라인 커버리지 30% 미만이면 check/build 실패 (현재 30.8% — 테스트 보강 시 상향)
            }
        }
    }
}

sonar {
    properties {
        // -PsonarProjectKey / -PsonarOrganization 으로 오버라이드 가능
        property("sonar.projectKey", providers.gradleProperty("sonarProjectKey")
            .getOrElse("{{SONAR_ORG}}_{{PROJECT_NAME}}"))
        property("sonar.organization", providers.gradleProperty("sonarOrganization")
            .getOrElse("{{SONAR_ORG}}"))
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.coverage.jacoco.xmlReportPaths",
            layout.buildDirectory.file("reports/kover/report.xml").get().asFile.path)
    }
}

// sonar 분석 전에 Kover XML 리포트 생성 보장
tasks.named("sonar") {
    dependsOn(tasks.named("koverXmlReport"))
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
    testImplementation(libs.archunit)
    testImplementation(libs.konsist)
    // Kotest: 코틀린 친화적 테스트 프레임워크(스펙 스타일 + 매처)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
    jvmToolchain(25)
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

        // Konsist 가 끌어오는 kotlin-compiler-embeddable jar 에는 jline native-image.properties 가
        // 번들돼 있으나, 그 properties 가 참조하는 reflection/resource-config.json 은 shading 시 누락돼 있다.
        // native-image 가 클래스패스의 native-image.properties 를 자동 로드하다
        // "Could not find reflection configuration resource ...jline-terminal/reflection-config.json" 으로
        // nativeTestCompile 초기화 단계에서 실패하므로, 해당 jar 의 내장 네이티브 설정을 통째로 무시한다.
        // (ArchitectureTest/KonsistTest 자체는 @DisabledInNativeImage 로 이미 네이티브 실행에서 제외됨)
        buildArgs.add("--exclude-config")
        buildArgs.add(".*kotlin-compiler-embeddable.*\\.jar")
        buildArgs.add("META-INF/native-image/.*")
    }
}
```

`gradle/libs.versions.toml`:

```toml
[versions]
kotlin = "2.4.0"
spring-boot = "4.1.0"
spring-dependency-management = "1.1.7"
hibernate = "7.4.3.Final"
graalvm-buildtools = "1.1.4"
archunit = "1.4.2"
konsist = "0.17.3"
kotest = "6.2.2"
pmd = "7.24.0"
kover = "0.9.8"
sonarqube = "7.3.1.8318"

[libraries]
# BOM 관리 (버전 생략 — io.spring.dependency-management가 버전 결정)
spring-boot-h2console = { module = "org.springframework.boot:spring-boot-h2console" }
spring-boot-starter-data-jpa = { module = "org.springframework.boot:spring-boot-starter-data-jpa" }
spring-boot-starter-webmvc = { module = "org.springframework.boot:spring-boot-starter-webmvc" }
spring-boot-devtools = { module = "org.springframework.boot:spring-boot-devtools" }
spring-boot-configuration-processor = { module = "org.springframework.boot:spring-boot-configuration-processor" }
spring-boot-starter-data-jpa-test = { module = "org.springframework.boot:spring-boot-starter-data-jpa-test" }
spring-boot-starter-webmvc-test = { module = "org.springframework.boot:spring-boot-starter-webmvc-test" }
kotlin-reflect = { module = "org.jetbrains.kotlin:kotlin-reflect" }
kotlin-test-junit5 = { module = "org.jetbrains.kotlin:kotlin-test-junit5" }
jackson-module-kotlin = { module = "tools.jackson.module:jackson-module-kotlin" }
h2 = { module = "com.h2database:h2" }
junit-platform-launcher = { module = "org.junit.platform:junit-platform-launcher" }
# 버전 명시
archunit = { module = "com.tngtech.archunit:archunit", version.ref = "archunit" }
konsist = { module = "com.lemonappdev:konsist", version.ref = "konsist" }
kotest-runner-junit5 = { module = "io.kotest:kotest-runner-junit5", version.ref = "kotest" }
kotest-assertions-core = { module = "io.kotest:kotest-assertions-core", version.ref = "kotest" }

[plugins]
kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
kotlin-spring = { id = "org.jetbrains.kotlin.plugin.spring", version.ref = "kotlin" }
kotlin-jpa = { id = "org.jetbrains.kotlin.plugin.jpa", version.ref = "kotlin" }
spring-boot = { id = "org.springframework.boot", version.ref = "spring-boot" }
spring-dependency-management = { id = "io.spring.dependency-management", version.ref = "spring-dependency-management" }
hibernate-orm = { id = "org.hibernate.orm", version.ref = "hibernate" }
graalvm-native = { id = "org.graalvm.buildtools.native", version.ref = "graalvm-buildtools" }
kover = { id = "org.jetbrains.kotlinx.kover", version.ref = "kover" }
sonarqube = { id = "org.sonarqube", version.ref = "sonarqube" }
```

## 6. Step 3 — Gradle Wrapper 생성

wrapper jar는 바이너리이므로 명령으로 생성한다:

```bash
mise exec gradle@9.6.1 -- gradle wrapper --gradle-version 9.6.1
```

(시스템에 gradle이 이미 있으면 `gradle wrapper --gradle-version 9.6.1`도 가능.)
결과: `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`, `gradle/wrapper/gradle-wrapper.properties`

## 7. Step 4 — git 지원 파일

`.gitignore`:

```
HELP.md
.gradle
build/
!gradle/wrapper/gradle-wrapper.jar
!**/src/main/**/build/
!**/src/test/**/build/

### STS ###
.apt_generated
.classpath
.factorypath
.project
.settings
.springBeans
.sts4-cache
bin/
!**/src/main/**/bin/
!**/src/test/**/bin/

### IntelliJ IDEA ###
.idea
*.iws
*.iml
*.ipr
out/
!**/src/main/**/out/
!**/src/test/**/out/

### NetBeans ###
/nbproject/private/
/nbbuild/
/dist/
/nbdist/
/.nb-gradle/

### VS Code ###
.vscode/

### Kotlin ###
.kotlin

### 로그 파일 ###
logs/
*.log

### 데이터베이스 ###
*.db
*.sqlite
*.h2.db

### OS별 파일 ###
.DS_Store
Thumbs.db
Desktop.ini

### claude code ###
.claude/
plans/

### jdx.mise ###
mise.toml
```

`.gitattributes`:

```
/gradlew text eol=lf
*.bat text eol=crlf
*.jar binary
```

`.gitmessage.txt`:

```
# <type>(<scope>): <subject>
# |<----    최대 50자 사용 맞추려고 노력하기       ---->|
# <body>
# |<----               각 줄을 최대 72자로 제한해야 한다.                ---->|
# <footer>
#
# --- COMMIT END ---

# Type can be
#    ✨ feat      - 새로운 기능
#    🐛 fix       - 버그 수정
#    ⚡ perf      - 성능 개선
#    💎 improve   - 코드 개선
#    ♻️  refactor  - 리팩토링
#    📝 docs      - 문서 수정
#    💄 style     - 코드 스타일 변경 (포맷팅, 세미콜론 등)
#    ✅ test      - 테스트 추가/수정
#    📦 chore     - 기타 변경사항 (빌드 스크립트, 패키지 매니저 등)
#    🎉 release   - 새 버전 릴리즈
#    🔨 build     - 빌드 시스템 또는 외부 의존성 변경
#    💚 ci        - CI 설정 파일 및 스크립트 변경
#    🔥 remove    - 코드/파일 삭제
#    ⏪ revert    - 커밋 되돌리기
#    🔒 security  - 보안 이슈 수정
#    🚨 hotfix    - 긴급 수정 (프로덕션 이슈)
#    ⬆️  upgrade   - 의존성 업그레이드
#    ⬇️  downgrade - 의존성 다운그레이드
# --------------------
# Scope can be
#    auth     - 인증 관련
#    api      - API 관련
#    ui       - UI/UX 관련
#    db       - 데이터베이스 관련
#    config   - 설정 관련
#    core     - 핵심 비즈니스 로직
#    util     - 유틸리티 함수
#    test     - 테스트 관련
#    deps     - 의존성 관련
#    infra    - 인프라 관련
#    admin    - 어드민 관련
#    user     - 사용자 기능 관련
# --------------------
# Subject Rules:
#    1. 명령문, 현재 시제 사용 ("changed" 대신 "change")
#    2. 첫 글자 소문자 사용
#    3. 마침표(.) 사용하지 않기
#    4. 50자 이내로 작성
# --------------------
# Body Rules:
#    1. 한 줄당 72자 이내로 작성
#    2. 어떻게(How)보다 무엇을(What), 왜(Why) 설명
#    3. 변경 이유와 이전과의 차이점 설명
#    4. 현재 시제 사용
# --------------------
# Footer Rules:
#    중대한 변경 사항: <중대한 변경 사항 설명>
#    See also: #<issue-number>, #<issue-number>
#    Co-authored-by: Kai Ahn <13996827+rkaehdaos@users.noreply.github.com>
#    Refs: [JIRA-XXX] #<GitHub issue-number>
```

`.github/PULL_REQUEST_TEMPLATE.md`:

```markdown
# Pull Request

## 관련 이슈 / Related Issue
Closes #

---

## PR 타입 / Change Type
<!-- 해당하는 항목에 체크 -->
- [ ] ✨ **Feature** - 새로운 기능 추가
- [ ] 🐛 **Bug Fix** - 버그 수정
- [ ] ♻️ **Refactor** - 코드 리팩토링
- [ ] 📝 **Docs** - 문서 수정
- [ ] ⚡ **Performance** - 성능 개선
- [ ] ✅ **Test** - 테스트 코드 추가/수정
- [ ] 🔧 **Chore** - 빌드 과정, 보조 도구 변경
- [ ] 🔒 **Security** - 보안 관련 변경

---

## 요약 / Summary
<!-- 이 PR에서 수행한 작업과 그 이유(무엇을, 왜)를 간단히 설명해 주세요 -->

## 변경사항 / Changes
<!-- 주요 변경사항을 bullet로 작성해 주세요 -->
-
-

---

## 테스트 / Testing
- [ ] `./gradlew build` 성공 (테스트 + PMD 포함)
- [ ] 수동 확인 완료 (필요 시 아래에 방법 기재)

<!-- 수동 확인이 필요한 경우 재현 방법을 적어 주세요 -->

---

## 체크리스트 / Checklist
- [ ] 커밋 메시지가 `.gitmessage.txt` 규칙을 따릅니다
- [ ] 코드 리뷰가 가능한 상태입니다 (Conflict 해결됨)
- [ ] 관련 문서(CLAUDE.md 등)를 갱신했습니다 (필요 시)
- [ ] DB 스키마·설정 변경을 검토했습니다 (필요 시)

---

## 추가 노트 / Additional Notes
<!-- 리뷰어가 알아야 할 참고사항, 후속 작업 등을 자유롭게 작성해 주세요 -->
```

`.github/pmd/ruleset.xml`:

```xml
<?xml version="1.0"?>
<ruleset name="Custom ruleset"
         xmlns="http://pmd.sourceforge.net/ruleset/2.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd">
    <description>
        메서드 길이를 30줄로 제한하는 custom ruleset
    </description>

    <!-- https://docs.pmd-code.org/latest/pmd_rules_java_design.html#ncsscount-->
    <rule ref="category/java/design.xml/NcssCount">
        <properties>
            <property name="methodReportLevel" value="30"/> <!-- NcssCount default 60 -->
            <property name="classReportLevel" value="1500"/> <!--default-->
        </properties>
    </rule>

</ruleset>
```

`.github/workflows/build.yml` (치환 불필요 — 그대로 복사):

```yaml
name: build

on:
  push:
    branches: [main]
  pull_request:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0 # Sonar 신규 코드/blame 분석에 전체 히스토리 필요
      - uses: actions/setup-java@v4
        with:
          distribution: graalvm # mise.toml(oracle-graalvm-25)과 정합
          java-version: '25'
      - uses: gradle/actions/setup-gradle@3f131e8634966bd73d06cc69884922b02e6faf92 # v6.2.0 — 서드파티 액션은 SHA 핀 고정(Sonar S7637)
      - name: Build & Test (koverVerify 포함)
        run: ./gradlew build koverXmlReport
      - name: SonarCloud 분석
        if: ${{ github.event_name == 'push' || github.event.pull_request.head.repo.full_name == github.repository }} # 포크 PR은 secret 없음
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        run: ./gradlew sonar
```

의존성 검증 메타데이터 생성(Sonar S8569/S6474 대응 — 생성된 `gradle/verification-metadata.xml`을 커밋):

```bash
./gradlew --write-verification-metadata sha256 clean build koverXmlReport
```

⚠️ **`--write-verification-metadata`는 그 실행에서 실제로 해석된 아티팩트만 기록한다.**
detached configuration에서 끌어오는 일부 `.pom`(대표적으로 `org.hibernate.orm:hibernate-platform`의
pom)은 로컬 Gradle 캐시 상태에 따라 해석이 생략되어 메타데이터에서 빠질 수 있다. 이 경우
로컬(warm cache)에서는 빌드가 통과하지만 CI(cold cache)에서만
`DependencyVerificationException: One artifact failed verification: hibernate-platform-<버전>.pom`
으로 실패한다. 따라서 생성 직후 **`.module`뿐 아니라 `.pom` 항목까지 존재하는지** 반드시 확인한다:

```bash
grep "hibernate-platform-<버전>.pom" gradle/verification-metadata.xml   # 예: 7.4.3.Final
```

항목이 없으면 Maven Central의 공식 sha256과 직접 계산값을 대조한 뒤 해당 component 블록에
`<artifact>` 항목을 수동으로 추가한다:

```bash
V=7.4.3.Final
BASE=https://repo.maven.apache.org/maven2/org/hibernate/orm/hibernate-platform/$V/hibernate-platform-$V.pom
curl -s "$BASE" | shasum -a 256          # 계산값
curl -s "$BASE.sha256"                    # Maven Central 공식 게시값 — 위와 일치해야 함
```

`verification-metadata.xml`의 `hibernate-platform` component 블록에 아래처럼 추가(`.module` 항목 형식 참고):

```xml
<artifact name="hibernate-platform-7.4.3.Final.pom">
   <sha256 value="61c0faadd73127c2d80381b86d2426625429490651f4f8b05fbabc5e116ff27f" origin="Maven Central published checksum"/>
</artifact>
```

## 8. Step 5 — 메인 소스 코드

아래에서 `{{BASE_PACKAGE}}` = `{{GROUP}}.{{PACKAGE_SEGMENT}}` (예: `dev.haja.demoservice`).
디렉토리는 `src/main/kotlin/` 하위에 패키지 경로대로 생성.

`src/main/kotlin/.../{{CLASS_PREFIX}}Application.kt`:

```kotlin
package {{BASE_PACKAGE}}

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class {{CLASS_PREFIX}}Application

fun main(args: Array<String>) {
    runApplication<{{CLASS_PREFIX}}Application>(*args)
}
```

`src/main/kotlin/.../domain/Memo.kt`:

```kotlin
package {{BASE_PACKAGE}}.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

/**
 * 샘플 도메인 엔티티.
 * - allOpen 플러그인이 @Entity 클래스를 open 처리 (build.gradle.kts 참조)
 * - kotlin("plugin.jpa")가 no-arg 생성자 생성
 * - 도메인 계층은 다른 계층/Spring Web 에 의존하지 않는다 (ArchitectureTest 에서 검증)
 */
@Entity
class Memo(
    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var content: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
)
```

`src/main/kotlin/.../repository/MemoRepository.kt`:

```kotlin
package {{BASE_PACKAGE}}.repository

import {{BASE_PACKAGE}}.domain.Memo
import org.springframework.data.jpa.repository.JpaRepository

interface MemoRepository : JpaRepository<Memo, Long>
```

`src/main/kotlin/.../service/MemoService.kt`:

```kotlin
package {{BASE_PACKAGE}}.service

import {{BASE_PACKAGE}}.domain.Memo
import {{BASE_PACKAGE}}.repository.MemoRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/** 존재하지 않는 메모 조회 시 발생 — GlobalExceptionHandler 에서 404 로 매핑 */
class MemoNotFoundException(id: Long) : RuntimeException("Memo not found: id=$id")

/** 생성자 주입만 사용 (필드 @Autowired 금지 — KonsistTest 에서 검증) */
@Service
@Transactional(readOnly = true)
class MemoService(
    private val memoRepository: MemoRepository,
) {
    fun findAll(): List<Memo> = memoRepository.findAll()

    fun findById(id: Long): Memo =
        memoRepository.findByIdOrNull(id)
            ?: throw MemoNotFoundException(id)

    @Transactional
    fun create(title: String, content: String): Memo =
        memoRepository.save(Memo(title = title, content = content))
}
```

`src/main/kotlin/.../controller/MemoController.kt`:

```kotlin
package {{BASE_PACKAGE}}.controller

import {{BASE_PACKAGE}}.domain.Memo
import {{BASE_PACKAGE}}.service.MemoNotFoundException
import {{BASE_PACKAGE}}.service.MemoService
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestController
@RequestMapping("/api/memos")
class MemoController(
    private val memoService: MemoService,
) {
    @GetMapping
    fun list(): List<MemoResponse> = memoService.findAll().map(MemoResponse::from)

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): MemoResponse =
        MemoResponse.from(memoService.findById(id))

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: MemoCreateRequest): MemoResponse =
        MemoResponse.from(memoService.create(request.title, request.content))
}

/** 존재하지 않는 리소스 조회 시 500 대신 404 + ProblemDetail(RFC 7807) 응답 */
@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MemoNotFoundException::class)
    fun handleNotFound(e: MemoNotFoundException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message ?: "Not Found")
}

/** DTO 는 data class 로 작성 (KonsistTest 에서 검증) */
data class MemoCreateRequest(val title: String, val content: String)

data class MemoResponse(val id: Long, val title: String, val content: String) {
    companion object {
        fun from(memo: Memo) = MemoResponse(
            id = requireNotNull(memo.id),
            title = memo.title,
            content = memo.content,
        )
    }
}
```

`src/main/resources/application.yaml`:

```yaml
spring:
  application:
    name: {{PROJECT_NAME}}
```

## 9. Step 6 — 테스트 코드

`src/test/kotlin/.../{{CLASS_PREFIX}}ApplicationTests.kt`:

```kotlin
package {{BASE_PACKAGE}}

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class {{CLASS_PREFIX}}ApplicationTests {

    @Test
    fun contextLoads() {
    }

}
```

`src/test/kotlin/.../controller/MemoControllerTest.kt`:

```kotlin
package {{BASE_PACKAGE}}.controller

import {{BASE_PACKAGE}}.service.MemoNotFoundException
import {{BASE_PACKAGE}}.service.MemoService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@DisabledInNativeImage // Mockito(@MockitoBean)는 런타임 바이트코드 생성이 필요해 네이티브 이미지에서 동작 불가
@WebMvcTest(MemoController::class)
class MemoControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
) {
    @MockitoBean
    private lateinit var memoService: MemoService

    @Test
    fun `존재하지 않는 메모 조회는 404 ProblemDetail 응답`() {
        given(memoService.findById(999L))
            .willThrow(MemoNotFoundException(999))

        mockMvc.perform(get("/api/memos/999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.detail").value("Memo not found: id=999"))
    }
}
```

`src/test/kotlin/.../architecture/ArchitectureTest.kt`
(⚠️ 문자열 리터럴 2곳의 `{{BASE_PACKAGE}}`도 반드시 치환):

```kotlin
package {{BASE_PACKAGE}}.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import jakarta.persistence.Entity
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RestController

/**
 * [역할 분담]
 * ArchUnit : 바이트코드 레벨 — 계층 의존 방향, 순환 금지, 어노테이션 위치 등 "구조" 규칙
 * Konsist  : 코틀린 소스 레벨 — 네이밍, 패키지-경로 일치, data class, 생성자 주입 등 "컨벤션" 규칙
 * (KonsistTest.kt 참조 — 두 도구 간 규칙 중복 금지)
 *
 * [네이티브 이미지 미지원]
 * ArchUnit 은 클래스패스의 .class 바이트코드를 런타임에 읽어 분석하므로 네이티브 이미지에서 동작 불가하다
 * (네이티브 이미지에는 .class 파일이 존재하지 않음). 따라서 @DisabledInNativeImage 로 제외한다.
 * ArchUnit 전용 엔진(@AnalyzeClasses/@ArchTest)은 Jupiter 의 조건부 실행을 평가하지 않으므로,
 * 일반 JUnit Jupiter @Test + ArchUnit core API 방식으로 작성하여 @DisabledInNativeImage 가 적용되게 한다.
 */
@DisabledInNativeImage
class ArchitectureTest {

    // 분석 대상 클래스는 한 번만 임포트하여 각 테스트에서 재사용 (테스트 클래스 제외)
    private val importedClasses: JavaClasses = ClassFileImporter()
        .withImportOption(ImportOption.DoNotIncludeTests())
        .importPackages("{{BASE_PACKAGE}}")

    // 계층 규칙: controller → service → repository → domain (단방향)
    // consideringOnlyDependenciesInLayers() 로 stdlib/프레임워크 의존은 무시
    // 참고: "controller 가 repository 직접 접근 금지" 는 아래 Repository 규칙에 포함됨 (중복 규칙 없음)
    @Test
    fun `계층 의존은 단방향이다`() {
        layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Controller").definedBy("..controller..")
            .layer("Service").definedBy("..service..")
            .layer("Repository").definedBy("..repository..")
            .layer("Domain").definedBy("..domain..")
            .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
            .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
            .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Controller", "Service", "Repository")
            .check(importedClasses)
    }

    // 최상위 하위 패키지 간 순환 의존 금지
    @Test
    fun `패키지 간 순환 의존이 없다`() {
        slices()
            .matching("{{BASE_PACKAGE}}.(*)..")
            .should().beFreeOfCycles()
            .check(importedClasses)
    }

    @Test
    fun `@Service 는 service 패키지에 위치한다`() {
        classes()
            .that().areAnnotatedWith(Service::class.java)
            .should().resideInAPackage("..service..")
            .check(importedClasses)
    }

    @Test
    fun `@RestController 는 controller 패키지에 위치한다`() {
        classes()
            .that().areAnnotatedWith(RestController::class.java)
            .should().resideInAPackage("..controller..")
            .check(importedClasses)
    }

    @Test
    fun `@Entity 는 domain 패키지에 위치한다`() {
        classes()
            .that().areAnnotatedWith(Entity::class.java)
            .should().resideInAPackage("..domain..")
            .check(importedClasses)
    }

    // 도메인은 순수하게: 다른 계층 및 Spring Web/스테레오타입에 의존 금지
    @Test
    fun `domain 은 다른 계층과 Spring Web 에 의존하지 않는다`() {
        noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "..controller..", "..service..", "..repository..",
                "org.springframework.web..", "org.springframework.stereotype..",
            )
            .check(importedClasses)
    }
}
```

`src/test/kotlin/.../architecture/KonsistTest.kt`:

```kotlin
package {{BASE_PACKAGE}}.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withAnnotationOf
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RestController

/**
 * [역할 분담] Konsist: 코틀린 소스 레벨 컨벤션 규칙 (구조 규칙은 ArchitectureTest.kt = ArchUnit 담당)
 *
 * 주의:
 * - Konsist 는 소스 파일을 직접 파싱하므로 프로젝트 루트에서 실행되어야 한다 (Gradle test 기본 동작 OK).
 * - assertTrue/assertFalse 는 선언 리스트가 비어 있으면 예외를 던진다. 샘플 코드(Memo, MemoService,
 *   MemoController 등)가 존재하므로 문제없다. 샘플 삭제 시 해당 규칙도 함께 정리할 것.
 */
@DisabledInNativeImage // Konsist 는 네이티브 이미지에서 동작 불가 (파일 파싱/리플렉션)
class KonsistTest {

    // scope 생성은 파일 시스템 전체를 파싱하므로 필드로 한 번만 초기화해 공유한다.
    private val production = Konsist.scopeFromProduction()
    private val project = Konsist.scopeFromProject()
    private val test = Konsist.scopeFromTest()

    @Test
    fun `@Service 클래스는 이름이 Service 로 끝난다`() {
        production.classes()
            .withAnnotationOf(Service::class)
            .assertTrue { it.name.endsWith("Service") }
    }

    @Test
    fun `@RestController 클래스는 이름이 Controller 로 끝난다`() {
        production.classes()
            .withAnnotationOf(RestController::class)
            .assertTrue { it.name.endsWith("Controller") }
    }

    @Test
    fun `JpaRepository 상속 인터페이스는 이름이 Repository 로 끝난다`() {
        production.interfaces()
            .filter { it.hasParentOf(JpaRepository::class, indirectParents = true) }
            .assertTrue { it.name.endsWith("Repository") }
    }

    @Test
    fun `패키지 선언은 디렉터리 경로와 일치한다`() {
        project
            .packages
            .assertTrue { it.hasMatchingPath }
    }

    @Test
    fun `필드 주입 금지 - @Autowired 프로퍼티 없음 (생성자 주입만 허용)`() {
        project
            .properties()
            .assertFalse { it.hasAnnotationOf(Autowired::class) }
    }

    @Test
    fun `DTO(Request-Response 접미사)는 data class 로 작성한다`() {
        production.classes()
            .withNameEndingWith("Request", "Response")
            .assertTrue { it.hasDataModifier }
    }

    @Test
    fun `와일드카드 import 금지`() {
        project
            .files
            .flatMap { it.imports }
            .assertFalse { it.isWildcard }
    }

    @Test
    fun `테스트 클래스 이름은 Test 또는 Tests 로 끝난다`() {
        // Kotest 스펙은 `@Test`를 쓰지 않으므로 이 규칙에 걸리지 않는다.
        test
            .classes()
            .filter { cls -> cls.functions().any { it.hasAnnotationOf(Test::class) } }
            .assertTrue { it.name.endsWith("Test") || it.name.endsWith("Tests") }
    }
}
```

## 10. Step 7 — CLAUDE.md

프로젝트 루트에 `CLAUDE.md` 생성:

```markdown
# CLAUDE.md

이 저장소에서 작업할 때 반드시 지켜야 할 규칙과 컨텍스트.

## 프로젝트 개요

Spring Boot **4.1.0** + Kotlin **2.4.0** + JDK **25(GraalVM)** 기반 프로젝트.

- 빌드: Gradle 9.6.1 (Kotlin DSL) + 버전 카탈로그 `gradle/libs.versions.toml`
- DB: H2 (in-memory) + Spring Data JPA
- 웹: Spring MVC (`spring-boot-starter-webmvc`)
- 네이티브 이미지: GraalVM Native Build Tools 지원
- 정적분석: PMD 7.24.0 (커스텀 룰셋 `.github/pmd/ruleset.xml`, 메서드 NCSS 30줄 제한, main 소스셋만)
- 커버리지: Kover (라인 30% 미만이면 check 실패) + SonarCloud 연동 (`.github/workflows/build.yml`, `SONAR_TOKEN` secret 필요)
- 샘플 도메인: `Memo` (최소 CRUD — 아키텍처 규칙 예시용)

## 필수 명령

```bash
mise install              # oracle-graalvm-25.0.3 설치 (mise.toml)
./gradlew build           # 컴파일 + 전체 테스트 + PMD
./gradlew test            # 테스트만
./gradlew koverHtmlReport # 커버리지 HTML 리포트 (build/reports/kover/html)
./gradlew sonar           # SonarCloud 분석 (SONAR_TOKEN 필요)
./gradlew bootRun         # 로컬 실행 (http://localhost:8080)
./gradlew nativeCompile   # (선택) 네이티브 실행 파일 빌드 — 장시간 소요
./gradlew nativeTest      # (선택) 네이티브 이미지 테스트
```

## 아키텍처 (ArchitectureTest.kt가 강제)

베이스 패키지: `{{BASE_PACKAGE}}`

계층은 단방향 의존만 허용: `controller → service → repository → domain`

- `domain`은 순수 계층 — 다른 계층 및 Spring Web/stereotype 의존 금지
- 패키지 간 순환 의존 금지
- `@Service`/`@RestController`/`@Entity`는 각각 service/controller/domain 패키지에만 위치

## 코드 컨벤션 (KonsistTest.kt가 강제)

- **생성자 주입만** 사용 — `@Autowired` 필드/프로퍼티 주입 금지
- DTO는 `Request`/`Response` 접미사 + **data class**
- `@Service` → 이름 `*Service`, `@RestController` → `*Controller`, JpaRepository 상속 → `*Repository`
- 와일드카드 import 금지
- 패키지 선언은 디렉터리 경로와 일치
- 테스트 클래스 이름은 `Test`/`Tests`로 끝남 (Kotest 스펙 제외)

## 커밋 / PR 컨벤션

- 커밋 메시지는 `.gitmessage.txt` 규칙: `<이모지 type>(<scope>): <subject>` (예: `✨ feat(api): ...`), subject 50자 이내·소문자 시작·마침표 금지, body 72자/줄
- PR은 `.github/PULL_REQUEST_TEMPLATE.md` 준수

## 주의사항

- **Spring Boot 4.x 신규 명칭을 3.x 스타일로 "교정"하지 말 것**:
  - `spring-boot-starter-webmvc` (3.x의 `starter-web` 아님), `spring-boot-h2console`
  - `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`
  - `org.springframework.test.context.bean.override.mockito.MockitoBean`
  - Jackson 3: `tools.jackson.module:jackson-module-kotlin` (`com.fasterxml` 아님)
- 의존성 버전은 반드시 `gradle/libs.versions.toml` 버전 카탈로그로 관리
- `build.gradle.kts`의 GraalVM `buildArgs`와 주석은 네이티브 빌드 실패 회피용 — 임의 삭제 금지
- Mockito 기반 테스트(`@MockitoBean`/`@WebMvcTest` 등)는 런타임 바이트코드 생성이 필요해 네이티브 이미지(`nativeTest`)에서 동작 불가 → `@DisabledInNativeImage` 필수 (ArchUnit/Konsist 테스트도 동일)
- 샘플 `Memo` 도메인 삭제 시 KonsistTest 규칙도 함께 정리할 것 (Konsist `assertTrue`는 빈 리스트에서 예외 발생)
- `mise.toml`, `HELP.md`는 `.gitignore` 대상 (커밋되지 않는 것이 정상)
```

## 11. Step 8 — git 초기화

```bash
git init -b main
git config commit.template .gitmessage.txt
git add -A
git commit -m "🎉 release(config): initialize {{PROJECT_NAME}} 프로젝트"
```

(`mise.toml`은 `.gitignore` 대상이라 커밋에 포함되지 않는다 — 정상.)

## 12. 검증 (반드시 수행)

1. `mise exec -- java -version` → GraalVM 25 확인
2. `./gradlew build` → **BUILD SUCCESSFUL** + 테스트 16개 전체 통과
   (contextLoads 1 + MemoControllerTest 1 + ArchitectureTest 6 + KonsistTest 8), PMD 위반 0,
   koverVerify(라인 30% 기준) 통과
3. `./gradlew koverHtmlReport` → `build/reports/kover/html/index.html` 생성 확인
4. ⚠️ `./gradlew sonar`는 부트스트랩 검증에서 **실행하지 마라** — SonarCloud 프로젝트
   import(Automatic Analysis 비활성화) 및 GitHub secret `SONAR_TOKEN` 등록이 선행돼야 하는
   수동 조치이며, 없으면 실패하는 것이 정상이다
5. 스모크 테스트:
   ```bash
   ./gradlew bootRun &   # 기동 대기: 아래 curl이 응답할 때까지 2초 간격 폴링 (최대 60초)
   until curl -sf localhost:8080/api/memos > /dev/null; do sleep 2; done
   curl -s -X POST localhost:8080/api/memos -H 'Content-Type: application/json' \
        -d '{"title":"t","content":"c"}'          # 201 + JSON 응답
   curl -s localhost:8080/api/memos              # 목록에 1건
   curl -s -o /dev/null -w '%{http_code}' localhost:8080/api/memos/999   # 404
   ```
   확인 후 bootRun 종료.
6. (선택, 장시간) `./gradlew nativeCompile`
7. (선택, 장시간) `./gradlew nativeTest` → **BUILD SUCCESSFUL**. 단, 네이티브 이미지에서는
   `contextLoads`(`{{CLASS_PREFIX}}ApplicationTests`) 1개만 실행/통과하고
   `MemoControllerTest`(Mockito)·`ArchitectureTest`·`KonsistTest`는 모두
   `@DisabledInNativeImage`로 **스킵되는 것이 정상**이다. 스킵을 실패로 오인해
   어노테이션을 제거하지 마라 — Mockito/ArchUnit/Konsist는 런타임 바이트코드
   생성·`.class` 파싱에 의존해 네이티브 이미지에서 근본적으로 동작할 수 없다.

빌드 실패 시: 에러를 읽고 수정하되, **0장의 규칙(최신 명칭 교정 금지)을 위반하는 방향의
수정은 절대 하지 마라.** 대부분의 실패 원인은 placeholder 미치환 또는 오타다.

**CI에서만 `DependencyVerificationException: One artifact failed verification: ...pom` 발생 시**:
코드 문제가 아니라 검증 메타데이터 누락이다(§7 참고 — 로컬 warm cache에서는 해당 `.pom`이
해석되지 않아 메타데이터에 기록되지 않았을 수 있다). **`verify-metadata`를 끄거나
`verification-metadata.xml`을 삭제하는 방향으로 "해결"하지 마라.** 올바른 대처는 Maven Central
공식 sha256을 대조한 뒤 누락된 `.pom` artifact 항목을 해당 component 블록에 수동 추가하는 것이다.

## 13. 주의사항 부록

- `Memo`, `MemoService`, `MemoController` 등은 **샘플 도메인**이며 프로젝트 이름이 아니다. 치환 금지.
- Konsist 테스트는 샘플 코드 존재에 의존한다 (빈 리스트에서 `assertTrue` 예외). 샘플 삭제 시 해당 규칙도 함께 정리.
- `ArchitectureTest.kt`의 문자열 리터럴(`importPackages`, `slices().matching`) 안의 패키지명 치환을 빠뜨리면 아키텍처 테스트가 빈 클래스 집합을 검사하게 되므로 반드시 확인.
- 이슈 템플릿은 이 템플릿에 포함되지 않는다 — 생성하지 마라. (CI 워크플로우 `build.yml`은 포함 대상.)
- Kover 필터의 `*__*` 제외는 Spring AOT 생성 클래스(`__BeanDefinitions`, `__TestContext*` 등)가 분모를 부풀려 커버리지를 왜곡(실측 0.25%까지 하락)하는 것을 막는 설정 — 임의 삭제 금지.
- `minBound(30)`은 샘플 코드 실측 커버리지(30.8%) 기준 — 테스트 보강 시 상향할 것.
- Mockito 기반 테스트(`@MockitoBean`/`@WebMvcTest` 등)는 런타임에 ByteBuddy로 동적 바이트코드를 생성하므로 GraalVM 네이티브 이미지에서 동작 불가하다. `nativeTest` 실행 시 AOT 컨텍스트 초기화 중 Mockito 클래스 초기화 실패(`NoClassDefFoundError`)로 `ApplicationContext` 로드가 깨진다. 따라서 `MemoControllerTest`에는 `@DisabledInNativeImage`가 반드시 부여돼 있어야 하며(JVM `test` 태스크에서는 계속 실행되어 커버리지에 영향 없음), 이 어노테이션을 임의 제거하지 마라. 이후 추가되는 Mockito/목 기반 테스트도 동일하게 처리할 것.
- `gradle/verification-metadata.xml`은 로컬에서 완전해 보여도 CI cold cache에서만 누락이 드러날 수 있다. `--write-verification-metadata`는 실행 시 실제 해석된 아티팩트만 기록하므로, detached configuration의 `.pom`(예: `hibernate-platform`)이 로컬에서 캐시로만 충족되면 메타데이터에서 빠진다. 생성 후 §7의 grep 확인 절차를 반드시 거치고, CI에서 pom 검증 실패가 나면 검증을 끄지 말고 공식 sha256으로 항목을 보강할 것(§7·§12 참고).
