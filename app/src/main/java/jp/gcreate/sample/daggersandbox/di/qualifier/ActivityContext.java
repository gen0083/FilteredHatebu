package jp.gcreate.sample.daggersandbox.di.qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Copyright 2016 G-CREATE
 */

@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityContext {
}
