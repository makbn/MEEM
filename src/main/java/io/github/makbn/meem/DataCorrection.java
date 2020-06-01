package io.github.makbn.meem;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DataCorrection {


    public static void main(String[] args) throws IOException {
        HashMap<String, ArrayList<City>> canadaProvs = getCanadaStates();
        ArrayList<String> provs = new ArrayList<>(canadaProvs.keySet());

        HashMap<String, String> pMap = new HashMap<>();
        HashMap<String, City> cMap = new HashMap<>();

        int pIndex = 0;
        HashMap<String, Integer> cIndex = new HashMap<>();


        File inputFile = new File("/Users/makbn/Downloads/MEEM-2/data/data_vertex.csv");

        CSVReader reader = new CSVReader(new FileReader(inputFile), ',');
        List<String[]> csvBody = reader.readAll();

        for (int i = 0; i < csvBody.size(); i++) {

            String[] cols = csvBody.get(i);

            String pKey = cols[10];
            String prov = null;
            City city = null;
            String cKey = cols[12];
            if (pMap.containsKey(pKey)) {
                prov = pMap.get(pKey);
                if (cMap.containsKey(cKey)) {
                    city = cMap.get(cKey);
                } else {
                    int size = canadaProvs.get(prov).size();
                    city = canadaProvs.get(prov).get(cIndex.get(prov) % size);
                    cMap.put(cKey, city);
                    cIndex.put(prov, cIndex.get(prov) + 1);
                }
            } else {
                prov = provs.get((pIndex++) % provs.size());
                int size = canadaProvs.get(prov).size();
                city = canadaProvs.get(prov).get(0);
                cMap.put(cKey, city);
                cIndex.put(prov, 1);
                cMap.put(cKey, city);
                pMap.put(pKey, prov);
            }

            csvBody.get(i)[10] = prov;
            csvBody.get(i)[9] = prov;

            csvBody.get(i)[12] = city.name;
            csvBody.get(i)[6] = city.name;

            csvBody.get(i)[1] = city.lat + "";
            csvBody.get(i)[2] = city.lon + "";
        }

        reader.close();

        CSVWriter writer = new CSVWriter(new FileWriter("/Users/makbn/Downloads/MEEM-2/data/data_vertex_new.csv"), ',');
        writer.writeAll(csvBody);
        writer.flush();
        writer.close();

    }


    private static HashMap<String, ArrayList<City>> getCanadaStates() {
        JSONParser parser = new JSONParser();
        HashMap<String, ArrayList<City>> provs = new HashMap<>();
        int id = 1;
        try {

            Object obj = parser.parse(new FileReader(
                    "/Users/makbn/Downloads/MEEM-2/data/ca.json"));

            JSONArray companyList = (JSONArray) obj;

            Iterator<JSONObject> iterator = companyList.iterator();
            while (iterator.hasNext()) {
                JSONObject item = iterator.next();
                String key = (String) item.get("admin");
                if (provs.containsKey(key)) {
                    provs.get(key)
                            .add(new City((String) item.get("city"), Double.valueOf((String) item.get("lat")),
                                    Double.valueOf((String) item.get("lng"))));

                } else {
                    String prov = key;
                    ArrayList<City> cities = new ArrayList<>();
                    cities.add(new City((String) item.get("city"), Double.valueOf((String) item.get("lat")),
                            Double.valueOf((String) item.get("lng"))));

                    provs.put(prov, cities);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return provs;

    }
}


class City {

    String name;
    double lat;
    double lon;

    public City(String name, double lat, double lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }
}