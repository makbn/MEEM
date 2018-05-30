package io.github.makbn.meem.events;

import io.github.makbn.meemmapviewer.Style;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class EventStyle{

   public static HashMap <Integer, Style> lineStyleMap = new HashMap<>();
   public static HashMap<String, Style> dotStyleMap = new HashMap<>();

    static {

        lineStyleMap.put(5000,new Style(new Color(0, 0, 255, 255)
                ,null
                , new BasicStroke(4.5f)
                ,null));
        lineStyleMap.put(3000,new Style(new Color(0, 0, 255, 220)
                ,null
                , new BasicStroke(3.8f)
                ,null));
        lineStyleMap.put(2000,new Style(new Color(0, 0, 255, 190)
                ,null
                ,new BasicStroke(3.1f)
                ,null));
        lineStyleMap.put(1500,new Style(new Color(0, 0, 255, 130)
                ,null
                , new BasicStroke(2.5f)
                ,null));
        lineStyleMap.put(1000,new Style(new Color(0, 0, 255, 100)
                ,null
                , new BasicStroke(2.2f)
                ,null));
        lineStyleMap.put(500,new Style(new Color(0, 0, 255, 70)
                ,null
                , new BasicStroke(1.8f)
                ,null));
        lineStyleMap.put(0,new Style(new Color(0, 0, 255, 40)
                ,null
                , new BasicStroke(1.5f)
                ,null));
        dotStyleMap.put("cityInputTraffic",new Style(Color.MAGENTA
                , new Color(255, 175, 175,100)
                , null
                , null));
        dotStyleMap.put("cityOutputTraffic",new Style(Color.BLUE
                , new Color(0, 0, 255, 100)
                , null
                , null));
        dotStyleMap.put("stateInputTraffic",new Style(Color.MAGENTA
                , new Color(0, 255, 6, 97)
                , null
                , null));
        dotStyleMap.put("stateOutputTraffic",new Style(Color.BLUE
                , new Color(136,45,97, 100)
                , null
                , null));

    }


    public static Style getLineStyle(int w){
        for (Map.Entry<Integer,Style> entry: lineStyleMap.entrySet()){
            if(w>entry.getKey()){
                return entry.getValue();
            }
        }
        return null;
    }

    public static Style getDotStyle(String type){
        return dotStyleMap.get(type);
    }


    public static Style getDynamicViewDotStyle() {
        return new Style(Color.BLACK, Color.red, (Stroke)null, null);
    }
}
