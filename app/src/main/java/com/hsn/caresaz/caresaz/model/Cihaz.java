package com.hsn.caresaz.caresaz.model;

/**
 * Created by HULYA on 20.04.2018.
 */

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HULYA on 1.04.2018.
 */

public class Cihaz implements Serializable {

    private String isim;
    private String tur;
    private String resim;
    private String kod;

    public Cihaz(String isim, String tur, String resim, String kod){
        this.isim=isim;
        this.tur=tur;
        this.resim=resim;
        this.kod=kod;
    }
    public Cihaz(){

    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public String getTur() {
        return tur;
    }

    public void setTur(String tur) {
        this.tur = tur;
    }

    public String getResim() {
        return resim;
    }

    public void setResim(String resim) {
        this.resim = resim;
    }

    public String getKod() {
        return kod;
    }

    public void setKod(String kod) {
        this.kod = kod;
    }

    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("isim", isim);

        result.put("tur", tur);

        result.put("resim",resim);

        result.put("kod",kod);

        return result;

    }
}
