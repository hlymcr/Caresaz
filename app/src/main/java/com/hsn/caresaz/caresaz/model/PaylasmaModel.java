package com.hsn.caresaz.caresaz.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HULYA on 8.04.2018.
 */

public class PaylasmaModel implements Serializable {

    private String paylasmaKonusu;
    private String il;
    private String ilce;
    private String tel;
    private String tarih;
    private String resimpath;
    private String kayipDetay;
    private String id;
    private String ad;
    private String soyad;

   /* public PaylasmaModel(String konu, String il, String ilce, String tel, String tarih, String path, String kayipDetay, String id, String ad, String soyad) {
        this.paylasmaKonusu=konu;
        this.il=il;
        this.ilce=ilce;
        this.tel=tel;
        this.tarih=tarih;
        this.resimpath=path;
        this.kayipDetay=kayipDetay;
        this.id=id;
        this.ad=ad;
        this.soyad=soyad;

    }
*/

    public String getPaylasmaKonusu() {
        return paylasmaKonusu;
    }

    public void setPaylasmaKonusu(String paylasmaKonusu) {
        this.paylasmaKonusu = paylasmaKonusu;
    }

    public String getIl() {
        return il;
    }

    public void setIl(String il) {
        this.il = il;
    }

    public String getIlce() {
        return ilce;
    }

    public void setIlce(String ilce) {
        this.ilce = ilce;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public String getResimpath() {
        return resimpath;
    }

    public void setResimpath(String resimpath) {
        this.resimpath = resimpath;
    }

    public String getKayipDetay() {
        return kayipDetay;
    }

    public void setKayipDetay(String kayipDetay) {
        this.kayipDetay = kayipDetay;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("paylasmaKonu", paylasmaKonusu);

        result.put("il", il);

        result.put("ilce",ilce);

        result.put("tel",tel);

        result.put("tarih",tarih);

        result.put("resimpath",resimpath);

        result.put("kayipDetay",kayipDetay);

        result.put("id",id);

        result.put("ad",ad);

        result.put("soyad",soyad);

        return result;

    }
}
