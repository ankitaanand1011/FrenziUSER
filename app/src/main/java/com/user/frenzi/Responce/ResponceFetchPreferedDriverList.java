package com.user.frenzi.Responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponceFetchPreferedDriverList {
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("response")
    @Expose
    public List<Response> response = null;

    public ResponceFetchPreferedDriverList(Integer status, String message, List<Response> response) {
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

    public List<Response> getResponse() {
        return response;
    }

    public void setResponse(List<Response> response) {
        this.response = response;
    }

    public class Response {

        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("email")
        @Expose
        public String email;
        @SerializedName("password")
        @Expose
        public String password;
        @SerializedName("phone")
        @Expose
        public String phone;
        @SerializedName("country_id")
        @Expose
        public String countryId;
        @SerializedName("state_id")
        @Expose
        public String stateId;
        @SerializedName("city_id")
        @Expose
        public String cityId;
        @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("latitude")
        @Expose
        public String latitude;
        @SerializedName("longitude")
        @Expose
        public String longitude;
        @SerializedName("image_icon")
        @Expose
        public String imageIcon;
        @SerializedName("status")
        @Expose
        public Integer status;
        @SerializedName("reg_date")
        @Expose
        public String regDate;
        @SerializedName("vehicle_type")
        @Expose
        public String vehicleType;
        @SerializedName("brand")
        @Expose
        public String brand;
        @SerializedName("model")
        @Expose
        public String model;
        @SerializedName("year")
        @Expose
        public String year;
        @SerializedName("color")
        @Expose
        public String color;
        @SerializedName("vehicle_no")
        @Expose
        public String vehicleNo;
        @SerializedName("license")
        @Expose
        public String license;
        @SerializedName("insurance")
        @Expose
        public String insurance;
        @SerializedName("permit")
        @Expose
        public String permit;
        @SerializedName("registration")
        @Expose
        public String registration;
        @SerializedName("vehicle_info")
        @Expose
        public String vehicleInfo;
        @SerializedName("reviews")
        @Expose
        public String reviews;
        @SerializedName("paypal_id")
        @Expose
        public String paypalId;
        @SerializedName("is_online")
        @Expose
        public Integer isOnline;
        @SerializedName("facebook_id")
        @Expose
        public String facebookId;
        @SerializedName("gmail_id")
        @Expose
        public String gmailId;
        @SerializedName("register_type")
        @Expose
        public Integer registerType;
        @SerializedName("is_verify")
        @Expose
        public Integer isVerify;

        public Response(Integer id, String name, String email, String password, String phone, String countryId, String stateId, String cityId, String address, String latitude, String longitude, String imageIcon, Integer status, String regDate, String vehicleType, String brand, String model, String year, String color, String vehicleNo, String license, String insurance, String permit, String registration, String vehicleInfo, String reviews, String paypalId, Integer isOnline, String facebookId, String gmailId, Integer registerType, Integer isVerify) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.password = password;
            this.phone = phone;
            this.countryId = countryId;
            this.stateId = stateId;
            this.cityId = cityId;
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
            this.imageIcon = imageIcon;
            this.status = status;
            this.regDate = regDate;
            this.vehicleType = vehicleType;
            this.brand = brand;
            this.model = model;
            this.year = year;
            this.color = color;
            this.vehicleNo = vehicleNo;
            this.license = license;
            this.insurance = insurance;
            this.permit = permit;
            this.registration = registration;
            this.vehicleInfo = vehicleInfo;
            this.reviews = reviews;
            this.paypalId = paypalId;
            this.isOnline = isOnline;
            this.facebookId = facebookId;
            this.gmailId = gmailId;
            this.registerType = registerType;
            this.isVerify = isVerify;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getCountryId() {
            return countryId;
        }

        public void setCountryId(String countryId) {
            this.countryId = countryId;
        }

        public String getStateId() {
            return stateId;
        }

        public void setStateId(String stateId) {
            this.stateId = stateId;
        }

        public String getCityId() {
            return cityId;
        }

        public void setCityId(String cityId) {
            this.cityId = cityId;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getImageIcon() {
            return imageIcon;
        }

        public void setImageIcon(String imageIcon) {
            this.imageIcon = imageIcon;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getRegDate() {
            return regDate;
        }

        public void setRegDate(String regDate) {
            this.regDate = regDate;
        }

        public String getVehicleType() {
            return vehicleType;
        }

        public void setVehicleType(String vehicleType) {
            this.vehicleType = vehicleType;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getVehicleNo() {
            return vehicleNo;
        }

        public void setVehicleNo(String vehicleNo) {
            this.vehicleNo = vehicleNo;
        }

        public String getLicense() {
            return license;
        }

        public void setLicense(String license) {
            this.license = license;
        }

        public String getInsurance() {
            return insurance;
        }

        public void setInsurance(String insurance) {
            this.insurance = insurance;
        }

        public String getPermit() {
            return permit;
        }

        public void setPermit(String permit) {
            this.permit = permit;
        }

        public String getRegistration() {
            return registration;
        }

        public void setRegistration(String registration) {
            this.registration = registration;
        }

        public String getVehicleInfo() {
            return vehicleInfo;
        }

        public void setVehicleInfo(String vehicleInfo) {
            this.vehicleInfo = vehicleInfo;
        }

        public String getReviews() {
            return reviews;
        }

        public void setReviews(String reviews) {
            this.reviews = reviews;
        }

        public String getPaypalId() {
            return paypalId;
        }

        public void setPaypalId(String paypalId) {
            this.paypalId = paypalId;
        }

        public Integer getIsOnline() {
            return isOnline;
        }

        public void setIsOnline(Integer isOnline) {
            this.isOnline = isOnline;
        }

        public String getFacebookId() {
            return facebookId;
        }

        public void setFacebookId(String facebookId) {
            this.facebookId = facebookId;
        }

        public String getGmailId() {
            return gmailId;
        }

        public void setGmailId(String gmailId) {
            this.gmailId = gmailId;
        }

        public Integer getRegisterType() {
            return registerType;
        }

        public void setRegisterType(Integer registerType) {
            this.registerType = registerType;
        }

        public Integer getIsVerify() {
            return isVerify;
        }

        public void setIsVerify(Integer isVerify) {
            this.isVerify = isVerify;
        }
    }
}
