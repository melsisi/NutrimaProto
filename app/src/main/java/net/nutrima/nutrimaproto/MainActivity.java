package net.nutrima.nutrimaproto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Populate USDA data from xls ///////////////////////////////////
        List<NutritionUSDAEntry> USDAList = populateList();

        Globals.getInstance().setUSDATable(USDAList);
        //////////////////////////////////////////////////////////////////

        final Button button = (Button) findViewById(R.id.sneak_peak_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent activityChangeIntent = new Intent(MainActivity.this, PersonalInfoActivity.class);
                startActivity(activityChangeIntent);
            }
        });
    }

    private ArrayList<NutritionUSDAEntry> populateList(){
        ArrayList<NutritionUSDAEntry> list = new ArrayList<>();

        InputStream file = null;
        try {
            file = getResources().openRawResource(getResources().getIdentifier("nutrition",
                    "raw", getPackageName()));

            BufferedReader br = new BufferedReader(new InputStreamReader(file));
            //Create Workbook instance holding reference to .xlsx file
            HSSFWorkbook workbook = new HSSFWorkbook(file);

            //Get first/desired sheet from the workbook
            HSSFSheet sheet = workbook.getSheetAt(0);

            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();

            // Skip first two rows
            rowIterator.next();
            rowIterator.next();

            while (rowIterator.hasNext())
            {
                Row row = rowIterator.next();
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();

                Cell cell;

                // i is to index the sheet columns, format dependent
                int i = 0;
                NutritionUSDAEntry newEntry = new NutritionUSDAEntry();
                while (cellIterator.hasNext())
                {
                    cell = cellIterator.next();
                    if( i == 0)
                        newEntry.setFood(cell.getStringCellValue().toLowerCase());
                    else if (i == 1)
                        newEntry.setServing(cell.getStringCellValue());
                    else if (i == 2)
                        newEntry.setWeight(cell.getNumericCellValue());
                    else if (i == 3)
                        newEntry.setCalories(cell.getNumericCellValue());
                    else if (i == 4)
                        newEntry.setTotalFat(cell.getNumericCellValue());
                    else if (i == 5)
                        newEntry.setCarbohydrates(cell.getNumericCellValue());
                    else if (i == 6)
                        newEntry.setProtein(cell.getNumericCellValue());
                    i++;
                }
                list.add(newEntry);
            }
            file.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
