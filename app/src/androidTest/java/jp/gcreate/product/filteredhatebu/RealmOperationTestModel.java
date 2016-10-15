package jp.gcreate.product.filteredhatebu;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Copyright 2016 G-CREATE
 * TODO: primaryKeyと値を持つデータが出来たらこれは消す
 */

public class RealmOperationTestModel extends RealmObject {
    @PrimaryKey
    public long id;
    public String value;

    public RealmOperationTestModel() {
        // A default public constructor with no argument must be declared
        // if a custom constructor is declared.
        // そもそもビルドできなくなる
    }

    public RealmOperationTestModel(long id, String value) {
        this.id = id;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
