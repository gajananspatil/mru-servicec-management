package com.data;

import java.util.Date;

/**
 * Created by Gajanan on 24-06-2017.
 */

public class Complaint {

    private Integer complaintId;
    private String compliantStatus;
    private Date createDate;
    private Date lastUpdateDate;
    private String comments;

    public Integer getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Integer complaintId) {
        this.complaintId = complaintId;
    }

    public String getCompliantStatus() {
        return compliantStatus;
    }

    public void setCompliantStatus(String compliantStatus) {
        this.compliantStatus = compliantStatus;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
