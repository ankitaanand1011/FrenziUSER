package com.user.frenzi.Responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponce {

    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("response")
    @Expose
    public Response response;

    public LoginResponce(Integer status, String message, Response response) {
        this.status = status;
        this.message = message;
        this.response = response;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
    public class Response {

        @SerializedName("user_id")
        @Expose
        public Integer userId;
        @SerializedName("user_name")
        @Expose
        public String userName;
        @SerializedName("email")
        @Expose
        public String email;
        @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("paytm_number")
        @Expose
        public Object paytmNumber;
        @SerializedName("upi_number")
        @Expose
        public Object upiNumber;
        @SerializedName("paypal_email")
        @Expose
        public Object paypalEmail;

        @SerializedName("image")
        @Expose
        public String userImage;

        public Response(Integer userId, String userName, String email,
                        String address, Object paytmNumber, Object upiNumber,
                        Object paypalEmail, String userImage) {
            this.userId = userId;
            this.userName = userName;
            this.email = email;
            this.address = address;
            this.paytmNumber = paytmNumber;
            this.upiNumber = upiNumber;
            this.paypalEmail = paypalEmail;
            this.userImage = userImage;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Object getPaytmNumber() {
            return paytmNumber;
        }

        public void setPaytmNumber(Object paytmNumber) {
            this.paytmNumber = paytmNumber;
        }

        public Object getUpiNumber() {
            return upiNumber;
        }

        public void setUpiNumber(Object upiNumber) {
            this.upiNumber = upiNumber;
        }

        public Object getPaypalEmail() {
            return paypalEmail;
        }

        public void setPaypalEmail(Object paypalEmail) {
            this.paypalEmail = paypalEmail;
        }

        public String getUserImage() {
            return userImage;
        }

        public void setUserImage(String userImage) {
            this.userImage = userImage;
        }
    }
}
