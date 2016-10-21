package com.marked.pixsee.injection.scopes;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by Tudor on 21-Oct-16.
 */
@Scope
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Instance {
}
