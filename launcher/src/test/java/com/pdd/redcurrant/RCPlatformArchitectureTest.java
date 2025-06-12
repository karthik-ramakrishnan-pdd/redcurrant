package com.pdd.redcurrant;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.Architectures;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

/**
 * Architecture tests for validating structural and layer constraints in the RC Platform
 * application.
 * <p>
 * These tests ensure the application follows domain-driven design and clean architecture
 * rules, such as package boundaries, class annotations, naming conventions, and layer
 * dependencies.
 */
public class RCPlatformArchitectureTest {

    /**
     * Base package of the project used to scope ArchUnit rule checks.
     */
    private final String basePackage = RCPlatformArchitectureTest.class.getPackageName();

    /**
     * Imported Java classes from the base package for ArchUnit checks.
     */
    private final JavaClasses importedClasses = new ClassFileImporter().importPackages(basePackage);

    /**
     * Ensures there are no cyclic dependencies across packages.
     */
    @Test
    @DisplayName("Arch tests - no package cycles")
    public void testNoPackageCycles() {
        ArchRule rule = SlicesRuleDefinition.slices().matching(basePackage + ".(**)..").should().beFreeOfCycles();
        rule.check(importedClasses);
    }

    /**
     * Ensures that the domain layer does not access the application or infrastructure
     * layers.
     */
    @Test
    @DisplayName("Arch tests - domain doesn't access application and infrastructure")
    public void testDomainDoesNotAccessApplicationAndInfrastructure() {
        ArchRule rule = ArchRuleDefinition.noClasses()
            .that()
            .resideInAPackage(basePackage + ".domain..")
            .should()
            .accessClassesThat()
            .resideInAnyPackage(basePackage + ".application..", basePackage + ".infrastructure..");

        rule.check(importedClasses);
    }

    /**
     * Ensures that the application layer does not access the infrastructure layer or SPI
     * ports.
     */
    @Test
    @DisplayName("Arch tests - application doesn't access infrastructure")
    public void testApplicationDoesNotAccessInfrastructure() {
        ArchRule rule = ArchRuleDefinition.noClasses()
            .that()
            .resideInAPackage(basePackage + ".application..")
            .should()
            .accessClassesThat()
            .resideInAPackage(basePackage + ".infrastructure..")
            .orShould()
            .accessClassesThat()
            .resideInAPackage(basePackage + ".domain.ports.spi..");

        rule.check(importedClasses);
    }

    /**
     * Ensures that the infrastructure layer does not access the application layer or API
     * ports.
     */
    @Test
    @DisplayName("Arch tests - infrastructure doesn't access application")
    public void testInfrastructureDoesNotAccessApplication() {
        ArchRule rule = ArchRuleDefinition.noClasses()
            .that()
            .resideInAPackage(basePackage + ".infrastructure..")
            .should()
            .accessClassesThat()
            .resideInAPackage(basePackage + ".application..")
            .orShould()
            .accessClassesThat()
            .resideInAPackage(basePackage + ".domain.ports.api..");

        rule.check(importedClasses);
    }

    /**
     * Enforces proper layer access rules based on a clean architecture: Controller →
     * Service → Adapter → Config.
     */
    @Test
    @DisplayName("Arch tests - layer dependencies are respected")
    public void testAppLayersAreRespected() {
        ArchRule rule = Architectures.layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Controller")
            .definedBy(basePackage + ".application.controller..")
            .layer("Service")
            .definedBy(basePackage + ".domain.service..")
            .layer("Adapter")
            .definedBy(basePackage + ".infrastructure.adapters..")
            .layer("Config")
            .definedBy(basePackage + ".configuration..")
            .whereLayer("Controller")
            .mayNotBeAccessedByAnyLayer()
            .whereLayer("Service")
            .mayOnlyBeAccessedByLayers("Controller", "Config")
            .whereLayer("Adapter")
            .mayOnlyBeAccessedByLayers("Service", "Config");

        rule.check(importedClasses);
    }

    /**
     * Ensures that adapter classes are annotated with {@link Service} and have names
     * ending in 'Adapter'.
     */
    @Test
    @DisplayName("Arch tests - adapters should have @Service annotation and end with 'Adapter' suffix")
    public void testAdaptersAnnotationAreRespected() {
        ArchRule rule = ArchRuleDefinition.classes()
            .that()
            .resideInAPackage(basePackage + ".infrastructure.adapters..")
            .should()
            .beAnnotatedWith(Service.class)
            .andShould()
            .haveSimpleNameEndingWith("Adapter");

        rule.check(importedClasses);
    }

    /**
     * Ensures that all ports are interfaces and their names end with 'Port'.
     */
    @Test
    @DisplayName("Arch tests - ports should have a 'Port' suffix")
    public void testPortNamingAreRespected() {
        ArchRule rule = ArchRuleDefinition.classes()
            .that()
            .resideInAPackage(basePackage + ".domain.ports..")
            .should()
            .haveSimpleNameEndingWith("Port")
            .andShould()
            .beInterfaces();

        rule.check(importedClasses);
    }

    /**
     * Ensures that all Feign clients are placed inside the 'feignclient' package.
     */
    @Test
    @DisplayName("Arch tests - feign clients should be inside 'feignclient' package")
    public void testFeignClientPackageAreRespected() {
        ArchRule rule = ArchRuleDefinition.classes()
            .that()
            .areAnnotatedWith(FeignClient.class)
            .should()
            .resideInAPackage("..feignclient..(*)");

        rule.allowEmptyShould(true).check(importedClasses);
    }

    /**
     * Ensures that all DTOs are placed in the 'domain.data' package and their names end
     * with 'Dto'.
     */
    @Test
    @DisplayName("Arch tests - DTO should have a 'Dto' suffix")
    public void testDtoNamingAreRespected() {
        ArchRule rule = ArchRuleDefinition.classes()
            .that()
            .resideInAPackage(basePackage + ".domain.data..")
            .and()
            .areNotAnnotatedWith("lombok.Generated")
            .should()
            .haveSimpleNameEndingWith("Dto");

        rule.check(importedClasses);
    }

    /**
     * Ensures that all annotations are placed only inside the 'application.annotations'
     * or 'domain.annotations' packages.
     */
    @Test
    @DisplayName("Arch tests - Annotations should only be in 'domain.annotations' package")
    void annotationsShouldOnlyExistInDomainAnnotationsPackage() {
        ArchRule rule = ArchRuleDefinition.classes()
            .that()
            .areAnnotations()
            .should()
            .resideInAPackage(basePackage + ".application.annotations..")
            .orShould()
            .resideInAPackage(basePackage + ".domain.annotations..");

        rule.check(importedClasses);
    }

    /**
     * Ensures that annotation class names under the annotation packages do not end with
     * 'Dto'.
     */
    @Test
    @DisplayName("Arch tests - Annotations in 'domain.annotations' should not end with 'Dto'")
    void annotationsInDomainAnnotationsShouldNotEndWithDto() {
        ArchRule rule = ArchRuleDefinition.classes()
            .that()
            .resideInAPackage(basePackage + ".application.annotations..")
            .or()
            .resideInAPackage(basePackage + ".domain.annotations..")
            .and()
            .areAnnotations()
            .should()
            .haveSimpleNameNotEndingWith("Dto");

        rule.check(importedClasses);
    }

}
