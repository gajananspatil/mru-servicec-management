package com.data;

import java.util.HashMap;

/**
 * Created by Gajanan Patil on 3/9/2017.
 */

public class SubCategory {

    private Integer subCategoryId;

    private String SubCategoryName;

    private HashMap<String,ProductType> productTypes;

    public SubCategory() {
    }

    public SubCategory(Integer subCategoryId, String subCategoryName) {
        this.subCategoryId = subCategoryId;
        SubCategoryName = subCategoryName;
    }

    public Integer getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(Integer subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getSubCategoryName() {
        return SubCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        SubCategoryName = subCategoryName;
    }

    public  HashMap<String,ProductType> getProductTypes() {
        return productTypes;
    }

    public void setProductTypes( HashMap<String,ProductType> productTypes) {
        this.productTypes = productTypes;
    }
}
