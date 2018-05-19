package io.github.makbn.meem.evidence;

import io.github.makbn.meemlocationgraph.DirectedPath;
import io.github.makbn.meemlocationgraph.LocationVertex;
import io.github.makbn.meemmapviewer.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EvidenceLayer {

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
       LayerGroup mainPath=new LayerGroup(evidenceLayer,"Main Path");
       LayerGroup railway=new LayerGroup(evidenceLayer,"Railway");
       LayerGroup airline=new LayerGroup(evidenceLayer,"Airline");

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
            Layer layer = group.addLayer(directedPath.toString());
            tree.addLayer(layer);
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
}
