package com.hsn.caresaz.caresaz.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SehirIlceModel {

    @SerializedName("iller")
    @Expose
    private List<Iller> iller = null;

    public List<Iller> getIller() {
        return iller;
    }



}