package com.aihealthcare.config;

import com.aihealthcare.model.entity.DietPlan;
import com.aihealthcare.model.entity.NearbyPlace;
import com.aihealthcare.model.enums.PlaceType;
import com.aihealthcare.repository.DietPlanRepository;
import com.aihealthcare.repository.NearbyPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
public class ContentDataSeeder {

    @Bean
    @Profile("!test")
    CommandLineRunner seedContent(DietPlanRepository dietRepo, NearbyPlaceRepository placeRepo) {
        return args -> {
            if (dietRepo.count() == 0) {
                seedDietPlans(dietRepo);
            }
            if (placeRepo.count() == 0) {
                seedNearbyPlaces(placeRepo);
            }
        };
    }

    private void seedDietPlans(DietPlanRepository repo) {
        Object[][] plans = {
            {"Influenza", "Drink warm fluids|Rest 8+ hours|Eat light soups|Take vitamin C foods",
             "Avoid cold drinks|Avoid oily food|Avoid going out in cold|Avoid smoking",
             "Chicken soup|Garlic ginger tea|Citrus fruits|Warm water with honey",
             "Get plenty of rest and stay hydrated during recovery."},
            {"Common Cold", "Drink warm water|Eat soft foods|Rest well|Gargle with salt water",
             "Avoid ice cream|Avoid fried food|Avoid cold beverages",
             "Hot turmeric milk|Vegetable soup|Steamed vegetables|Herbal tea",
             "Most colds resolve in 7-10 days with proper rest."},
            {"Diabetes", "Eat high-fiber foods|Choose whole grains|Include lean protein|Monitor carb intake",
             "Avoid sugary drinks|Avoid white bread|Avoid processed snacks|Limit sweets",
             "Oats|Leafy greens|Beans and lentils|Nuts in moderation",
             "Maintain regular meal times and monitor blood sugar."},
            {"Heart Disease", "Eat heart-healthy fats|Include omega-3 foods|Eat plenty of vegetables|Reduce salt",
             "Avoid trans fats|Avoid excessive salt|Avoid red meat|Limit alcohol",
             "Salmon|Walnuts|Olive oil|Berries and whole grains",
             "Follow a low-sodium, low-fat diet as advised by your cardiologist."},
            {"Gastroenteritis", "Drink ORS/electrolyte fluids|Eat BRAT diet (banana,rice,apple,toast)|Rest",
             "Avoid dairy|Avoid spicy food|Avoid caffeine|Avoid fatty foods",
             "Plain rice|Boiled potatoes|Bananas|Clear broth",
             "Prevent dehydration by drinking fluids frequently."},
            {"Migraine", "Eat regular meals|Stay hydrated|Include magnesium-rich foods",
             "Avoid trigger foods (chocolate, cheese)|Avoid skipping meals|Avoid alcohol|Avoid caffeine excess",
             "Leafy greens|Almonds|Whole grains|Ginger tea",
             "Identify and avoid personal migraine triggers."},
            {"Hypertension", "Reduce sodium intake|Eat potassium-rich foods|Include whole grains|Eat fruits and vegetables",
             "Avoid processed food|Avoid excess salt|Avoid alcohol|Limit caffeine",
             "Bananas|Spinach|Oats|Low-fat dairy",
             "DASH diet is recommended for blood pressure management."},
            {"COVID-19", "Stay hydrated|Eat protein-rich foods|Rest adequately|Monitor oxygen if needed",
             "Avoid crowds|Avoid strenuous exercise|Avoid smoking",
             "Protein shakes|Vitamin C fruits|Warm soups|Plenty of fluids",
             "Isolate and seek medical help if symptoms worsen."},
        };

        for (Object[] p : plans) {
            repo.save(DietPlan.builder()
                    .diseaseName((String) p[0])
                    .dos((String) p[1])
                    .donts((String) p[2])
                    .dietRecommendations((String) p[3])
                    .generalAdvice((String) p[4])
                    .build());
        }
    }

    private void seedNearbyPlaces(NearbyPlaceRepository repo) {
        Object[][] places = {
            {"City General Hospital", "HOSPITAL", "MG Road, Hyderabad", "Hyderabad", "+91-40-12345678", "24/7"},
            {"Apollo Medical Store", "MEDICAL_STORE", "Banjara Hills, Hyderabad", "Hyderabad", "+91-40-87654321", "8AM-10PM"},
            {"Heart Care Institute", "HOSPITAL", "Road No 12, Hyderabad", "Hyderabad", "+91-40-11223344", "24/7"},
            {"MedPlus Pharmacy", "MEDICAL_STORE", "Hitech City, Hyderabad", "Hyderabad", "+91-40-55667788", "7AM-11PM"},
            {"LifeCare Clinic", "CLINIC", "Kukatpally, Hyderabad", "Hyderabad", "+91-40-99887766", "9AM-9PM"},
            {"Diagnostic Lab Plus", "LAB", "Secunderabad, Hyderabad", "Hyderabad", "+91-40-44332211", "7AM-8PM"},
            {"Bangalore City Hospital", "HOSPITAL", "Indiranagar, Bangalore", "Bangalore", "+91-80-12345678", "24/7"},
            {"HealthPlus Medical Store", "MEDICAL_STORE", "Koramangala, Bangalore", "Bangalore", "+91-80-87654321", "8AM-10PM"},
            {"Mumbai General Hospital", "HOSPITAL", "Andheri West, Mumbai", "Mumbai", "+91-22-12345678", "24/7"},
            {"PharmaCare Store", "MEDICAL_STORE", "Bandra, Mumbai", "Mumbai", "+91-22-87654321", "24/7"},
        };

        for (Object[] p : places) {
            repo.save(NearbyPlace.builder()
                    .name((String) p[0])
                    .type(PlaceType.valueOf((String) p[1]))
                    .address((String) p[2])
                    .city((String) p[3])
                    .phone((String) p[4])
                    .openingHours((String) p[5])
                    .latitude(17.385 + Math.random() * 0.1)
                    .longitude(78.486 + Math.random() * 0.1)
                    .build());
        }
    }
}
