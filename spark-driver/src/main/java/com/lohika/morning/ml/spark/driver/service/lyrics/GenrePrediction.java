package com.lohika.morning.ml.spark.driver.service.lyrics;

public class GenrePrediction {

    private String genre;
    private Double metalProbability;
    private Double popProbability;

    public GenrePrediction(String genre, Double metalProbability, Double popProbability) {
        this.genre = genre;
        this.metalProbability = metalProbability;
        this.popProbability = popProbability;
    }

    public GenrePrediction(String genre) {
        this.genre = genre;
    }

    public GenrePrediction(String name, double apply, double apply1, double apply2, double apply3, double apply4, double apply5, double apply6, double apply7) {
    }

    public String getGenre() {
        return genre;
    }

    public Double getMetalProbability() {
        return metalProbability;
    }

    public Double getPopProbability() {
        return popProbability;
    }
}
