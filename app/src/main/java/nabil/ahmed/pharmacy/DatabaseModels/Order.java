package nabil.ahmed.pharmacy.DatabaseModels;

import com.google.firebase.Timestamp;

public class Order {
    public String userId;
    public Timestamp timestamp;
    public String quantity;
    public String drugId;
    public String state;
}
