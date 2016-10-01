package com.pixsee.di.scopes;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by Tudor Pop on 19-Mar-16.
 * Scope tutorial at the following link
 * https://frogermcs.github.io/dependency-injection-with-dagger-2-custom-scopes/
 */
@Scope
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityScope {
}
