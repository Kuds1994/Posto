package com.kudu.posto.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

    private int id;
    @JsonProperty("first_name")
    private String nomeDoPosto;
    @JsonProperty("password")
    private String senha;
    private String email;
    private Posto posto;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeDoPosto() {
        return nomeDoPosto;
    }

    public void setNomeDoPosto(String nomeDoPosto) {
        this.nomeDoPosto = nomeDoPosto;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Posto getPosto() {
        return posto;
    }

    public void setPosto(Posto posto) {
        this.posto = posto;
    }


}
