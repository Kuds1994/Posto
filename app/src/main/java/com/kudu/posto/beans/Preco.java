package com.kudu.posto.beans;

public class Preco {

    private int id;
    private double gasolina;
    private double aditivada;
    private double alcool;
    private double diesel;
    private double gnv;
    private double etanol;

    public double getEtanol() {
        return etanol;
    }

    public void setEtanol(double etanol) {
        this.etanol = etanol;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getGasolina() {
        return gasolina;
    }

    public void setGasolina(double gasolina) {
        this.gasolina = gasolina;
    }

    public double getAditivada() {
        return aditivada;
    }

    public void setAditivada(double aditivada) {
        this.aditivada = aditivada;
    }

    public double getAlcool() {
        return alcool;
    }

    public void setAlcool(double alcool) {
        this.alcool = alcool;
    }

    public double getDiesel() {
        return diesel;
    }

    public void setDiesel(double diesel) {
        this.diesel = diesel;
    }

    public double getGnv() {
        return gnv;
    }

    public void setGnv(double gnv) {
        this.gnv = gnv;
    }

    @Override
    public String toString() {
        return "Preco{" +
                "id=" + id +
                ", gasolina=" + gasolina +
                ", aditivada=" + aditivada +
                ", alcool=" + alcool +
                ", diesel=" + diesel +
                ", gnv=" + gnv +
                '}';
    }
}
