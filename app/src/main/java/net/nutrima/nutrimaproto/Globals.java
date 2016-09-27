package net.nutrima.nutrimaproto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by melsisi on 9/26/2016.
 */
public class Globals {
    private static List<NutritionUSDAEntry> USDATable;
    private static Globals ourInstance = new Globals();

    public static Globals getInstance() {
        if(ourInstance == null) {
            ourInstance = new Globals();
        }
        return ourInstance;
    }

    protected Globals() {
        USDATable = new ArrayList<NutritionUSDAEntry>();
    }

    protected List<NutritionUSDAEntry> getUSDATable() {
        return USDATable;
    }

    protected void setUSDATable(List<NutritionUSDAEntry> USDATable) {
        Globals.USDATable = USDATable;
    }

}
