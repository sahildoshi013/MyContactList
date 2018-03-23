package com.example.sahilj.mycontactlist.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sahilj.mycontactlist.R;
import com.example.sahilj.mycontactlist.model.Person;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

/**
 * Created by Sahil J on 3/22/2018.
 */

public class MyAdapter extends FirestoreAdapter<MyAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter{

    @NonNull
    @Override
    public String getSectionName(int position) {
        Person personDetail = getSnapshot(position).toObject(Person.class);
        //    Resources resources = itemView.getResources();

        if(personDetail.getFirstName()!=null && !personDetail.getFullName().isEmpty()){
            return personDetail.getFullName().charAt(0)+"";
        }else
            return "#";
    }

    public interface OnContactSelectedListener {

        void onContactSelected(DocumentSnapshot person);

    }

    private OnContactSelectedListener mListener;

    public MyAdapter(Query query, OnContactSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
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

        void bind(final DocumentSnapshot snapshot, final OnContactSelectedListener mListener) {
            final Person personDetail = snapshot.toObject(Person.class);
        //    Resources resources = itemView.getResources();

            String firstChar;
            if(personDetail.getFirstName()!=null && !personDetail.getFullName().isEmpty()){
                firstChar = personDetail.getFullName().charAt(0)+"";
            }else
                firstChar = "#";

            tvDisplayLetter.setText(firstChar);

            tvFullName.setText(personDetail.getFullName());
            tvContactNumber.setText(personDetail.getContactNumber());

            if(personDetail.getContactNumber()==null)
            {
                btnCall.setVisibility(View.GONE);
                btnMessage.setVisibility(View.GONE);
            }else{
                btnCall.setVisibility(View.VISIBLE);
                btnMessage.setVisibility(View.VISIBLE);

                btnCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",personDetail.getContactNumber() , null));
                        view.getContext().startActivity(intent);
                    }
                });
                btnMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendSMS(view,personDetail.getContactNumber());
                    }
                });
            }
            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener!= null) {
                        mListener.onContactSelected(snapshot);
                    }
                }
            });
        }
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
