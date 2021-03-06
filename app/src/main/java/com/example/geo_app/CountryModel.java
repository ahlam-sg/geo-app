package com.example.geo_app;

import java.io.Serializable;

public class CountryModel implements Serializable {
    private String capital;
    private String continent;
    private String country;
    private String code;
    private String flagURL;
    private String mapURL;

    public CountryModel() {
    }

    public String getCapital() {
        return capital;
    }
    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getContinent() {
        return continent;
    }
    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getFlagURL() {
        return flagURL;
    }
    public void setFlagURL(String flagURL) {
        this.flagURL = flagURL;
    }

    public String getMapURL() {
        return mapURL;
    }
    public void setMapURL(String mapURL) {
        this.mapURL = mapURL;
    }
}
