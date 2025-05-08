package com.lohika.morning.ml.api.controller;

import com.lohika.morning.ml.api.service.MusicService;
import com.lohika.morning.ml.spark.driver.service.lyrics.GenrePrediction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/music")
public class MusicController {

    @Autowired
    private MusicService musicService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "index.html";
    }

    @RequestMapping(value = "/train", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> trainForMusicPrediction() {
        Map<String, Object> trainStatistics = musicService.classifyMusic();

        return new ResponseEntity<>(trainStatistics, HttpStatus.OK);
    }

    @RequestMapping(value = "/predict", method = RequestMethod.POST)
    ResponseEntity<GenrePrediction> predictGenre(@RequestBody String unknownMusic) {
        GenrePrediction genrePrediction = musicService.predictGenre(unknownMusic);

        return new ResponseEntity<>(genrePrediction, HttpStatus.OK);
    }



}
