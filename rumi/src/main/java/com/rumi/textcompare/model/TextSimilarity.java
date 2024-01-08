package com.rumi.textcompare.model;

public class TextSimilarity {
    private final String url;
    private final String text;
    private final double similarityScore;
    private final String similarityScoreStr;

    public TextSimilarity(String url, String text, double score, String similarityScoreStr) {
        this.url = url;
        this.text = text;
        this.similarityScore = score;
        this.similarityScoreStr = similarityScoreStr;
    }

    public String getUrl() {
        return url;
    }

    public String getText() {
        return text;
    }

    public double getSimilarityScore() {
        return similarityScore;
    }

    public String getSimilarityScoreStr() {
        return similarityScoreStr;
    }
}
