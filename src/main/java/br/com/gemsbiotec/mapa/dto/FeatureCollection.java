package br.com.gemsbiotec.mapa.dto;

import java.util.ArrayList;
import java.util.List;

public class FeatureCollection {
    public final String type = "FeatureCollection";
    public List<Feature> features = new ArrayList<>();
}
