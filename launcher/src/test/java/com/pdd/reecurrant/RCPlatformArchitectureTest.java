package com.pdd.reecurrant;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.Architectures;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

public class RCPlatformArchitectureTest {

    private final String basePackage = RCPlatformArchitectureTest.class.getPackageName();

    private final JavaClasses importedClasses = new ClassFileImporter().importPackages(basePackage);

    @Test
    @DisplayName("Arch tests - repositories used only in infrastructure layer")
    public void testRepositoriesUsedInInfrastructureOnly() {
        ArchRule rule = ArchRuleDefinition.classes()
                .that()
                .resideInAPackage("..repository..")
                .should()
                .onlyBeAccessed()
                .byAnyPackage("..infrastructure..");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Arch tests - no package cycles")
    public void testNoPackageCycles() {
        ArchRule rule = SlicesRuleDefinition.slices().matching(basePackage + ".(**)..").should().beFreeOfCycles();

        rule.check(importedClasses);
    }

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

    @Test
    @DisplayName("Arch tests - layer dependencies are respected")
    public void testAppLayersAreRespected() {
        ArchRule rule = Architectures.layeredArchitecture().consideringOnlyDependenciesInLayers()
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

    @Test
    @DisplayName("Arch tests - repositories should have @Repository annotation")
    public void testRepositoryAnnotationAreRespected() {
        ArchRule rule = ArchRuleDefinition.classes()
                .that()
                .resideInAPackage(basePackage + ".infrastructure.repository..")
                .should()
                .beAnnotatedWith(Repository.class);

        rule.check(importedClasses);
    }

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

}
