package com.khulatech.mboni.api.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class CodeMBoniResult implements Parcelable {

    public static final String RESULT_KEY = "mboni_code_selection_result";

    private long id;
    private double longitude;
    private double latitude;
    private String nom;
    private String street;
    private String town;
    private String country;
    private String description;
    private String audio;
    private String photoDevanture;
    private String photoSecondaire;
    private boolean localisationVerifie;
    private String uniqueCode;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }


    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getPhotoDevanture() {
        return photoDevanture;
    }

    public void setPhotoDevanture(String photoDevanture) {
        this.photoDevanture = photoDevanture;
    }

    public String getPhotoSecondaire() {
        return photoSecondaire;
    }

    public void setPhotoSecondaire(String photoSecondaire) {
        this.photoSecondaire = photoSecondaire;
    }

    public boolean isLocalisationVerifie() {
        return localisationVerifie;
    }

    public void setLocalisationVerifie(boolean localisationVerifie) {
        this.localisationVerifie = localisationVerifie;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
        dest.writeString(this.nom);
        dest.writeString(this.description);
        dest.writeString(this.audio);
        dest.writeString(this.photoDevanture);
        dest.writeString(this.photoSecondaire);
        dest.writeByte(this.localisationVerifie ? (byte) 1 : (byte) 0);
        dest.writeString(this.uniqueCode);
    }

    public CodeMBoniResult() {
    }

    protected CodeMBoniResult(Parcel in) {
        this.id = in.readLong();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.nom = in.readString();
        this.description = in.readString();
        this.audio = in.readString();
        this.photoDevanture = in.readString();
        this.photoSecondaire = in.readString();
        this.localisationVerifie = in.readByte() != 0;
        this.uniqueCode = in.readString();
    }

    public static final Parcelable.Creator<CodeMBoniResult> CREATOR = new Parcelable.Creator<CodeMBoniResult>() {
        @Override
        public CodeMBoniResult createFromParcel(Parcel source) {
            return new CodeMBoniResult(source);
        }

        @Override
        public CodeMBoniResult[] newArray(int size) {
            return new CodeMBoniResult[size];
        }
    };
}
