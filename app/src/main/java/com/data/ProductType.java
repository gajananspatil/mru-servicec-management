package com.data;

import java.util.HashMap;

/**
 * Created by Gajanan Patil on 3/9/2017.
 */

public class ProductType {

    private Integer productId;

    private String productName;

    HashMap<String,ProductSubType> productSubTypes ;

    public ProductType(Integer productId, String productName) {
        this.productId = productId;
        this.productName = productName;
    }

    public ProductType() {
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public HashMap<String,ProductSubType> getProductSubTypes() {
        return productSubTypes;
    }

    public void setProductSubTypes(HashMap<String,ProductSubType> productSubTypes) {
        this.productSubTypes = productSubTypes;
    }
}
