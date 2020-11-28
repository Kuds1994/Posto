package com.kudu.posto.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Login {

    @JsonProperty("email")
    private String email;
    @JsonProperty("password")
    private String senha;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
