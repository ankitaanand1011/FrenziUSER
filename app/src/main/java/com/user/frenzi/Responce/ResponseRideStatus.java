package com.user.frenzi.Responce;

public class ResponseRideStatus {
    public Integer status;
    public String message;
    public String ride_status ;

    public ResponseRideStatus(Integer status, String message,String ride_status ) {
        this.status = status;
        this.ride_status  = ride_status ;
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

    public String getRide_status() {
        return ride_status;
    }

    public void setRide_status(String ride_status) {
        this.ride_status = ride_status;
    }
}
