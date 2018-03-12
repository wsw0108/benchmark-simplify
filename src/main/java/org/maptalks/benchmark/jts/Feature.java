package org.maptalks.benchmark.jts;

import com.vividsolutions.jts.geom.Geometry;
import java.util.HashMap;
import java.util.Map;

public class Feature extends GeoJSONCommon {

    private String type;
    private Object id;
    private com.vividsolutions.jts.geom.Geometry geometry;
    private Map<String, Object> properties;

    public Feature() {
        this.type = "Feature";
        this.properties = new HashMap<>();
    }

    public String getType() {
        return type;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

}
