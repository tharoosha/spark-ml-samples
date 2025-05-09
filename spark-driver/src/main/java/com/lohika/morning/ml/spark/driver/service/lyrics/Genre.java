package com.lohika.morning.ml.spark.driver.service.lyrics;

public enum Genre {

    POP("pop", 0D),

    COUNTRY("country", 1D),

    BLUES("blues", 2D),

    ROCK("rock", 3D),

    JAZZ("jazz", 4D),

    REGGAE("reggae", 5D),

    HIP_HOP("hip hop", 6D),

    KPOP("kpop", 7D),

    UNKNOWN("Don\'t know :(", -1D);

    private final String name;
    private final Double value;

    Genre(final String name, final Double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Double getValue() {
        return value;
    }

}