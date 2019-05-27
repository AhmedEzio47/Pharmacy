package nabil.ahmed.pharmacy.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.concurrent.SynchronousQueue;

public class LocationHelper {

    private static FusedLocationProviderClient fusedLocationClient;
    private static Location mCurrentLocation;
    private static boolean mOperationDone = false;

    private static SynchronousQueue<Location> queue = new SynchronousQueue<>();


    public static Location getCurrentLocation(final Context context, Activity activity){


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( activity, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    100 );
        }
        else{

            final LocationManager manager = (LocationManager) activity.getSystemService( Context.LOCATION_SERVICE );

            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                buildAlertMessageNoGps(context, activity);
            }

            fusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {

                            if(task.isSuccessful()){
                                if(task.getResult() != null){
                                    FancyToast.makeText(context, "Location acquired: "
                                            , FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                                    //mOperationDone = true;
                                    mCurrentLocation = task.getResult();
//                                    try {
//                                        queue.put(mCurrentLocation);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
                                }
                            }

                            else {
                                FancyToast.makeText(context,task.getException().getMessage()
                                        , FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            }
                        }
                    });
        }



        return mCurrentLocation;
    }

    private static void buildAlertMessageNoGps(Context context, final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
