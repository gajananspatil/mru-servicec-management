package com.data;

/**
 * Created by Trupti on 24-06-2017.
 */

public class RequestDetail {
    private Integer requestId;

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    private String subCategory;
    private UserDetails user;
    private Complaint complaint;

    public RequestDetail() {
        user = new UserDetails();
        complaint = new Complaint();
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public UserDetails getUser() {
        return user;
    }

    public void setUser(UserDetails user) {
        this.user = user;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    public void setComplaint(Complaint complaint) {
        this.complaint = complaint;
    }
}
