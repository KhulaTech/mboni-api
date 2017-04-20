package com.khulatech.mboni.api.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Class permettant de configurer l'activity {@link com.khulatech.mboni.api.ui.MBoniAPIActivity}
 *      - enterCodeExplanationText: text affiché en haut du champ de saisi de code mboni
 *      - toDisplayCode: code mboni à afficher au lancement de l'actitvity {@link com.khulatech.mboni.api.ui.MBoniAPIActivity}
 *      - apiKey: votre cle api
 */

public class MBoniApiConfiguration implements Parcelable {
    private String enterCodeExplanationText;
    private String toDisplayCode;
    private final String apiKey;

    public MBoniApiConfiguration(@NonNull String apiKey) {
        this.apiKey = apiKey;
    }

    public MBoniApiConfiguration(String enterCodeExplanationText, String toDisplayCode,@NonNull String apiKey) {
        this.enterCodeExplanationText = enterCodeExplanationText;
        this.toDisplayCode = toDisplayCode;
        this.apiKey = apiKey;
    }

    public String getToDisplayCode() {
        return toDisplayCode;
    }

    public String getEnterCodeExplanationText() {
        return enterCodeExplanationText;
    }

    public String getApiKey() {
        return apiKey;
    }

    public MBoniApiConfiguration setEnterCodeExplanationText(String enterCodeExplanationText) {
        this.enterCodeExplanationText = enterCodeExplanationText;
        return this;
    }

    public MBoniApiConfiguration setToDisplayCode(String toDisplayCode) {
        this.toDisplayCode = toDisplayCode;
        return this;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.enterCodeExplanationText);
        dest.writeString(this.toDisplayCode);
        dest.writeString(this.apiKey);
    }

    protected MBoniApiConfiguration(Parcel in) {
        this.enterCodeExplanationText = in.readString();
        this.toDisplayCode = in.readString();
        this.apiKey = in.readString();
    }

    public static final Creator<MBoniApiConfiguration> CREATOR = new Creator<MBoniApiConfiguration>() {
        @Override
        public MBoniApiConfiguration createFromParcel(Parcel source) {
            return new MBoniApiConfiguration(source);
        }

        @Override
        public MBoniApiConfiguration[] newArray(int size) {
            return new MBoniApiConfiguration[size];
        }
    };
}
