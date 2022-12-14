package com.user.frenzi.Responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileResponse {

    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("response")
    @Expose
    public Response response;

    public ProfileResponse(Integer status, String message, Response response) {
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
        public Integer user_id;

        @SerializedName("user_name")
        @Expose
        public String user_name;

        @SerializedName("phone")
        @Expose
        public String user_phone;


        @SerializedName("email")
        @Expose
        public String user_email;


        @SerializedName("address_line_1")
        @Expose
        public String address1;

        @SerializedName("address_line_2")
        @Expose
        public String address2;

        @SerializedName("city")
        @Expose
        public String city;

        @SerializedName("postcode")
        @Expose
        public String postcode;

        @SerializedName("image")
        @Expose
        public String image;


        public Response(Integer user_id, String user_name, String user_phone,String user_email
                ,String address1,String address2,String city,String postcode,
                        String image) {
            this.user_id = user_id;
            this.user_name = user_name;
            this.user_phone = user_phone;
            this.user_email = user_email;
            this.address1 = address1;
            this.address2 = address2;
            this.city = city;
            this.postcode = postcode;
            this.image = image;

        }

        public Integer getUser_id() {
            return user_id;
        }

        public void setUser_id(Integer user_id) {
            this.user_id = user_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getUser_phone() {
            return user_phone;
        }

        public void setUser_phone(String user_phone) {
            this.user_phone = user_phone;
        }

        public String getUser_email() {
            return user_email;
        }

        public void setUser_email(String user_email) {
            this.user_email = user_email;
        }

        public String getAddress1() {
            return address1;
        }

        public void setAddress1(String address1) {
            this.address1 = address1;
        }

        public String getAddress2() {
            return address2;
        }

        public void setAddress2(String address2) {
            this.address2 = address2;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getPostcode() {
            return postcode;
        }

        public void setPostcode(String postcode) {
            this.postcode = postcode;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }





}