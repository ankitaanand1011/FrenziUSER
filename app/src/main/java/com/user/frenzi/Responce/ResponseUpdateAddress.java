package com.user.frenzi.Responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseUpdateAddress {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("response")
    @Expose
    public ReturnData returnData;

    public ResponseUpdateAddress(Integer status, String message, ReturnData returnData) {
        this.status = status;
        this.message = message;
        this.returnData = returnData;
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

    public ReturnData getResponse() {
        return returnData;
    }

    public void setResponse(ReturnData response) {
        this.returnData = response;
    }

    public static class ReturnData {

        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("user_id")
        @Expose
        public Integer userId;
        @SerializedName("title")
        @Expose
        public String title;
        @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("add_lat")
        @Expose
        public String addLat;
        @SerializedName("add_long")
        @Expose
        public String addLong;
        @SerializedName("address_status")
        @Expose
        public String addressStatus;

        public ReturnData(Integer id, Integer userId, String title, String address,
                        String addLat, String addLong, String addressStatus) {
            this.id = id;
            this.userId = userId;
            this.title = title;
            this.address = address;
            this.addLat = addLat;
            this.addLong = addLong;
            this.addressStatus = addressStatus;

        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAddLat() {
            return addLat;
        }

        public void setAddLat(String addLat) {
            this.addLat = addLat;
        }

        public String getAddLong() {
            return addLong;
        }

        public void setAddLong(String addLong) {
            this.addLong = addLong;
        }

        public String getAddressStatus() {
            return addressStatus;
        }

        public void setAddressStatus(String addressStatus) {
            this.addressStatus = addressStatus;
        }

    }
}
