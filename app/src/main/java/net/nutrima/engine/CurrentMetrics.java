package net.nutrima.engine;

/**
 * Created by ayehia on 9/30/2016.
 */

public class CurrentMetrics {

    //Calories
    int calories=0;

    //Macro Nutrients
    int protein=0;
    int carbs=0;
    int fat=0;
    int satFat=0;
    int addedsugar=0;
    int fibers=0;

    //Minerals
    int calcium=0; //mg
    int Iron=0; //mg
    int Magnesium=0; //mg
    int phosphorus=0; //mg
    int potassium=0; //mg
    int sodium=0; //mg
    int zinc=0; //mg
    int copper=0; //mcg
    int manganese=0; //mg
    int selenium=0; //mcg

    //Vitamins
    int vitaminA; //mg
    int vitaminE; //mg
    int vitaminD; //IU
    int vitaminC;//mg
    int thiamin;
    int riboflavin;//mg
    int niacin;//mg
    int vitaminB6;//mg
    int vitaminB12; // mcg
    int choline; //mg
    int vitaminK; //mcg
    int folate; //mcg

    //Time in day
    PartOfDay partOfDay=PartOfDay.BEGIN;
}