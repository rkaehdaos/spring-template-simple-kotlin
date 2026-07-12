package dev.haja.springtemplatesimplekotlin.architecture

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import jakarta.persistence.Entity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RestController

/**
 * [역할 분담]
 * ArchUnit : 바이트코드 레벨 — 계층 의존 방향, 순환 금지, 어노테이션 위치 등 "구조" 규칙
 * Konsist  : 코틀린 소스 레벨 — 네이밍, 패키지-경로 일치, data class, 생성자 주입 등 "컨벤션" 규칙
 * (KonsistTest.kt 참조 — 두 도구 간 규칙 중복 금지)
 */
@AnalyzeClasses(
    packages = ["dev.haja.springtemplatesimplekotlin"],
    importOptions = [ImportOption.DoNotIncludeTests::class],
)
class ArchitectureTest {

    // 계층 규칙: controller → service → repository → domain (단방향)
    // consideringOnlyDependenciesInLayers() 로 stdlib/프레임워크 의존은 무시
    // 참고: "controller 가 repository 직접 접근 금지" 는 아래 Repository 규칙에 포함됨 (중복 규칙 없음)
    @ArchTest
    val layerDependenciesAreOneWay: ArchRule = layeredArchitecture()
        .consideringOnlyDependenciesInLayers()
        .layer("Controller").definedBy("..controller..")
        .layer("Service").definedBy("..service..")
        .layer("Repository").definedBy("..repository..")
        .layer("Domain").definedBy("..domain..")
        .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
        .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
        .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
        .whereLayer("Domain").mayOnlyBeAccessedByLayers("Controller", "Service", "Repository")

    // 최상위 하위 패키지 간 순환 의존 금지
    @ArchTest
    val noPackageCycles: ArchRule = slices()
        .matching("dev.haja.springtemplatesimplekotlin.(*)..")
        .should().beFreeOfCycles()

    // 어노테이션 위치 규칙
    @ArchTest
    val servicesResideInServicePackage: ArchRule = classes()
        .that().areAnnotatedWith(Service::class.java)
        .should().resideInAPackage("..service..")

    @ArchTest
    val controllersResideInControllerPackage: ArchRule = classes()
        .that().areAnnotatedWith(RestController::class.java)
        .should().resideInAPackage("..controller..")

    @ArchTest
    val entitiesResideInDomainPackage: ArchRule = classes()
        .that().areAnnotatedWith(Entity::class.java)
        .should().resideInAPackage("..domain..")

    // 도메인은 순수하게: 다른 계층 및 Spring Web/스테레오타입에 의존 금지
    @ArchTest
    val domainDependsOnNothing: ArchRule = noClasses()
        .that().resideInAPackage("..domain..")
        .should().dependOnClassesThat().resideInAnyPackage(
            "..controller..", "..service..", "..repository..",
            "org.springframework.web..", "org.springframework.stereotype..",
        )
}
