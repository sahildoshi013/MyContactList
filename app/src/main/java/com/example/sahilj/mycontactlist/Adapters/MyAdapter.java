package com.example.sahilj.mycontactlist.Adapters;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


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
            Person personDetail = snapshot.toObject(Person.class);
        //    Resources resources = itemView.getResources();

            String firstChar;
            if(personDetail.getFirstName()!=null && !personDetail.getFullName().isEmpty()){
                firstChar = personDetail.getFullName().charAt(0)+"";
            }else
                firstChar = "#";

            tvDisplayLetter.setText(firstChar);

            tvFullName.setText(personDetail.getFullName());
            tvContactNumber.setText(personDetail.getContactNumber());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener!= null) {
                        mListener.onContactSelected(snapshot);
                    }
                }
            });

            btnCall.setOnClickListener(this);
            btnMessage.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnCall:
                    Toast.makeText(itemView.getContext(), "Call " + this.getPosition(), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btnMessage:
                    Toast.makeText(itemView.getContext(), "Message " + this.getPosition(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
