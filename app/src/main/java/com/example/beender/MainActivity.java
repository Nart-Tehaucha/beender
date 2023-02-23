package com.example.beender;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.example.beender.model.CurrentItems;
import com.example.beender.ui.dashboard.DashboardFragment;
import com.example.beender.util.Settings;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.GeoApiContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = DashboardFragment.class.getSimpleName();
    public static final GeoApiContext gaContext = new GeoApiContext.Builder().apiKey(BuildConfig.MAPS_API_KEY).build();
    public static int routeType; // 0 - Normal, 1 - Star

    //Get user's preferences (from 'SETTINGS' fragment)
    SharedPreferences sharedPreferences;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        //Get user's preferences (from 'SETTINGS' fragment)
        this.settings = new Settings(this);

        Log.i("kind_of_trip: " +settings.getKindOfTrip(), "INFO");
        Log.i("numOfPlacesPerDay: " + settings.getNumOfPlacesPerDay(), "INFO");
        Log.i("kmRadius: " + settings.getKmRadius().toString(), "INFO");
        Log.i("numOfDaysForTravel: " +settings.getNumOfDaysForTravel(), "INFO");
        Log.i("hoursOfTravel: " + settings.getHoursOfTravel(), "INFO");
        Log.i("adaptedForChildren: " +settings.getAdaptedForChildren(), "INFO");
        Log.i("adaptedForElders: " + settings.getAdaptedForElders(), "INFO");
        Log.i("adapteForAWheelchair: " + settings.getAdaptedForAWheelchair(), "INFO");
        Log.i("ratingStar: " + settings.getRatingStar(), "INFO");
        Log.i("TypeOfPlaces: " + settings.getTypeOfPlaces(), "INFO");
        Log.i("priceLevel: " + settings.getPriceLevel(), "INFO");
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.MyProfileBTN:
                Toast.makeText(this, "My Profile BTN pressed", Toast.LENGTH_SHORT).show();
                break;
            case R.id.logoutBTN:
                CurrentItems.getInstance().reset();
                mAuth.signOut();
                Intent intent = new Intent(this, AuthActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
