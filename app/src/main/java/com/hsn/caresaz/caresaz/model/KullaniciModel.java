package com.hsn.caresaz.caresaz.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HULYA on 1.04.2018.
 */

public class KullaniciModel implements Serializable {

    private String ad;
    private String soyad;
    private String tel;
    private String sehir;

    public KullaniciModel(String ad, String soyad, String tel, String sehir){
        this.ad=ad;
        this.soyad=soyad;
        this.tel=tel;
        this.sehir=sehir;
    }
    public KullaniciModel(){

    }
    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getSoyad() {
        return soyad;
    }

    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }



    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getSehir() {
        return sehir;
    }

    public void setSehir(String sehir) {
        this.sehir = sehir;
    }
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("ad", ad);

        result.put("soyad", soyad);

        result.put("tel",tel);

        result.put("sehir",sehir);

        return result;

    }
}
