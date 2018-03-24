package com.example.sahilj.mycontactlist.Adapters;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sahilj.mycontactlist.R;
import com.example.sahilj.mycontactlist.Utils.MyUtilities;
import com.example.sahilj.mycontactlist.model.Person;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

/**
 * Created by Sahil J on 3/22/2018.
 */

public class MyAdapter extends FirestoreAdapter<MyAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter{

    //Interface to handle click event of any item of recyclerView
    public interface OnContactSelectedListener {
        void onContactSelected(DocumentSnapshot person);
    }

    private OnContactSelectedListener mListener;

    protected MyAdapter(Query query, OnContactSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }


    // Use to set First letter to popup of scrollbar
    @NonNull
    @Override
    public String getSectionName(int position) {
        Person personDetail = getSnapshot(position).toObject(Person.class);
        return MyUtilities.getFirstCharacter(personDetail.getFirstName(),personDetail.getLastName());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDisplayLetter;
        private final TextView tvFullName;
        private final TextView tvContactNumber;
        private final ImageView btnMessage;
        private final ImageView btnCall;

        ViewHolder(View inflate) {
            super(inflate);
            tvDisplayLetter = inflate.findViewById(R.id.imgDisplayLetter);
            tvFullName = inflate.findViewById(R.id.tvFullName);
            tvContactNumber = inflate.findViewById(R.id.tvNumber);
            btnMessage = inflate.findViewById(R.id.btnMessage);
            btnCall = inflate.findViewById(R.id.btnCall);
        }

        //Set data of snapshot to view
        void bind(final DocumentSnapshot snapshot, final OnContactSelectedListener mListener) {

            final Person personDetail = snapshot.toObject(Person.class);

            tvDisplayLetter.setText(MyUtilities.getFirstCharacter(personDetail.getFirstName(),personDetail.getLastName()));
            tvFullName.setText(MyUtilities.getFullName(personDetail.getFirstName(),personDetail.getLastName()));
            tvContactNumber.setText(MyUtilities.getContactNumber(personDetail.getMobileNumber()));

            if(MyUtilities.getContactNumber(personDetail.getMobileNumber())==null)//Hide Button if No number Available
            {
                btnCall.setVisibility(View.GONE);
                btnMessage.setVisibility(View.GONE);
            }else{
                btnCall.setVisibility(View.VISIBLE);
                btnMessage.setVisibility(View.VISIBLE);

                //Open Contact dialer with persons first available number
                btnCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",MyUtilities.getContactNumber(personDetail.getMobileNumber()) , null));
                        view.getContext().startActivity(intent);
                    }
                });
                btnMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendSMS(view,MyUtilities.getContactNumber(personDetail.getMobileNumber()));
                    }
                });
            }

            // View Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener!= null) {
                        mListener.onContactSelected(snapshot);
                    }
                }
            });
        }

        //Open SMS app for particular contact
        private void sendSMS(View mContext, String contactNumber) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // At least KitKat
            {
                String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(mContext.getContext()); // Need to change the build to API 19

                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");

                if (defaultSmsPackageName != null)// Can be null in case that there is no default, then the user would be able to choose
                // any app that support this intent.
                {
                    sendIntent.setPackage(defaultSmsPackageName);
                }
                sendIntent.putExtra("address",contactNumber);
                mContext.getContext().startActivity(sendIntent);

            }
            else // For early versions, do what worked for you before.
            {
                Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address",contactNumber);
                mContext.getContext().startActivity(smsIntent);
            }
        }
    }
}
