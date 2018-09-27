package io.github.makbn.meem;

import io.github.makbn.meem.events.EventLayer;
import io.github.makbn.meem.evidence.EvidenceLayer;
import io.github.makbn.meem.evidence.EvidencePathVisualization;
import io.github.makbn.meem.utils.Config;
import io.github.makbn.meem.utils.IOUtils;
import io.github.makbn.meemlocationgraph.*;

import io.github.makbn.meemmapviewer.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class App{

    private static LocationGraph<LocationVertex, PathEdge<LocationVertex>> lg;
    public static String RUNNING_PATH;
    private static boolean isDynamicEnabled=false;

    private static Coordinate c(double lat, double lon) {
        return new Coordinate(lat, lon);
    }

    /**
     * @param args Main program arguments
     */
    public static void main(final String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel (new NimbusLookAndFeel());
                    starter(args);
                } catch (UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void starter(String[] args){
        Config.fixEncoding();
        MapView meem =MapView.init(Constant.APP_NAME);

        try {
            if (lg == null) {

                RUNNING_PATH = App.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
                System.out.println("raw RP:"+RUNNING_PATH);
                ClassLoader classLoader = App.class.getClassLoader();
                File file = new File(classLoader.getResource("data/").getFile());
                System.out.println(file.isDirectory());

                Utils.init(null, RUNNING_PATH);
                if (RUNNING_PATH.charAt(RUNNING_PATH.length() - 1) != '/') {
                    RUNNING_PATH = RUNNING_PATH.substring(0, RUNNING_PATH.lastIndexOf("/"));
                }
                System.out.println("RP:"+RUNNING_PATH);

                lg=IOUtils.loadData(RUNNING_PATH);
                ArrayList<DirectedPath> mainPth = MainPathFactory.mainPath(RUNNING_PATH + "/data/path.xls");
                HashMap<DirectedPath.PathType,ArrayList<DirectedPath>> otherPath=MainPathFactory.airAndRailWay(RUNNING_PATH + "/data/path_other_ca.xls");

                final EventLayer eventLayer=new EventLayer(lg,meem.getTreeMap());
                final EvidenceLayer evidenceLayer=new EvidenceLayer(meem.getTreeMap());

                //Add routing path of each trace
                meem.getTreeMap().setAutoscrolls(true);
                meem.getTreeMap().getViewer().getMapPolygonList().addAll(eventLayer.getTraceGraph());
                meem.getTreeMap().getViewer().getMapMarkerList().addAll(eventLayer.getCityTraffic());
                meem.getTreeMap().getViewer().getMapMarkerList().addAll(eventLayer.getStateTraffic());
                meem.getTreeMap().getViewer().repaint();
                lg.updateMaxTraffic();


               // EvidencePathVisualization mainPath=evidenceLayer.drawPath(EvidenceLayer.EvidenceType.MainPath,mainPth);
            //    meem.getTreeMap().getViewer().getMapMarkerList().addAll(mainPath.getCities());
            //    meem.getTreeMap().getViewer().getMapPolygonList().addAll(mainPath.getRoads());


                EvidencePathVisualization railway=evidenceLayer.drawPath(EvidenceLayer.EvidenceType.RailWay
                        , otherPath.get(DirectedPath.PathType.railway));
                meem.getTreeMap().getViewer().getMapMarkerList().addAll(railway.getCities());
                meem.getTreeMap().getViewer().getMapPolygonList().addAll(railway.getRoads());


                EvidencePathVisualization airline=evidenceLayer.drawPath(EvidenceLayer.EvidenceType.AirLine
                        , otherPath.get(DirectedPath.PathType.airline));
                meem.getTreeMap().getViewer().getMapMarkerList().addAll(airline.getCities());
                meem.getTreeMap().getViewer().getMapPolygonList().addAll(airline.getRoads());

                meem.getTreeMap().getViewer().repaint();
                MapView.getDynamicBtn().addActionListener((new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(!isDynamicEnabled) {
                            eventLayer.dynamicView(lg);
                            isDynamicEnabled=true;
                        }
                    }
                }));

                meem.getFrame().setVisible(true);

            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }


}
