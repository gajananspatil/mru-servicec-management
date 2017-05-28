package com.data;

import java.util.HashMap;

/**
 * Created by Gajanan Patil on 3/9/2017.
 */

public class Category {
    private Integer categoryId;

    private String categoryName;

    HashMap<String,SubCategory> subCategories;

    public Category() {
    }

    public Category(Integer categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        subCategories = new HashMap<>();
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public  HashMap<String,SubCategory>  getSubCategories() {
        return subCategories;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setSubCategories( HashMap<String,SubCategory>  subCategories) {
        this.subCategories = subCategories;
    }
}
