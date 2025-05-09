package com.lohika.morning.ml.spark.driver.service.lyrics.pipeline;

import static com.lohika.morning.ml.spark.distributed.library.function.map.lyrics.Column.*;

import com.lohika.morning.ml.spark.distributed.library.function.map.lyrics.Column;
import com.lohika.morning.ml.spark.driver.service.MLService;
import com.lohika.morning.ml.spark.driver.service.lyrics.Genre;
import com.lohika.morning.ml.spark.driver.service.lyrics.GenrePrediction;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.linalg.DenseVector;
import org.apache.spark.ml.tuning.CrossValidatorModel;
import org.apache.spark.sql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.when;
import static org.apache.spark.sql.functions.lit;
import static org.apache.spark.sql.functions.monotonically_increasing_id;

public abstract class CommonLyricsPipeline implements LyricsPipeline {

    @Autowired
    protected SparkSession sparkSession;

    @Autowired
    private MLService mlService;

    @Value("${lyrics.training.set.directory.path}")
    private String lyricsTrainingSetDirectoryPath;

    @Value("${lyrics.model.directory.path}")
    private String lyricsModelDirectoryPath;

    @Override
    public GenrePrediction predict(final String unknownLyrics) {
        String lyrics[] = unknownLyrics.split("\\r?\\n");
        Dataset<String> lyricsDataset = sparkSession.createDataset(Arrays.asList(lyrics),
                Encoders.STRING());

        Dataset<Row> unknownLyricsDataset = lyricsDataset
                .withColumnRenamed("value", VALUE.getName())
                .withColumn(LABEL.getName(), functions.lit(Genre.UNKNOWN.getValue()))
                .withColumn(ID.getName(), functions.lit("unknown.txt"));

        CrossValidatorModel model = mlService.loadCrossValidationModel(getModelDirectory());
        getModelStatistics(model);

        PipelineModel bestModel = (PipelineModel) model.bestModel();

        Dataset<Row> predictionsDataset = bestModel.transform(unknownLyricsDataset);
        Row predictionRow = predictionsDataset.first();

        System.out.println("\n------------------------------------------------");
        final Double prediction = predictionRow.getAs("prediction");
        System.out.println("Prediction: " + Double.toString(prediction));

        if (Arrays.asList(predictionsDataset.columns()).contains("probability")) {
            final DenseVector probability = predictionRow.getAs("probability");
            System.out.println("Probability: " + probability);
            System.out.println("------------------------------------------------\n");

            return new GenrePrediction(
                    getGenre(prediction).getName(),
                    probability.apply(0),
                    probability.apply(1),
                    probability.apply(2),
                    probability.apply(3),
                    probability.apply(4),
                    probability.apply(5),
                    probability.apply(6),
                    probability.apply(7)
            );
        }

        System.out.println("------------------------------------------------\n");
        return new GenrePrediction(getGenre(prediction).getName());
    }

    Dataset<Row> readLyrics() {
        Dataset<Row> rawTrainingSet = sparkSession
                .read()
                .option("header", "true")
                .schema(getTrainingSetSchema())
                .csv(lyricsTrainingSetDirectoryPath + "/Merged_dataset.csv");

        rawTrainingSet = rawTrainingSet.withColumn("id", monotonically_increasing_id().cast("string"));


        rawTrainingSet.count();
        rawTrainingSet.cache();

        rawTrainingSet = rawTrainingSet.withColumn(
                LABEL.getName(),
                when(col("genre").equalTo("pop"), Genre.POP.getValue())
                        .when(col("genre").equalTo("country"), Genre.COUNTRY.getValue())
                        .when(col("genre").equalTo("blues"), Genre.BLUES.getValue())
                        .when(col("genre").equalTo("rock"), Genre.ROCK.getValue())
                        .when(col("genre").equalTo("jazz"), Genre.JAZZ.getValue())
                        .when(col("genre").equalTo("reggae"), Genre.REGGAE.getValue())
                        .when(col("genre").equalTo("hip hop"), Genre.HIP_HOP.getValue())
                        .when(col("genre").equalTo("kpop"), Genre.KPOP.getValue())
                        .otherwise(Genre.UNKNOWN.getValue())
        );

        return rawTrainingSet;
    }

    private StructType getTrainingSetSchema() {
        return new StructType(new StructField[] {
                new StructField("artist_name", DataTypes.StringType, true, Metadata.empty()),
                new StructField("track_name", DataTypes.StringType, true, Metadata.empty()),
                new StructField("release_date", DataTypes.StringType, true, Metadata.empty()),
                new StructField("genre", DataTypes.StringType, true, Metadata.empty()),
                new StructField("lyrics", DataTypes.StringType, true, Metadata.empty())
        });
    }

    // Dataset<Row> readLyrics() {
    //     Dataset input = readLyricsForGenre(lyricsTrainingSetDirectoryPath, Genre.METAL)
    //                                             .union(readLyricsForGenre(lyricsTrainingSetDirectoryPath, Genre.POP));
    //     // Reduce the input amount of partition minimal amount (spark.default.parallelism OR 2, whatever is less)
    //     input = input.coalesce(sparkSession.sparkContext().defaultMinPartitions()).cache();
    //     // Force caching.
    //     input.count();

    //     return input;
    // }

    // private Dataset<Row> readLyricsForGenre(String inputDirectory, Genre genre) {
    //     Dataset<Row> lyrics = readLyrics(inputDirectory, genre.name().toLowerCase() + "/*");
    //     Dataset<Row> labeledLyrics = lyrics.withColumn(LABEL.getName(), functions.lit(genre.getValue()));

    //     System.out.println(genre.name() + " music sentences = " + lyrics.count());

    //     return labeledLyrics;
    // }

    // private Dataset<Row> readLyrics(String inputDirectory, String path) {
    //     Dataset<String> rawLyrics = sparkSession.read().textFile(Paths.get(inputDirectory).resolve(path).toString());
    //     rawLyrics = rawLyrics.filter(rawLyrics.col(VALUE.getName()).notEqual(""));
    //     rawLyrics = rawLyrics.filter(rawLyrics.col(VALUE.getName()).contains(" "));

    //     // Add source filename column as a unique id.
    //     Dataset<Row> lyrics = rawLyrics.withColumn(ID.getName(), functions.input_file_name());

    //     return lyrics;
    // }

    private Genre getGenre(Double value) {
        for (Genre genre: Genre.values()){
            if (genre.getValue().equals(value)) {
                return genre;
            }
        }

        return Genre.UNKNOWN;
    }

    @Override
    public Map<String, Object> getModelStatistics(CrossValidatorModel model) {
        Map<String, Object> modelStatistics = new HashMap<>();

        Arrays.sort(model.avgMetrics());
        modelStatistics.put("Best model metrics", model.avgMetrics()[model.avgMetrics().length - 1]);

        return modelStatistics;
    }

    void printModelStatistics(Map<String, Object> modelStatistics) {
        System.out.println("\n------------------------------------------------");
        System.out.println("Model statistics:");
        System.out.println(modelStatistics);
        System.out.println("------------------------------------------------\n");
    }

    void saveModel(CrossValidatorModel model, String modelOutputDirectory) {
        this.mlService.saveModel(model, modelOutputDirectory);
    }

    void saveModel(PipelineModel model, String modelOutputDirectory) {
        this.mlService.saveModel(model, modelOutputDirectory);
    }

    public void setLyricsTrainingSetDirectoryPath(String lyricsTrainingSetDirectoryPath) {
        this.lyricsTrainingSetDirectoryPath = lyricsTrainingSetDirectoryPath;
    }

    public void setLyricsModelDirectoryPath(String lyricsModelDirectoryPath) {
        this.lyricsModelDirectoryPath = lyricsModelDirectoryPath;
    }

    protected abstract String getModelDirectory();

    String getLyricsModelDirectoryPath() {
        return lyricsModelDirectoryPath;
    }
}