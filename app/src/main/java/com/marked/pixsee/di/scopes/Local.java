package com.marked.pixsee.di.scopes;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by Tudor on 25-Jun-16.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Local {
}
