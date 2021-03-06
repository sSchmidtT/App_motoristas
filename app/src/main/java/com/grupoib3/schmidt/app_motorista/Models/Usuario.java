package com.grupoib3.schmidt.app_motorista.Models;

import java.util.Date;

public class Usuario {
    private int Id;
    private String Cgc;
    private String DtNasc;
    private int Authenticated;
    private Date Created;
    private Date Expiration;
    private String AccessToken;
    private String Message;
    private String User;
    private String FCMToken;
    private int Id_Filial;


    public Usuario(){

    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getCgc() {
        return Cgc;
    }

    public void setCgc(String cgc) {
        Cgc = cgc;
    }

    public String getDtNasc() {
        return DtNasc;
    }

    public void setDtNasc(String dtNasc) {
        DtNasc = dtNasc;
    }

    public int getAuthenticated() {
        return Authenticated;
    }

    public void setAuthenticated(int authenticated) {
        Authenticated = authenticated;
    }

    public Date getCreated() {
        return Created;
    }

    public void setCreated(Date created) {
        Created = created;
    }

    public Date getExpiration() {
        return Expiration;
    }

    public void setExpiration(Date expiration) {
        Expiration = expiration;
    }

    public String getAccessToken() {
        return AccessToken;
    }

    public void setAccessToken(String accessToken) {
        AccessToken = accessToken;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getFCMToken() {
        return FCMToken;
    }

    public void setFCMToken(String FCMToken) {
        this.FCMToken = FCMToken;
    }

    public int getId_Filial() {
        return Id_Filial;
    }

    public void setId_Filial(int id_Filial) {
        Id_Filial = id_Filial;
    }
}
