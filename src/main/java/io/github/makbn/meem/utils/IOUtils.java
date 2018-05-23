package io.github.makbn.meem.utils;

import io.github.makbn.meem.App;
import io.github.makbn.meemlocationgraph.GraphFactory;
import io.github.makbn.meemlocationgraph.LocationGraph;
import io.github.makbn.meemlocationgraph.LocationVertex;
import io.github.makbn.meemlocationgraph.Utils;
import io.github.makbn.meempreprocessor.Preprocessor;
import io.github.makbn.meempreprocessor.StarterEventListener;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class IOUtils {


    public static void saveToFile(String csv, String FILENAME) {
        File file = new File(FILENAME);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME))) {
            bw.write(csv);
            System.out.println("Done");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<String> readLines(File file) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            if (line != null && !line.isEmpty())
                lines.add(line);
        }
        return lines;

    }

    public static LocationGraph createFromSQL(String path){
        try {
            return GraphFactory.createFromSQL("127.0.0.1",
                    "ExMdatabase",
                    "DBUser3",
                    "DBPass3",
                    null,
                    null,
                    null,
                    null,
                        path);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static LocationGraph loadData(final String RUNNING_PATH) throws IOException {
        LocationGraph lg=null;
        File vertexData = new File(RUNNING_PATH + "/data/data_vertex.csv");
        File edgeData = new File(RUNNING_PATH + "/data/data_edges.csv");


        if (vertexData.exists() && edgeData.exists()) {

            lg = GraphFactory.createEmpty();
            ArrayList<String> vertexRaw = IOUtils.readLines(vertexData);
            ArrayList<String> edgeRaw = IOUtils.readLines(edgeData);
            for (String s : vertexRaw) {
                lg.addVertex(LocationVertex.getLocationVertex(s));
            }

            for (String s : edgeRaw) {
                String[] data = s.split(",");
                lg.createEdge(lg.findById(Integer.parseInt(data[0])),
                        lg.findById(Integer.parseInt(data[1]))).setWeight(Integer.parseInt(data[2]));
            }

        } else {
            Preprocessor.start(new String[]{RUNNING_PATH}, new StarterEventListener() {
                @Override
                public void FileCopied(File vertex, File edge) {
                    App.main(null);
                }

                @Override
                public void queryExcuted(LocationGraph lg) {
                    IOUtils.saveToFile(lg.getEdgeCSV(), RUNNING_PATH + "/data/data_edges.csv");
                    IOUtils.saveToFile(lg.getVerticesCSV(), RUNNING_PATH + "/data/data_vertex.csv");
                    App.main(null);
                }
            });
           /* lg = IOUtils.createFromSQL(RUNNING_PATH);
            IOUtils.saveToFile(lg.getEdgeCSV(), RUNNING_PATH + "/data/data_edges.csv");
            IOUtils.saveToFile(lg.getVerticesCSV(), RUNNING_PATH + "/data/data_vertex.csv");*/
        }
        Utils.init(lg, RUNNING_PATH);
        return lg;
    }
}
