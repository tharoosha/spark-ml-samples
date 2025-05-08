package com.lohika.morning.ml.spark.driver.service.lyrics.pipeline;

import com.lohika.morning.ml.spark.driver.service.BaseTest;
import com.lohika.morning.ml.spark.driver.service.MLService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

//import static junit.framework.TestCase.assertNotNull;

public class NaiveBayesPipelineTest extends BaseTest {

    @Autowired
    private NaiveBayesBagOfWordsPipeline naiveBayesPipeline;

    @Autowired
    private MLService mlService;

    @Test
    public void testNaiveBayesPipeline(){
        assertNotNull(naiveBayesPipeline);
        assertNotNull(mlService);
    }

}
