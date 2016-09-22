package net.nutrima.nutrimaproto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class PersonalInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        // Populate age spinner //////////////////////////////////////////
        String[] age_array = new String[90];
        for(int i = 0; i < 90; i++)
            age_array[i] = Integer.toString(i + 10);
        Spinner spinner = (Spinner) findViewById(R.id.age_spinner);
        ArrayAdapter<String> ageSpinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, age_array);
        ageSpinnerAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinner.setAdapter(ageSpinnerAdapter);
        //////////////////////////////////////////////////////////////////

        // Setup Map view button /////////////////////////////////////////
        final Button mapViewButton = (Button) findViewById(R.id.map_view_button);
        mapViewButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent activityChangeIntent = new Intent(PersonalInfoActivity.this, MapActivity.class);
                startActivity(activityChangeIntent);
            }
        });
        //////////////////////////////////////////////////////////////////

        // Setup Log meals button /////////////////////////////////////////
        final Button logMealsButton = (Button) findViewById(R.id.log_meals_button);
        logMealsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent activityChangeIntent = new Intent(PersonalInfoActivity.this, MealLoggerActivity.class);
                startActivity(activityChangeIntent);
            }
        });
        //////////////////////////////////////////////////////////////////
    }
}
