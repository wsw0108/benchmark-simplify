package org.maptalks.benchmark.jts;

import java.util.List;

public class FeatureCollection extends GeoJSONCommon {

    private String type;
    private List<Feature> features;

    public FeatureCollection() {
        this.type = "FeatureCollection";
    }

    public String getType() {
        return type;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

}
