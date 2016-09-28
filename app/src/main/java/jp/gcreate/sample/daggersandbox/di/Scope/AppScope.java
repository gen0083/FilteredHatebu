package jp.gcreate.sample.daggersandbox.di.Scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Copyright 2016 G-CREATE
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface AppScope {
}
