package com.example.beender.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.beender.model.CurrentItems;
import com.example.beender.model.ItemModel;
import com.example.beender.model.UserTrip;
import com.example.beender.ui.dashboard.DashboardFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.maps.model.LatLng;
import com.google.type.DateTime;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FireStoreUtils {
    private static final String TAG = DashboardFragment.class.getSimpleName();
    private static FirebaseAuth mAuth;
    private static boolean flag;

    public static boolean archiveTrip() {
        flag = false;

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        String key = "xx_" + UUID.randomUUID();
        String userEmail = currentUser.getEmail();
        String dateTime = java.time.LocalDate.now().toString();
        HashMap<Integer, ArrayList<ItemModel>> swipedRight = CurrentItems.getInstance().getSwipedRight();

        // Get the first destination of the trip, save its image as a thumbnail in Firebase Storage.
        ItemModel placeholder = swipedRight.get(0).get(1);
        Bitmap thumbnail = placeholder.getImage();
        String title = placeholder.getCity();
        // Upload the image as a compressed thumbnail. The name of the image is the ID of the archived trip.
        FireStoreUtils.uploadImage(thumbnail, key);


        // Convert HashMap to ArrayList
        //List<List<LatLng>> hashToList = new ArrayList<>();
        HashMap<String, List<LatLng>> stringHash = new HashMap<>();

        swipedRight.forEach((k, v) -> {
            //hashToList.add(CurrentItems.getInstance().getAsLatLng(k));
            String strKey = String.valueOf(k);
            stringHash.put(strKey, CurrentItems.getInstance().getAsLatLng(k));
        });

        UserTrip userTrip = new UserTrip(stringHash, userEmail, dateTime, title, key);

        FirebaseFirestore fdb = FirebaseFirestore.getInstance();
        fdb.collection("trips")
                .document(key)
                .set(userTrip)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        flag = true;
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                flag = false;
            }
        });

        return flag;
    }

    public static void getTrips() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ArrayList<UserTrip> trips = new ArrayList<>();

        db.collection("trips")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                if(document.get(""))
                            }
                        } else {
                        }
                    }
                });
    }

    public static void uploadImage (Bitmap bitmap, String title) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        Bitmap compressedImage = ImageTools.compressImage(bitmap);

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        // Create a child reference
        StorageReference thumbnailRef = storageRef.child("thumbnails/" + title + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = thumbnailRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }

    // Downloads image from Firebase storage and loads in inside the ImageView
    public static void downloadImage(ImageView imageView, String pathname, Context context) {
        FirebaseStorage storage= FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference storageReference = storageRef.child(pathname);
        storageReference.getDownloadUrl().addOnCompleteListener(
                new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            String downloadUrl = task.getResult().toString();
                            Glide.with(context)
                                    .load(downloadUrl)
                                    .dontAnimate()
                                    .into(imageView);
                        } else {
                            System.out.println( "Getting download url was not successful."+
                                    task.getException());
                        }
                    }
                });
    }
}
