package com.nikh.cth.dao;

import org.intellij.lang.annotations.MagicConstant;

@MagicConstant(valuesFromClass = SortOrder.class)
public @interface SortOrder {

    String ASC = "asc";
    String DESC = "desc";
}
