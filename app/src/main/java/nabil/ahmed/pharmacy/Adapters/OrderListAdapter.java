package nabil.ahmed.pharmacy.Adapters;

import android.content.Context;
//import android.support.annotation.NonNull;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import nabil.ahmed.pharmacy.DatabaseModels.Order;
import nabil.ahmed.pharmacy.Helpers.TimeAgo;
import nabil.ahmed.pharmacy.MainActivity;
import nabil.ahmed.pharmacy.R;

public class OrderListAdapter extends ArrayAdapter<Order> {
    Context mContext;
    ArrayList<Order> mOrders;
    ArrayList<String> mOrdersIds;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private static class ViewHolder {
        TextView userName;
        TextView quantity;
        TextView drugName;
        TextView date;
        ImageButton doneBtn;
    }

    public OrderListAdapter(Context context, ArrayList<Order> orders) {
        super(context, R.layout.order_list_item, orders);
        mOrders = orders;
        mContext = context;
        mOrdersIds = new ArrayList<>();
    }


    @Override
    public Order getItem(int position) {
        return mOrders.get(position);
    }

    @Override
    public void add(Order object) {
        mOrders.add(object);
        notifyDataSetChanged();
    }


    public void addId(String orderId) {
        mOrdersIds.add(orderId);
        notifyDataSetChanged();
    }

    @Override
    public void remove(Order object) {
        mOrders.remove(object);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mOrders.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Order order = getItem(position);

        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.order_list_item, parent, false);
            viewHolder.userName =  convertView.findViewById(R.id.order_item_user_name);
            viewHolder.quantity =  convertView.findViewById(R.id.order_item_quantity);
            viewHolder.drugName =  convertView.findViewById(R.id.order_item_drug_name);
            viewHolder.date = convertView.findViewById(R.id.order_item_date);
            viewHolder.doneBtn = convertView.findViewById(R.id.order_item_done_btn);

//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });

            viewHolder.doneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Deliver Order")
                            .setMessage("Do you really want to deliver this order?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    db.collection("pharmacies").document(uid).collection("orders").document(mOrdersIds.get(position))
                                            .update("state", "done").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(Task<Void> task) {
                                            if (task.isSuccessful()){
                                                remove(order);
                                                FancyToast.makeText(mContext, "Order deleted", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false);
                                            }
                                            else {
                                                FancyToast.makeText(mContext, task.getException().getMessage(), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false);
                                            }
                                        }
                                    });
                                }})
                            .setNegativeButton(android.R.string.no, null).show();


                }
            });


            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        getOrderItemData(viewHolder, order);

        // Return the completed view to render on screen
        return convertView;
    }

    private void getOrderItemData(final ViewHolder viewHolder, final Order order){
        db.collection("users").document(order.userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    final String userName = task.getResult().get("name").toString();
                    db.collection("pharmacies").document(uid).collection("drugs").document(order.drugId)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                String drugName = task.getResult().get("name").toString();
                                viewHolder.userName.setText(userName);
                                viewHolder.quantity.setText(order.quantity);
                                viewHolder.drugName.setText(drugName);
                                viewHolder.date.setText(TimeAgo.TimeAgo(order.timestamp));
                            }
                        }
                    });
                }
            }
        });
    }


}
