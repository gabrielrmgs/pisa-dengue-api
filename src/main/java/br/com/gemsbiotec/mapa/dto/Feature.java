package br.com.gemsbiotec.mapa.dto;

import org.locationtech.jts.geom.Geometry;
import java.util.Map;

public class Feature {
    public final String type = "Feature";
    public Geometry geometry;
    public Map<String, Object> properties;
}
