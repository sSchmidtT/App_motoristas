package com.grupoib3.schmidt.app_motorista.Models;

public class Notificacao {
    private int id;
    private int id_user;
    private int id_filial;
    private String data_notificacao;
    private String titulo_notificacao;
    private String msg_notificacao;
    private int status_notificacao;
    private String url;

    public Notificacao(int id, int id_user, int id_filial, String data_notificacao, String titulo_notificacao, String msg_notificacao, int status_notificacao, String url) {
        this.id = id;
        this.id_user = id_user;
        this.id_filial = id_filial;
        this.data_notificacao = data_notificacao;
        this.titulo_notificacao = titulo_notificacao;
        this.msg_notificacao = msg_notificacao;
        this.status_notificacao = status_notificacao;
        this.url = url;
    }

    public  Notificacao(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public int getId_filial() {
        return id_filial;
    }

    public void setId_filial(int id_filial) {
        this.id_filial = id_filial;
    }

    public String getData_notificacao() {
        return data_notificacao;
    }

    public void setData_notificacao(String data_notificacao) {
        this.data_notificacao = data_notificacao;
    }

    public String getTitulo_notificacao() {
        return titulo_notificacao;
    }

    public void setTitulo_notificacao(String titulo_notificacao) {
        this.titulo_notificacao = titulo_notificacao;
    }

    public String getMsg_notificacao() {
        return msg_notificacao;
    }

    public void setMsg_notificacao(String msg_notificacao) {
        this.msg_notificacao = msg_notificacao;
    }

    public int getStatus_notificacao() {
        return status_notificacao;
    }

    public void setStatus_notificacao(int status_notificacao) {
        this.status_notificacao = status_notificacao;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
