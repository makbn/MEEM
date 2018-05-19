package io.github.makbn.meem;

import io.github.makbn.meemmapviewer.JMapViewer;
import io.github.makbn.meemmapviewer.JMapViewerTree;
import io.github.makbn.meemmapviewer.OsmTileLoader;
import io.github.makbn.meemmapviewer.events.JMVCommandEvent;
import io.github.makbn.meemmapviewer.interfaces.JMapViewerEventListener;
import io.github.makbn.meemmapviewer.interfaces.TileLoader;
import io.github.makbn.meemmapviewer.interfaces.TileSource;
import io.github.makbn.meemmapviewer.tilesources.BingAerialTileSource;
import io.github.makbn.meemmapviewer.tilesources.OsmTileSource;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

public class MapView implements JMapViewerEventListener{
    private static MapView MAP_VIEW;
    private static JFrame MAP;
    private final JMapViewerTree treeMap;
    private final JLabel zoomLabel;
    private final JLabel zoomValue;
    private final JLabel mperpLabelName;
    private final JLabel mperpLabelValue;

    private MapView(String name){
        MAP=new JFrame(name);
        treeMap = new JMapViewerTree("Layers");
        // Listen to the map viewer for user operations so components will
        // receive events and update
        map().addJMVListener(this);

        mperpLabelName  = new JLabel("Meters/Pixels: ");
        mperpLabelValue = new JLabel(String.format("%s", map().getMeterPerPixel()));
        zoomLabel       = new JLabel("Zoom: ");
        zoomValue       = new JLabel(String.format("%s", map().getZoom()));

        MAP.setLayout(new BorderLayout());
        MAP.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MAP.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel panel        = new JPanel(new BorderLayout());
        JPanel panelTop     = new JPanel();
        JPanel panelBottom  = new JPanel();
        JPanel helpPanel    = new JPanel();

        MAP.add(panel, BorderLayout.NORTH);
        MAP.add(helpPanel, BorderLayout.SOUTH);
        panel.add(panelTop, BorderLayout.NORTH);
        panel.add(panelBottom, BorderLayout.SOUTH);

        helpPanel.add(new JLabel(Constant.HELP_MESSAGE));

        JComboBox<TileLoader> tileLoader=getTileLoaderSelector();
        JComboBox<TileSource> tileSource=getTileSourceSelector();

        map().setTileLoader((TileLoader) tileLoader.getSelectedItem());

        panelTop.add(tileSource);
        panelTop.add(tileLoader);

        panelBottom.add(getShowMapMarker());
        panelBottom.add(getShowTreeLayers());
        panelBottom.add(getShowTileGrid());
        panelBottom.add(getShowZoomControls());
        panelBottom.add(getDisplayToFitMapMarkersBtn());
        panelBottom.setBackground(Color.WHITE);
        panelTop.setBackground(Color.WHITE);
        helpPanel.setBorder(new LineBorder(Color.gray));
        panelTop.add(zoomLabel);
        panelTop.add(zoomValue);
        panelTop.add(mperpLabelName);
        panelTop.add(mperpLabelValue);

        MAP.add(treeMap, BorderLayout.CENTER);

        map().addMouseListener(getMouseListener());
        map().addMouseMotionListener(getMouseMotionListener());
    }

    public static MapView init(String name){
        if(MAP_VIEW==null)
            MAP_VIEW=new MapView(name);
        return MAP_VIEW;
    }

    public JMapViewerTree getTreeMap() {
        return MAP_VIEW.treeMap;
    }

    public static JFrame getFrame(){
        return MAP;
    }


    private JMapViewer map() {
        return treeMap.getViewer();
    }

    private void updateZoomParameters() {
        if (mperpLabelValue != null)
            mperpLabelValue.setText(String.format("%s", map().getMeterPerPixel()));
        if (zoomValue != null)
            zoomValue.setText(String.format("%s", map().getZoom()));
    }

    private JButton getDisplayToFitMapMarkersBtn(){

        JButton button = new JButton(Constant.SET_DISPLAY_TO_MARKERS);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                map().setDisplayToFitMapMarkers();
            }
        });

        return button;
    }

    private JComboBox<TileSource> getTileSourceSelector(){
        JComboBox<TileSource> tileSourceSelector = new JComboBox<>(new TileSource[]{
                new OsmTileSource.Mapnik(),
                new OsmTileSource.CycleMap(),
                new BingAerialTileSource(),
        });

        tileSourceSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                map().setTileSource((TileSource) e.getItem());
            }
        });

        return tileSourceSelector;
    }


    private JComboBox<TileLoader> getTileLoaderSelector(){
        JComboBox<TileLoader> tileLoaderSelector;
        tileLoaderSelector = new JComboBox<>(new TileLoader[]{new OsmTileLoader(map())});
        tileLoaderSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                map().setTileLoader((TileLoader) e.getItem());
            }
        });

        return tileLoaderSelector;
    }

    private JCheckBox getShowMapMarker(){
        final JCheckBox showMapMarker = new JCheckBox("Map markers visible");
        showMapMarker.setSelected(map().getMapMarkersVisible());
        showMapMarker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                map().setMapMarkerVisible(showMapMarker.isSelected());
            }
        });
        return showMapMarker;
    }

    private JCheckBox getShowTreeLayers(){
        final JCheckBox showTreeLayers = new JCheckBox(Constant.TREE_LAYER_VISIBLE);
        showTreeLayers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                treeMap.setTreeVisible(showTreeLayers.isSelected());
            }
        });
        treeMap.setTreeVisible(true);
        showTreeLayers.setSelected(true);
        return showTreeLayers;
    }

    private JCheckBox getShowTileGrid(){
        final JCheckBox showTileGrid = new JCheckBox("Tile grid visible");
        showTileGrid.setSelected(map().isTileGridVisible());
        showTileGrid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                map().setTileGridVisible(showTileGrid.isSelected());
            }
        });
        return showTileGrid;
    }

    private JCheckBox getShowZoomControls(){
        final JCheckBox showZoomControls = new JCheckBox("Show zoom controls");
        showZoomControls.setSelected(map().getZoomControlsVisible());
        showZoomControls.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                map().setZoomContolsVisible(showZoomControls.isSelected());
            }
        });
        return showZoomControls;
    }

    private MouseListener getMouseListener() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    map().getAttribution().handleAttribution(e.getPoint(), true);
                }
            }
        };
    }

    private MouseMotionListener getMouseMotionListener() {
        return new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                boolean cursorHand = map().getAttribution().handleAttributionCursor(p);
                if (cursorHand) {
                    map().setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    map().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        };
    }

    @Override
    public void processCommand(JMVCommandEvent command) {
        if (command.getCommand().equals(JMVCommandEvent.COMMAND.ZOOM) ||
                command.getCommand().equals(JMVCommandEvent.COMMAND.MOVE)) {
            updateZoomParameters();
        }
    }
}
