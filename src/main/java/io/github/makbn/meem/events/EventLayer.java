package io.github.makbn.meem.events;

import io.github.makbn.meemlocationgraph.LocationGraph;
import io.github.makbn.meemlocationgraph.LocationVertex;
import io.github.makbn.meemlocationgraph.PathEdge;
import io.github.makbn.meemlocationgraph.Utils;
import io.github.makbn.meemmapviewer.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EventLayer {

    private final LayerGroup staticEvents;
    private final Layer dynamicEvents;
    private LayerGroup traceLayer, pathLayer;
    private JMapViewerTree tree;
    private HashMap<String,Layer> layerMap;
    private LocationGraph<LocationVertex, PathEdge<LocationVertex>> locationGraph;

    public EventLayer(LocationGraph<LocationVertex, PathEdge<LocationVertex>> locationGraph,JMapViewerTree tree) {
        this.locationGraph  = locationGraph;
        this.tree           = tree;
        this.layerMap       = new HashMap<>();
        this.traceLayer     = new LayerGroup("Events");
        this.staticEvents   = new LayerGroup(traceLayer,"Static View");
        this.dynamicEvents  = new Layer(traceLayer,"Dynamic View");
        this.pathLayer = new LayerGroup("Paths");
        this.staticEvents.add(pathLayer);

        init();
    }

    private void init() {
        addTraceLayer(0, 500);
        addTraceLayer(500, 1000);
        addTraceLayer(1000, 2000);
        addTraceLayer(2000, 3000);
        addTraceLayer(3000, 5000);
        addTraceLayer(5000, Integer.MAX_VALUE);

        Layer cities = staticEvents.addLayer("Cities");
        Layer states = staticEvents.addLayer("States");


        this.tree.addLayer(cities);
        this.tree.addLayer(states);

        layerMap.put("cities",cities);
        layerMap.put("states",states);

    }

    private void addTraceLayer(int start, int end){
        String s = String.valueOf(start);
        String e = (end == Integer.MAX_VALUE) ? "" : String.valueOf(end);

        Layer layer0 = pathLayer.addLayer(s+"\t\t< W <\t\t"+e);
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
                circleInput = new MapMarkerCircle(layerMap.get("cities"), vertex.geteCity()
                        , new Coordinate(vertex.getLat(), vertex.getLon()), rInput);

                circleOutput = new MapMarkerCircle(layerMap.get("cities"), vertex.geteCity()
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


    public void dynamicView(LocationGraph lg){
        ArrayList<PathEdge> edgs=new ArrayList(lg.getEdges());
        Collections.sort(edgs, new Comparator<PathEdge>() {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS");
            @Override
            public int compare(PathEdge o1, PathEdge o2) {
                try {
                    Date d1=format.parse(o1.getVertexDst().getDate());
                    Date d2=format.parse(o2.getVertexDst().getDate());
                    return d1.compareTo(d2);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        HashMap<String,ArrayList<PathEdge>> dividedByDate=new HashMap<>();
        for(final PathEdge pe:edgs){
            String date=pe.getVertexDst().getDate().substring(0,10);
            if(dividedByDate.containsKey(date)){
                dividedByDate.get(date).add(pe);
            }else {
                dividedByDate.put(date,new ArrayList<PathEdge>(){{
                    add(pe);
                }});
            }

        }
        for (int i = 0; i < edgs.size(); i++) {
            int w = edgs.get(i).getWeight();
            final PathEdge<LocationVertex> p = edgs.get(i);
            if (w > 500 && !p.getVertexSrc().equals(p.getVertexDst())) {
                final MapMarkerCircle[] dot = new MapMarkerCircle[1];
                dot[0] = new MapMarkerDot(dynamicEvents, p.getVertexSrc().getpCity() + "-" + p.getVertexDst().getpCity(), p.getVertexSrc().getLat(), p.getVertexSrc().getLon());
                Runnable r = new Runnable() {
                    @Override
                    public void run() {

                        double x = p.getVertexSrc().getLat();
                        double y = p.getVertexSrc().getLon();

                        double y1 = p.getVertexSrc().getLon();
                        double y2 = p.getVertexDst().getLon();
                        double x1 = p.getVertexSrc().getLat();
                        double x2 = p.getVertexDst().getLat();

                        double m = ((y2 - y1) / (x2 - x1));
                        double constant = y1 - (m * x1);
                        double step = -0.0005;
                        while (true) {
                            step = step * (-1);
                            while (x <= x2 && x >= x1) {
                                x = x + step;
                                y = (m * x) + constant;
                                dot[0].setLat(x);
                                dot[0].setLon(y);
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                tree.getViewer().repaint();
                            }
                            if (step > 0)
                                x = x1;
                            else
                                x = x1;
                        }
                    }
                };
                Thread t = new Thread(r);
                t.start();
                tree.getViewer().addMapMarker(dot[0]);
            }
        }
    }

}
