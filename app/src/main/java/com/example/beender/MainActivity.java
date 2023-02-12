package com.example.beender;

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

import com.example.beender.ui.dashboard.DashboardFragment;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.maps.GeoApiContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = DashboardFragment.class.getSimpleName();
    public static final GeoApiContext gaContext = new GeoApiContext.Builder().apiKey(BuildConfig.MAPS_API_KEY).build();
    public static int routeType; // 0 - Normal, 1 - Star

    //Get user's preferences (from 'SETTINGS' fragment)
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // enter the key from your xml and the default value
        String kind_of_trip = sharedPreferences.getString("kind_of_trip","");
        String numOfPlacesPerDay = sharedPreferences.getString("numOfPlacesPerDay","");
        Integer kmRadius = sharedPreferences.getInt("kmRadius", 1 );
        String numOfDaysForTravel = sharedPreferences.getString("numOfDaysForTravel","");
        Boolean adaptedForAWheelchair = sharedPreferences.getBoolean("adaptedForAWheelchair",false);
        String ratingStar = sharedPreferences.getString("ratingStar","");
        Set<String>  priceLevel = sharedPreferences.getStringSet("priceLevel",new HashSet<>());

        Log.i("kind_of_trip: " +kind_of_trip, "INFO");
        Log.i("numOfPlacesPerDay: " + numOfPlacesPerDay, "INFO");
        Log.i("kmRadius: " + kmRadius.toString(), "INFO");
        Log.i("numOfDaysForTravel: " +numOfDaysForTravel, "INFO");
        Log.i("adaptedForAWheelchair: " + adaptedForAWheelchair, "INFO");
        Log.i("ratingStar: " + ratingStar, "INFO");
        Log.i("priceLevel: " + priceLevel, "INFO");
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
                Toast.makeText(this, "Logout BTN pressed", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
