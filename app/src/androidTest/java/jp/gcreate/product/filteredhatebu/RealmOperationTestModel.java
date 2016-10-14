package jp.gcreate.product.filteredhatebu;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Copyright 2016 G-CREATE
 */

public class RealmOperationTestModel extends RealmObject {
    @PrimaryKey
    long id;
    String value;

    public RealmOperationTestModel() {
        // A default public constructor with no argument must be declared
        // if a custom constructor is declared.
        // そもそもビルドできなくなる
    }

    public RealmOperationTestModel(long id, String value) {
        this.id = id;
        this.value = value;
    }
}
