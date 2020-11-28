package com.kudu.posto.beans;

public class Posto {

    private int id;
    private String nome;
    private String cnpj;
    private String ltd;
    private String lgt;
    private Preco preco;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Preco getPreco() {
        return preco;
    }

    public void setPreco(Preco preco) {
        this.preco = preco;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getLtd() {
        return ltd;
    }

    public void setLtd(String ltd) {
        this.ltd = ltd;
    }

    public String getLgt() {
        return lgt;
    }

    public void setLgt(String lgt) {
        this.lgt = lgt;
    }

    @Override
    public String toString() {
        return "Posto{" +
                "id=" + id +
                ", cnpj='" + cnpj + '\'' +
                ", ltd='" + ltd + '\'' +
                ", lgt='" + lgt + '\'' +
                ", preco=" + preco +
                '}';
    }
}
