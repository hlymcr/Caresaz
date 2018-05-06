package com.hsn.caresaz.caresaz.model;

import android.widget.BaseAdapter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HULYA on 1.03.2018.
 */

public class KisiModel implements Serializable {

    private String adi;
    private String soyadi;
    private String tel;
    private String path;
    private String uid;
    private String yasadigi_yer;


    public KisiModel(String Adi, String Soyadi, String Tel, String Path, String Uid, String Yasadigi_yer){
        this.adi = Adi;
        this.soyadi = Soyadi;
        this.tel = Tel;
        this.path = Path;
        this.uid = Uid;
        this.yasadigi_yer = Yasadigi_yer;

    }
    public KisiModel(){}

    public String getAdi() {
        return adi;
    }

    public void setAdi(String adi) {
        this.adi = adi;
    }

    public String getSoyadi() {
        return soyadi;
    }

    public void setSoyadi(String soyadi) {
        this.soyadi = soyadi;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getYasadigi_yer() {
        return yasadigi_yer;
    }

    public void setYasadigi_yer(String yasadigi_yer) {
        this.yasadigi_yer = yasadigi_yer;
    }

    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("ad", adi);

        result.put("soyad", soyadi);

        result.put("tel", tel);

        result.put("path",path);

        result.put("uid",uid);

        result.put("Yasadigi yer",yasadigi_yer);

        return result;

    }




}

