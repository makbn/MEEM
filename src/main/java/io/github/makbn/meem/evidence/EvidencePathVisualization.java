package io.github.makbn.meem.evidence;

import io.github.makbn.meemmapviewer.MapMarkerCircle;
import io.github.makbn.meemmapviewer.MapPolyLine;

import java.util.ArrayList;

public class EvidencePathVisualization {
    private ArrayList<MapPolyLine> roads;
    private ArrayList<MapMarkerCircle> cities;


    public EvidencePathVisualization() {
        this.roads=new ArrayList<>();
        this.cities=new ArrayList<>();
    }


    public ArrayList<MapPolyLine> getRoads() {
        return roads;
    }

    public void setRoads(ArrayList<MapPolyLine> roads) {
        this.roads = roads;
    }

    public ArrayList<MapMarkerCircle> getCities() {
        return cities;
    }

    public void setCities(ArrayList<MapMarkerCircle> cities) {
        this.cities = cities;
    }
}
