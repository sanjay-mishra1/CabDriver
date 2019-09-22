package com.example.sanjay.cabdriver.NewTrip;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.cabdriver.R;
import com.google.android.gms.maps.model.LatLng;

public class Trip {
    String tripid;
    String username;
    String userphone;
    String userLocation;
    String desinationLocation;
    String userRating;
    String tripOTP;
    String userImg;
    int tripAmount;
    public String getTripid() {
        return tripid;
    }


    public void setTripid(String tripid) {
        this.tripid = tripid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserphone() {
        return userphone;
    }

    public void setUserphone(String userphone) {
        this.userphone = userphone;
    }

    public LatLng getUserLocation() {
        String [] loc=userLocation.split(",");
        return  new LatLng(Double.parseDouble(loc[0]),Double.parseDouble(loc[1]));
     }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public LatLng getDesinationLocation() {
        String [] loc=desinationLocation.split(",");
        return  new LatLng(Double.parseDouble(loc[0]),Double.parseDouble(loc[1]));
    }

    public void setDesinationLocation(String desinationLocation) {
        this.desinationLocation = desinationLocation;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getTripOTP() {
        return tripOTP;
    }

    public void setTripOTP(String tripOTP) {
        this.tripOTP = tripOTP;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public int getTripAmount() {
        return tripAmount;
    }

    public void setTripAmount(int tripAmount) {
        this.tripAmount = tripAmount;
    }

    void setInfoFields(Context context) {
      TextView pickupText=((Activity)context).findViewById(R.id.pickup);
      TextView destText=((Activity)context).findViewById(R.id.destination);
      TextView userNameText=((Activity)context).findViewById(R.id.userName);
      TextView userPhoneText=((Activity)context).findViewById(R.id.userPhone);
      TextView userRatingText=((Activity)context).findViewById(R.id.userRating);
      TextView tripAmountText=((Activity)context).findViewById(R.id.tripAmount);
      ImageView imageView=((Activity)context).findViewById(R.id.userImg);
      pickupText.setText(userLocation);
      destText.setText(desinationLocation);
      userNameText.setText(username);
      userPhoneText.setText(userphone);
      userRatingText.setText(userRating);
      tripAmountText.setText(tripAmount);
      Glide.with(context).setDefaultRequestOptions(RequestOptions.circleCropTransform()).load(userImg).into(imageView);
    }
}
