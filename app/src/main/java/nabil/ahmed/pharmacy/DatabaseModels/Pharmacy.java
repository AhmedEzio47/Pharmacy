package nabil.ahmed.pharmacy.DatabaseModels;

import com.google.firebase.firestore.GeoPoint;

public class Pharmacy {
    public String name;
    public String address;
    public String phone;
    public String mobile;
    public boolean hasDeliveryService;
    public GeoPoint location;
    public String notes;

}
