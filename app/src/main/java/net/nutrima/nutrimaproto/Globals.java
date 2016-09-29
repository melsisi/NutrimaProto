package net.nutrima.nutrimaproto;

import net.nutrima.engine.NutrimaMetrics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by melsisi on 9/26/2016.
 */
public class Globals {
    private static List<NutritionUSDAEntry> USDATable;
    private static Globals ourInstance = new Globals();
    private static NutrimaMetrics nutrimaMetrics;

    public static Globals getInstance() {
        if(ourInstance == null) {
            ourInstance = new Globals();
        }
        return ourInstance;
    }

    protected Globals() {
        USDATable = new ArrayList<NutritionUSDAEntry>();
        nutrimaMetrics = new NutrimaMetrics();
    }

    public List<NutritionUSDAEntry> getUSDATable() {
        return USDATable;
    }

    public void setUSDATable(List<NutritionUSDAEntry> USDATable) {
        Globals.USDATable = USDATable;
    }

    public NutrimaMetrics getNutrimaMetrics() {
        return nutrimaMetrics;
    }

    public void setNutrimaMetrics(NutrimaMetrics nutrimaMetrics) {
        Globals.nutrimaMetrics = nutrimaMetrics;
    }
}
