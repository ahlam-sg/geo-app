package com.example.geo_app;

import java.io.Serializable;

public class Country implements Serializable {
    private String capital;
    private String continent;
    private String country;
    private String code;
    private String flag_url;

    public Country() {
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

    public String getFlag_url() {
        return flag_url;
    }

    public void setFlag_url(String flag_url) {
        this.flag_url = flag_url;
    }
}
