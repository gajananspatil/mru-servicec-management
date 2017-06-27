package com.data;

/**
 * Created by Gajanan on 24-06-2017.
 */

public class UserDetails {
    //{"userId":7,"username":"8975940800","firstName":"Trupti","lastName":"Patil","mobileNo":8975940800,"emailId":"tt.gg@gg.com","password":"asdfgh","addresses":null}
    private long userId;
    private String username;
    private String firstName;
    private String lastName;
    private Long mobileNo;
    private String emailId;
    private String address;


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(Long mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
