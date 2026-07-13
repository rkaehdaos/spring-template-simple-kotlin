# CLAUDE.md

이 저장소에서 작업할 때 반드시 지켜야 할 규칙과 컨텍스트.

## 프로젝트 개요

Spring Boot **4.1.0** + Kotlin **2.4.0** + JDK **25(GraalVM)** 기반 템플릿 프로젝트.

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

베이스 패키지: `dev.haja.springtemplatesimplekotlin`

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
- 샘플 `Memo` 도메인 삭제 시 KonsistTest 규칙도 함께 정리할 것 (Konsist `assertTrue`는 빈 리스트에서 예외 발생)
- `mise.toml`, `HELP.md`는 `.gitignore` 대상 (커밋되지 않는 것이 정상)
