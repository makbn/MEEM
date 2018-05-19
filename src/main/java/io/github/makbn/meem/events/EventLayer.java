package io.github.makbn.meem.events;

import io.github.makbn.meemlocationgraph.LocationGraph;
import io.github.makbn.meemlocationgraph.LocationVertex;
import io.github.makbn.meemlocationgraph.PathEdge;
import io.github.makbn.meemlocationgraph.Utils;
import io.github.makbn.meemmapviewer.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventLayer {

    private final LayerGroup staticEvents;
    private LayerGroup traceLayer,trafficLayer;
    private JMapViewerTree tree;
    private HashMap<String,Layer> layerMap;
    private LocationGraph<LocationVertex, PathEdge<LocationVertex>> locationGraph;

    public EventLayer(LocationGraph<LocationVertex, PathEdge<LocationVertex>> locationGraph,JMapViewerTree tree) {
        this.locationGraph  = locationGraph;
        this.tree           = tree;
        this.layerMap       = new HashMap<>();
        this.traceLayer     = new LayerGroup("Events");
        this.staticEvents   = new LayerGroup(traceLayer,"Static View");
        this.trafficLayer   = new LayerGroup("Traffic");

        init();
    }

    private void init() {
        addTraceLayer(0, 500);
        addTraceLayer(500, 1000);
        addTraceLayer(1000, 2000);
        addTraceLayer(2000, 3000);
        addTraceLayer(3000, 5000);
        addTraceLayer(5000, Integer.MAX_VALUE);

        Layer cities = trafficLayer.addLayer("Cities");
        Layer states = trafficLayer.addLayer("States");

        this.tree.addLayer(cities);
        this.tree.addLayer(states);

        layerMap.put("cities",cities);
        layerMap.put("states",states);

    }

    private void addTraceLayer(int start, int end){
        String s = String.valueOf(start);
        String e = (end == Integer.MAX_VALUE) ? "" : String.valueOf(end);

        Layer layer0 = staticEvents.addLayer(s+"\t\t< W <\t\t"+e);
        layer0.setVisible(true);
        tree.addLayer(layer0);
        layerMap.put(s+"-"+end,layer0);
    }


    public ArrayList<MapPolyLine> getTraceGraph(){
        ArrayList<MapPolyLine> list=new ArrayList<>();
        for (int i = 0; i < locationGraph.getEdges().size(); i++) {
            int w = locationGraph.getEdges().get(i).getWeight();

            final PathEdge<LocationVertex> p = locationGraph.getEdges().get(i);
            List<Coordinate> coordinates = new ArrayList<>();
            LocationVertex dst = p.getVertexDst();
            LocationVertex src = p.getVertexSrc();

            dst.setInputTraffic(dst.getInputTraffic() + w);
            src.setOutputTraffic(src.getOutputTraffic() + w);

            coordinates.add(new Coordinate(p.getVertexSrc().getLat(), p.getVertexSrc().getLon()));
            coordinates.add(new Coordinate(p.getVertexDst().getLat(), p.getVertexDst().getLon()));
            coordinates.add(new Coordinate(p.getVertexDst().getLat(), p.getVertexDst().getLon()));

            MapPolyLine polyLine = new MapPolyLine(coordinates);
            polyLine.setStyle(EventStyle.getLineStyle(w));
            polyLine.setLayer(getLayer(w));
            list.add(polyLine);
        }
        return list;
    }

    public ArrayList<MapMarkerCircle> getCityTraffic(){
        ArrayList<MapMarkerCircle> markers=new ArrayList<>();
        for (LocationVertex vertex : locationGraph.getVertices()) {

            float rInput,rOutput,rTotal = 0.6f;
            int w = vertex.getInputTraffic() + vertex.getOutputTraffic();


            locationGraph.addToStateInputTraffic(vertex.geteState(), vertex.getInputTraffic());
            locationGraph.addToStateoutputTraffic(vertex.geteState(), vertex.getOutputTraffic());

            rInput = getRadius(vertex.getInputTraffic(), 100000, 10, 0.5, 0.05);
            rOutput = getRadius(vertex.getOutputTraffic(), 100000, 10, 0.5, 0.05);

            rTotal = getRadius(w, 100000, 10, 0.5, 0.05);

            MapMarkerCircle circleInput,circleOutput;

            if (rTotal >= 0.2f) {
                circleInput = new MapMarkerCircle(layerMap.get("cities"), vertex.getpCity()
                        , new Coordinate(vertex.getLat(), vertex.getLon()), rInput);

                circleOutput = new MapMarkerCircle(layerMap.get("cities"), vertex.getpCity()
                        , new Coordinate(vertex.getLat(), vertex.getLon()), rOutput);
            }else{
                circleInput = new MapMarkerCircle(layerMap.get("cities"), "", new Coordinate(vertex.getLat(), vertex.getLon()), rInput);
                circleOutput = new MapMarkerCircle(layerMap.get("cities"), "", new Coordinate(vertex.getLat(), vertex.getLon()), rOutput);
            }

            circleInput.setStyle(EventStyle.getDotStyle("cityInputTraffic"));
            circleOutput.setStyle(EventStyle.getDotStyle("cityOutputTraffic"));

            markers.add(circleInput);
            markers.add(circleOutput);

        }
        return markers;
    }

    public ArrayList<MapMarkerCircle> getStateTraffic(){
        ArrayList<MapMarkerCircle> markers=new ArrayList<>();
        Style input=EventStyle.getDotStyle("stateInputTraffic");
        Style output=EventStyle.getDotStyle("outputInputTraffic");

        for(Map.Entry<String,Integer> entry:locationGraph.getStateInputTraffic().entrySet()){
            markers.add(getCircle(entry.getValue(), entry.getKey(), input, layerMap.get("states")));
        }
        for(Map.Entry<String,Integer> entry:locationGraph.getStateOutputTraffic().entrySet()){
            markers.add(getCircle(entry.getValue(), entry.getKey(), output, layerMap.get("states")));
        }
        return markers;

    }

    private Layer getLayer(int w) {
        for(Map.Entry<String,Layer> entry : layerMap.entrySet()){
            try{
                String[] boundary = entry.getKey().split("-");
                if(Integer.valueOf(boundary[0]) <= w && Integer.valueOf(boundary[1]) >w )
                    return entry.getValue();
            }catch (NumberFormatException e){
                continue;
            }
        }
        System.out.println("LAYER NULL"+"W : "+w);
        return null;
    }

    private float getRadius(int w, int max, int min, double newMax, double newMin) {
        float newR = 0;

        newR = (float) (((((double) (w - min)) / ((double) (max - min))) * (newMax - newMin)) + newMin);

        return (float) newR;

    }

    private MapMarkerCircle getCircle(int traffic,String name,Style style,Layer layer){
        float rInput=getRadius(traffic,100000,10,0.5,0.05);
        double[] geo= Utils.getStateGeo(name);
        if(geo==null)
            System.out.println(name);
        MapMarkerCircle circle=new MapMarkerCircle(layer,name,new Coordinate(geo[0],geo[1]),rInput);
        circle.setStyle(style);
        return circle;
    }

}
