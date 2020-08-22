package com.example.alifdeltaforce.monitoring;


// โตรงสร้างของข้อมูลหรือ Data Modal สำหรับแปลงเป็น JSON
public class PatientInformation {

     public String Pid;
     public String Pname;
     public String Page;
     public String Pbith;
     public String UserID;
    public double lat;
    public double lon;
    public String time;
    public double Geo_lat;
    public double Geo_lon;




    public PatientInformation(){

    }

    public PatientInformation(String pid, String pname, String page, String pbith, String userID, double lat, double lon, String time, double geo_lat, double geo_lon) {
        this.Pid = pid;
        this.Pname = pname;
        this.Page = page;
        this.Pbith = pbith;
        this.UserID = userID;
        this.lat = lat;
        this.lon = lon;
        this.time = time;
        this.Geo_lat = geo_lat;
        this.Geo_lon = geo_lon;
    }
}
