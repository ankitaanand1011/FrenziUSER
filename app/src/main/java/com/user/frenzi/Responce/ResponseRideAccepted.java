package com.user.frenzi.Responce;

public class ResponseRideAccepted {
    public Integer status;
    public String message;
    public String accepted_driver_id ;

    public ResponseRideAccepted(Integer status, String message, String accepted_driver_id ) {
        this.status = status;
        this.message = message;
        this.accepted_driver_id  = accepted_driver_id ;
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

    public String getAccepted_driver_id() {
        return accepted_driver_id;
    }

    public void setAccepted_driver_id(String accepted_driver_id) {
        this.accepted_driver_id = accepted_driver_id;
    }
}
