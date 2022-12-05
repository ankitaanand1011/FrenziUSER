package com.user.frenzi.Responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseExtraCharges {

    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("response")
    @Expose
    public Response response;

    public ResponseExtraCharges(Integer status, String message, Response response) {
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

        @SerializedName("drop_off_charges")
        @Expose
        public String drop_off_charges;
        @SerializedName("toll_charges")
        @Expose
        public String toll_charges;
        @SerializedName("luggage_charges")
        @Expose
        public String luggage_charges;

        public Response(String drop_off_charges, String toll_charges,
                        String luggage_charges) {

            this.drop_off_charges = drop_off_charges;
            this.toll_charges = toll_charges;
            this.luggage_charges = luggage_charges;

        }

        public String getDrop_off_charges() {
            return drop_off_charges;
        }

        public void setDrop_off_charges(String drop_off_charges) {
            this.drop_off_charges = drop_off_charges;
        }

        public String getToll_charges() {
            return toll_charges;
        }

        public void setToll_charges(String toll_charges) {
            this.toll_charges = toll_charges;
        }

        public String getLuggage_charges() {
            return luggage_charges;
        }

        public void setLuggage_charges(String luggage_charges) {
            this.luggage_charges = luggage_charges;
        }
    }
}
