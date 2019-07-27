package nabil.ahmed.pharmacy.Helpers;

import com.google.firebase.Timestamp;

public class TimeAgo {
    public static String TimeAgo(Timestamp timestamp){
        String result = "";

        if((Timestamp.now().getSeconds() - timestamp.getSeconds()) > 3*24*60*60){
            return "Several days";
        }
        else if((Timestamp.now().getSeconds() - timestamp.getSeconds()) > 2*24*60*60){
            return "Couple days";
        }
        else if((Timestamp.now().getSeconds() - timestamp.getSeconds()) > 24*60*60){
            return "One day";
        }
        else if((Timestamp.now().getSeconds() - timestamp.getSeconds()) > 60*60){
            return "" + (Timestamp.now().getSeconds() - timestamp.getSeconds())/60*60 + " hours";
        }
        else if((Timestamp.now().getSeconds() - timestamp.getSeconds()) > 60*60){
            return "One hour";
        }
        else if((Timestamp.now().getSeconds() - timestamp.getSeconds()) > 2*60){
            return "" + (Timestamp.now().getSeconds() - timestamp.getSeconds())/60 + " minutes";
        }
        else if((Timestamp.now().getSeconds() - timestamp.getSeconds()) > 60){
            return "One minute";
        }
        return "Just now";
    }
}
