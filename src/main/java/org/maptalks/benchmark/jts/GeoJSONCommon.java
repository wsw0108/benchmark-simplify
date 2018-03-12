package org.maptalks.benchmark.jts;

public abstract class GeoJSONCommon {

    private CRS crs;
    private double[] bbox;

    public double[] getBbox() {
        return bbox;
    }

    public void setBbox(double[] bbox) {
        this.bbox = bbox;
    }

    public CRS getCrs() {
        return crs;
    }

    public void setCrs(CRS crs) {
        this.crs = crs;
    }

}
