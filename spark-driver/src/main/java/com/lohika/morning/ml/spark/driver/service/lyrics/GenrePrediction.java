package com.lohika.morning.ml.spark.driver.service.lyrics;

public class GenrePrediction {

    private String genre;
    private Double popProbability;
    private Double countryProbability;
    private Double bluesProbability;
    private Double rockProbability;
    private Double jazzProbability;
    private Double reggaeProbability;
    private Double hipHopProbability;
    private Double kPopProbability;

    public GenrePrediction(String genre, Double popProbability, Double countryProbability,
                           Double bluesProbability, Double rockProbability, Double jazzProbability,
                           Double reggaeProbability, Double hipHopProbability, Double kPopProbability) {
        this.genre = genre;
        this.popProbability = popProbability;
        this.countryProbability = countryProbability;
        this.bluesProbability = bluesProbability;
        this.rockProbability = rockProbability;
        this.jazzProbability = jazzProbability;
        this.reggaeProbability = reggaeProbability;
        this.hipHopProbability = hipHopProbability;
        this.kPopProbability = kPopProbability;
    }

    public GenrePrediction(String genre) {
        this.genre = genre;
    }

    public String getGenre() {
        return genre;
    }

    public Double getPopProbability() {
        return popProbability;
    }
    public Double getCountryProbability() {
        return countryProbability;
    }
    public Double getBluesProbability() {
        return bluesProbability;
    }
    public Double getRockProbability() {
        return rockProbability;
    }
    public Double getJazzProbability() {
        return jazzProbability;
    }
    public Double getReggaeProbability() {
        return reggaeProbability;
    }
    public Double getHipHopProbability() {
        return hipHopProbability;
    }
    public Double getkPopProbability() {
        return kPopProbability;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }
}