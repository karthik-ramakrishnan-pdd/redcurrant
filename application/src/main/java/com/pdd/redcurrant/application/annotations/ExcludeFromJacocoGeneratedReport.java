package com.pdd.redcurrant.application.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to exclude a method or class from jacoco reports. Using this annotation
 * to exclude coverage is not recommended.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ExcludeFromJacocoGeneratedReport {

}