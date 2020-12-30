package com.siva.insuris.model;

import java.util.ArrayList;

public class RatingModel {
    boolean success;
    String status_code;
    String status_message;

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getStatus_code() {
        return status_code;
    }
    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public String getStatus_message() {
        return status_message;
    }
    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }
}
