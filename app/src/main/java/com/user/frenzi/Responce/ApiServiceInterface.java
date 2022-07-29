package com.user.frenzi.Responce;



import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiServiceInterface {

    String BASE_URL = "https://mobileappsgamesstudio.com/works/frenzi_new/api/";


    @Multipart
    @POST("login")
    Call<LoginResponce> Login(
            @Part("email") RequestBody referal,
            @Part("password") RequestBody pass,
            @Part("login_type") RequestBody type
    );
    @Multipart
    @POST("otp_verify")
    Call<ServerGeneralResponse> OtpVerify(
            @Part("email") RequestBody referal,
            @Part("otp") RequestBody pass


    );
    @Multipart
    @POST("address_list")
    Call<ResponceFetchRecentAddressList> fetchRecentaAddresses(
            @Part("user_id") RequestBody referal


    );
    @Multipart
    @POST("vehicle_type")
    Call<ResponceFetchCarList> fetchCarlist(
            @Part("pickupLat") RequestBody log1,
            @Part("pickupLon") RequestBody lat1,
            @Part("dropLat") RequestBody log2,
            @Part("dropLon") RequestBody lat2


    );
    @Multipart
    @POST("driver_list")
    Call<ResponceFetchPreferedDriverList> fetchDriverList(
            @Part("vehicle_type") RequestBody vehicle_type


    );

    @Multipart
    @POST("forget_password")
    Call<FPAssResponse> ForgetPassword(
            @Part("email") RequestBody email


    );

    @Multipart
    @POST("reset_password")
    Call<ServerGeneralResponse> ResetPassword(
            @Part("email") RequestBody email,
            @Part("otp_code") RequestBody otp_code,
            @Part("new_password") RequestBody new_password


    );
  @Multipart
    @POST("send_otp_mail")
    Call<ServerGeneralResponse> SendOtpToEmail(
            @Part("email") RequestBody email



    );


    @Multipart
    @POST("ride_details")
    Call<ResponceFetchRidedetails> fetchRideDetails(
            @Part("ride_id") RequestBody ride_id


    );
    @Multipart
    @POST("ride_history")
    Call<ResponceFetchRideHistory> FetchRideHistory(
            @Part("user_id") RequestBody ride_id,
            @Part("date") RequestBody rideDate


    );

    @Multipart
    @POST("about_us")
    Call<ResponseAboutUs> AboutUs(
            @Part("body") RequestBody body


    );

    @Multipart
    @POST("contact_submit")
    Call<ServerGeneralResponse> ContactUs(
            @Part("fullname") RequestBody fullname,
            @Part("email") RequestBody email,
            @Part("phone") RequestBody phone,
            @Part("message") RequestBody message


    );


    @Multipart
    @POST("post_ride")
    Call<ServerGeneralResponse> SubmitRide(

            @Part("user_id") RequestBody          user_id,
            @Part("driver_id") RequestBody        driver_id,
            @Part("pickup_address") RequestBody   pickup_address,
            @Part("drop_address") RequestBody     drop_address,
            @Part("pickup_lat") RequestBody       pickup_lat,
            @Part("pickup_long") RequestBody      pickup_long,
            @Part("drop_lat") RequestBody         drop_lat,
            @Part("drop_long") RequestBody        drop_long,
            @Part("distance") RequestBody         distance,
            @Part("total_time") RequestBody       total_time,
            @Part("amount") RequestBody           amount,
            @Part("start_date") RequestBody       start_date,
            @Part("start_time") RequestBody       start_time,
            @Part("end_date") RequestBody         end_date,
            @Part("end_time") RequestBody         end_time



    );

    @Multipart
    @POST("user_profile_update")
    Call<ProfileResponse> UpdateUserProfile(
            @Part("user_id") RequestBody user_id,
            @Part("name") RequestBody name,
            @Part("phone") RequestBody phone,
            @Part("email") RequestBody email,
            @Part("address") RequestBody address,
            @Part MultipartBody.Part image_icon



    );
    @Multipart
    @POST("user_profile_update")
    Call<ProfileResponse> UpdateUserProfileWithoutImage(
            @Part("user_id") RequestBody user_id,
            @Part("name") RequestBody name,
            @Part("phone") RequestBody phone,
            @Part("email") RequestBody email,
            @Part("address") RequestBody address

    );

    @Multipart
    @POST("user_reg")
    Call<ResponseRegistration> UserRegistration(
            @Part("name") RequestBody name,
            @Part("email") RequestBody email,
            @Part("phone") RequestBody phone,
            @Part("password") RequestBody password,
            @Part("address") RequestBody address,
            @Part("reg_type") RequestBody reg_tpe



    );

    @Multipart
    @POST("user_profile")
    Call<ProfileResponse> UserProfile(
            @Part("user_id") RequestBody user_id
    );














//
//    @Multipart
//    @POST("teacher/forgotpass")
//    Call<ResponceForgetPassword> forgetPassword(
//            @Header("token") String taskId,
//            @Part("username") RequestBody referal
//
//    );
//
//    @Multipart
//    @POST("teacher/teacherAttendance")
//    Call<ResponceTeacherAttandance> fetch_myAttandance(
//            @Header("token") String taskId,
//            @Part("teacherID") RequestBody t_id,
//            @Part("month") RequestBody m,
//            @Part("year") RequestBody year
//    );
//
//    @Multipart
//    @POST("teacher/teacherAssignments")
//    Call<ResponceTeacherAssignment> fetch_assignment(
//            @Header("token") String taskId,
//            @Part("teacherID") RequestBody teacher_id,
//            @Part("datefrom") RequestBody from_date,
//            @Part("dateto") RequestBody to_date,
//            @Part("class") RequestBody m,
//            @Part("subject") RequestBody year
//    );
//
//    @Multipart
//    @POST("teacher/noticeBoard")
//    Call<ResponceFetchNotices> fetch_notices(
//            @Header("token") String taskId,
//            @Part("fromdate") RequestBody f_date,
//            @Part("todate") RequestBody t_date,
//            @Part("teacherID") RequestBody t_id
//    );
//
//    @Multipart
//    @POST("teacher/teacherProfile")
//    Call<ResponceFetchUserDetail> fetch_user_details(
//            @Header("token") String taskId,
//            @Part("teacherID") RequestBody t_id
//    );
//    @Multipart
//    @POST("teacher/classes")
//    Call<ResponceDataStandard> fetchclass(
//            @Header("token") String taskId,
//            @Part("school_id") RequestBody s_id,
//            @Part("teacher_id") RequestBody User_id
//
//    );
//    @Multipart
//    @POST("teacher/section")
//    Call<ResponcefetchSection> fetchsection(
//            @Header("token") String taskId,
//            @Part("class_id") RequestBody t_id,
//            @Part("school_id") RequestBody s_id,
//            @Part("teacher_id") RequestBody User_id
//
//
//    );
//    @Multipart
//    @POST("teacher/courses")
//    Call<ResponceFetchCourseList> fetchcourse(
//            @Header("token") String taskId,
//            @Part("class_id") RequestBody t_id,
//            @Part("school_id") RequestBody s_id,
//            @Part("teacher_id") RequestBody User_id
//
//    );
//
//
//
//    @Multipart
//    @POST("teacher/teacherProfileUpdate")
//    Call<ResponceUpdateProfileDetail> UpdateProfileDetail(
//            @Header("token") String taskId,
//            @Part("teacherID") RequestBody key,
//            @Part("name") RequestBody id,
//            @Part("dob") RequestBody ph,
//            @Part("phone") RequestBody pass,
//            @Part("permanent_address") RequestBody vat,
//            @Part("email") RequestBody in_ct_no,
//            @Part("blood_group") RequestBody blood
//
//
//    );
//
//    @Multipart
//    @POST("teacher/studentList")
//    Call<ResponceFetchStudendList> fetch_studentList(
//            @Header("token") String taskId,
//            @Part("school") RequestBody school,
//            @Part("class") RequestBody Class,
//            @Part("section") RequestBody sec
//    );
//
//    @Multipart
//    @POST("teacher/studentdsFee")
//    Call<ResponceFetchStudentFees> fetch_student_fees_list(
//            @Header("token") String taskId,
//            @Part("class") RequestBody t_id,
//            @Part("section") RequestBody m,
//            @Part("school_id") RequestBody year
//    );
//
//    @Multipart
//    @POST("user/studentProfile")
//    Call<ResponceFetchStudentDeatilAndHealthRecord> fetch_student_details(
//            @Header("token") String taskId,
//            @Part("studentID") RequestBody t_id
//    );
//
//    @Multipart
//    @POST("user/studentAttendance")
//    Call<ResponceStudentAttandance> fetch_studentAttandance(
//            @Header("token") String taskId,
//            @Part("studentID") RequestBody t_id,
//            @Part("month") RequestBody month,
//            @Part("year") RequestBody year
//    );
//
//    @Multipart
//    @POST("student/examResult")
//    Call<ResponceStudentSubjectList> fetch_studentSubject(
//            @Header("token") String taskId,
//            @Part("user_id") RequestBody t_id,
//            @Part("academic_year") RequestBody year
//    );
//    @Multipart
//    @POST("teacher/studentListAll")
//    Call<ResponcefetchStudentAll> fetch_All_Student(
//            @Header("token") String taskId,
//            @Part("class_id") RequestBody class_id,
//            @Part("section_id") RequestBody sec_id
//    );
//
//    @Multipart
//    @POST("teacher/postAssignment")
//    Call<ResponceCommon> createAssignment(
//            @Header("token") String taskId,
//            @Part("school_id") RequestBody school_id,
//            @Part("class") RequestBody class_id,
//            @Part("section") RequestBody sec_id,
//            @Part("subject") RequestBody subject_id,
//            @Part("academic_year") RequestBody year,
//            @Part("title") RequestBody title,
//            @Part("deadline") RequestBody submission_date,
//            @Part("note") RequestBody note,
////            @Part("status") RequestBody status,
//            @Part("user_id") RequestBody teacher_id
//
//    );
//
//    @Multipart
//    @POST("teacher/getTeacherAssignments")
//    Call<ResponceFetchAllAssignment> fetch_aAllassignment(
//            @Header("token") String taskId,
//            @Part("teacherID") RequestBody t_id
//    );
//
//    @Multipart
//    @POST("teacher/postHomeWork")
//    Call<ResponceCommon> createHomeWork(
//            @Header("token") String taskId,
//            @Part("school_id") RequestBody school_id,
//            @Part("class_id") RequestBody class_id,
//            @Part("section_id") RequestBody sec_id,
//            @Part("subject_id") RequestBody subject_id,
//            @Part("title") RequestBody title,
//            @Part("deadline") RequestBody submission_date,
//            @Part("note") RequestBody note,
//            @Part("status") RequestBody status,
//            @Part("teacherID") RequestBody teacher_id
//
//    );
//
//    @Multipart
//    @POST("teacher/getHomework")
//    Call<ResponceFetchHomework> fetch_homeworkList(
//            @Header("token") String taskId,
//            @Part("class") RequestBody t_id,
//            @Part("section") RequestBody m,
//            @Part("fromdate") RequestBody to_date,
//            @Part("todate") RequestBody from_date,
//            @Part("teacherID") RequestBody id
//    );
//    @Multipart
//    @POST("teacher/postHomeWork")
//    Call<ResponceCommon> createNotice(
//            @Header("token") String taskId,
////            @Part("school_id") RequestBody school_id,
//            @Part("class") RequestBody class_id,
//            @Part("section") RequestBody sec_id,
////            @Part("subject_id") RequestBody subject_id,
//            @Part("title") RequestBody title,
//            @Part("date") RequestBody submission_date,
//            @Part("notice") RequestBody note,
//            @Part("teacherID") RequestBody teacher_id
//
//    );
}
