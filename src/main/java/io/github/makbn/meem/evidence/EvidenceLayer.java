package io.github.makbn.meem.evidence;

import io.github.makbn.meemlocationgraph.DirectedPath;
import io.github.makbn.meemlocationgraph.LocationVertex;
import io.github.makbn.meemmapviewer.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class
EvidenceLayer {

    public enum EvidenceType{
        MainPath,RailWay,AirLine;
    }

    private final LayerGroup evidenceLayer;
    private HashMap<String,ArrayList<DirectedPath>> evidences;
    private JMapViewerTree tree;
    private HashMap<String,AbstractLayer> layerMap;


    public EvidenceLayer(JMapViewerTree tree) {
        this.evidenceLayer=new LayerGroup("Evidence");
        this.layerMap=new HashMap<>();
        this.evidences=new HashMap<>();
        this.tree=tree;
        init();
    }

    private void init(){
       LayerGroup mainPath=new LayerGroup(evidenceLayer,"Main Roads");
       LayerGroup railway=new LayerGroup(evidenceLayer,"Railways");
       LayerGroup airline=new LayerGroup(evidenceLayer,"Airlines");


       layerMap.put("mainPath",mainPath);
       layerMap.put("railway",railway);
       layerMap.put("airline",airline);

    }

    public EvidencePathVisualization drawPath(EvidenceType type,ArrayList<DirectedPath> paths) {
        EvidencePathVisualization epv=new EvidencePathVisualization();
        LayerGroup group=getLayerGroup(type);
        MapPolyLine polyLine=null;
        for (DirectedPath directedPath : paths) {
            if (directedPath.getVertices().size() == 0)
                continue;
            Coordinate srcC=null;
            List<Coordinate> coordinates = new ArrayList<Coordinate>();
            Layer layer =null;
            if(directedPath.getAdditionalName()==null) {
                layer = group.addLayer(pathtToString(directedPath));
                tree.addLayer(layer);
            }else {
                LayerGroup airlineLayer=getLayerGroupForAirline(directedPath.getAdditionalName());
                layer =airlineLayer.addLayer(pathtToString(directedPath));
                tree.addLayer(layer);
            }
            for (int i = 0; i < directedPath.getVertices().size(); i++) {
                LocationVertex src = directedPath.getVertices().get(i);
                srcC=new Coordinate(src.getLat(), src.getLon());
                coordinates.add(srcC);
                MapMarkerCircle dotSrc = new MapMarkerDot( layer,src.getLat(),src.getLon());
                dotSrc.setColor(Color.RED);
                epv.getCities().add(dotSrc);
            }
            coordinates.add(srcC);
            polyLine = new MapPolyLine(layer,coordinates);
            polyLine.setName(directedPath.toString());
            polyLine.setStroke(directedPath.getStroke());
            polyLine.setColor(directedPath.getColor());
            epv.getRoads().add(polyLine);

        }
        return epv;
    }

    private LayerGroup getLayerGroupForAirline(String additionalName) {
        if(layerMap.containsKey(additionalName)){
            return (LayerGroup) layerMap.get(additionalName);
        }else {
            LayerGroup layerGroup=new LayerGroup((LayerGroup) layerMap.get("airline"),additionalName);
            layerMap.put(additionalName,layerGroup);
            return layerGroup;
        }
    }

    private LayerGroup getLayerGroup(EvidenceType type) {
        if(type==EvidenceType.RailWay){
            return (LayerGroup) layerMap.get("railway");
        }else if(type==EvidenceType.AirLine){
            return (LayerGroup) layerMap.get("airline");
        }else if(type==EvidenceType.MainPath){
            return (LayerGroup) layerMap.get("mainPath");
        }
        return null;
    }

    public String pathtToString(DirectedPath directedPath) {
        String s = "";

        LocationVertex locationVertex;
        for(Iterator i$ = directedPath.getVertices().iterator(); i$.hasNext(); s = s.concat(locationVertex.geteCity() + " - ")) {
            locationVertex = (LocationVertex)i$.next();
        }
        s=s.substring(0,s.length()-3);
        return s;
    }
}
