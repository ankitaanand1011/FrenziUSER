package com.user.frenzi.Responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseNewRideDetails {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("response")
    @Expose
    public Response response;

    public ResponseNewRideDetails(Integer status, String message, Response response) {
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

    public static class Response {

        @SerializedName("ride_id ")
        @Expose
        public String ride_id;

        @SerializedName("rideID ")
        @Expose
        public Integer rideID;

        @SerializedName("user_id")
        @Expose
        public Integer userId;

        @SerializedName("driver_id")
        @Expose
        public Integer driverId;

        @SerializedName("pickup_address")
        @Expose
        public String pickup_address;

        @SerializedName("drop_address")
        @Expose
        public String drop_address;

        @SerializedName("pickup_location")
        @Expose
        public String pickup_location;

        @SerializedName("drop_location")
        @Expose
        public String drop_location;

        @SerializedName("distance")
        @Expose
        public String distance;

        @SerializedName("total_time")
        @Expose
        public Object total_time;

        @SerializedName("status")
        @Expose
        public String status;

        @SerializedName("amount")
        @Expose
        public String amount;

        @SerializedName("start_date")
        @Expose
        public String start_date;

        @SerializedName("start_time")
        @Expose
        public String start_time;

        @SerializedName("end_date")
        @Expose
        public Object end_date;

        @SerializedName("end_time")
        @Expose
        public Object end_time;

        @SerializedName("ride_otp")
        @Expose
        public String ride_otp;

        @SerializedName("fare_cost")
        @Expose
        public String fare_cost;

        @SerializedName("discount")
        @Expose
        public String  discount;

        @SerializedName("user_details")
        @Expose
        public UserDetails userDetails;

        @SerializedName("driver_details")
        @Expose
        public DriverDetails driverDetails;

        public Response(String ride_id,Integer rideID, Integer userId, Integer driverId,
                        String pickupAddress, String dropAddress, String pickupLocation,
                        String dropLocation, String distance, Object totalTime,
                        String status, String amount, String startDate, String startTime,
                        Object endDate, Object endTime, String rideOtp, String fare_cost,
                        String discount,
                        UserDetails userDetails, DriverDetails driverDetails) {

            this.ride_id = ride_id;
            this.rideID = rideID;
            this.userId = userId;
            this.driverId = driverId;
            this.pickup_address = pickupAddress;
            this.drop_address = dropAddress;
            this.pickup_location = pickupLocation;
            this.drop_location = dropLocation;
            this.distance = distance;
            this.total_time = totalTime;
            this.status = status;
            this.amount = amount;
            this.start_date = startDate;
            this.start_time = startTime;
            this.end_date = endDate;
            this.end_time = endTime;
            this.ride_otp = rideOtp;
            this.fare_cost = fare_cost;
            this.discount = discount;
            this.userDetails = userDetails;
            this.driverDetails = driverDetails;


        }

        public String getRide_id() {
            return ride_id;
        }

        public void setRide_id(String ride_id) {
            this.ride_id = ride_id;
        }

        public Integer getRideID() {
            return rideID;
        }

        public void setRideID(Integer rideID) {
            this.rideID = rideID;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getDriverId() {
            return driverId;
        }

        public void setDriverId(Integer driverId) {
            this.driverId = driverId;
        }

        public String getPickup_address() {
            return pickup_address;
        }

        public void setPickup_address(String pickup_address) {
            this.pickup_address = pickup_address;
        }

        public String getDrop_address() {
            return drop_address;
        }

        public void setDrop_address(String drop_address) {
            this.drop_address = drop_address;
        }

        public String getPickup_location() {
            return pickup_location;
        }

        public void setPickup_location(String pickup_location) {
            this.pickup_location = pickup_location;
        }

        public String getDrop_location() {
            return drop_location;
        }

        public void setDrop_location(String drop_location) {
            this.drop_location = drop_location;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public Object getTotal_time() {
            return total_time;
        }

        public void setTotal_time(Object total_time) {
            this.total_time = total_time;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getStart_date() {
            return start_date;
        }

        public void setStart_date(String start_date) {
            this.start_date = start_date;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public Object getEnd_date() {
            return end_date;
        }

        public void setEnd_date(Object end_date) {
            this.end_date = end_date;
        }

        public Object getEnd_time() {
            return end_time;
        }

        public void setEnd_time(Object end_time) {
            this.end_time = end_time;
        }

        public String getRide_otp() {
            return ride_otp;
        }

        public void setRide_otp(String ride_otp) {
            this.ride_otp = ride_otp;
        }

        public String getFare_cost() {
            return fare_cost;
        }

        public void setFare_cost(String fare_cost) {
            this.fare_cost = fare_cost;
        }

        public String getDiscount() {
            return discount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }

        public UserDetails getUserDetails() {
            return userDetails;
        }

        public void setUserDetails(UserDetails userDetails) {
            this.userDetails = userDetails;
        }

        public DriverDetails getDriverDetails() {
            return driverDetails;
        }

        public void setDriverDetails(DriverDetails driverDetails) {
            this.driverDetails = driverDetails;
        }


        public class UserDetails{

            @SerializedName("name")
            @Expose
            public String name;
            @SerializedName("user_id")
            @Expose
            public Integer userId;
            @SerializedName("email")
            @Expose
            public String email;
            @SerializedName("phone")
            @Expose
            public Object phone;
            @SerializedName("reviews")
            @Expose
            public String reviews;
            @SerializedName("image")
            @Expose
            public String image;

            public UserDetails(String name, Integer userId, String email, Object phone, String reviews, String image) {
                this.name = name;
                this.userId = userId;
                this.email = email;
                this.phone = phone;
                this.reviews = reviews;
                this.image = image;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Integer getUserId() {
                return userId;
            }

            public void setUserId(Integer userId) {
                this.userId = userId;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public Object getPhone() {
                return phone;
            }

            public void setPhone(Object phone) {
                this.phone = phone;
            }

            public String getReviews() {
                return reviews;
            }

            public void setReviews(String reviews) {
                this.reviews = reviews;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }
        }

        public class DriverDetails{
            @SerializedName("name")
            @Expose
            public String name;
            @SerializedName("driver_id")
            @Expose
            public Integer driverId;
            @SerializedName("email")
            @Expose
            public String email;
            @SerializedName("phone")
            @Expose
            public String phone;
            @SerializedName("reviews")
            @Expose
            public String reviews;
            @SerializedName("vehicle_no")
            @Expose
            public String vehicleNo;
            @SerializedName("vehicle_type")
            @Expose
            public String vehicleType;
            @SerializedName("image")
            @Expose
            public String image;

            public DriverDetails(String name, Integer driverId, String email, String phone, String reviews, String vehicleNo, String vehicleType, String image) {
                this.name = name;
                this.driverId = driverId;
                this.email = email;
                this.phone = phone;
                this.reviews = reviews;
                this.vehicleNo = vehicleNo;
                this.vehicleType = vehicleType;
                this.image = image;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Integer getDriverId() {
                return driverId;
            }

            public void setDriverId(Integer driverId) {
                this.driverId = driverId;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getReviews() {
                return reviews;
            }

            public void setReviews(String reviews) {
                this.reviews = reviews;
            }

            public String getVehicleNo() {
                return vehicleNo;
            }

            public void setVehicleNo(String vehicleNo) {
                this.vehicleNo = vehicleNo;
            }

            public String getVehicleType() {
                return vehicleType;
            }

            public void setVehicleType(String vehicleType) {
                this.vehicleType = vehicleType;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }
        }


    }
}
