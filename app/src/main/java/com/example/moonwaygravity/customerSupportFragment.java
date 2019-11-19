package com.example.moonwaygravity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link customerSupportFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link customerSupportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class customerSupportFragment extends Fragment {
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    CardView parking_issue, suspicious_issue, entry_issue, topup_issue, other_issue;

    private OnFragmentInteractionListener mListener;


    public customerSupportFragment() {
        // Required empty public constructor
    }

    public static customerSupportFragment newInstance(String param1, String param2) {
        customerSupportFragment fragment = new customerSupportFragment();

        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_support, container, false);

        parking_issue = view.findViewById(R.id.parking_issue);
        suspicious_issue = view.findViewById(R.id.suspicious_issue);
        entry_issue =view.findViewById(R.id.entry_issue);
        topup_issue = view.findViewById(R.id.topup_issue);
        other_issue = view.findViewById(R.id.other_issue);

        final Bundle bundle = new Bundle();
        final Intent intent = new Intent(getActivity(), CustomerMessageActivity.class);

        parking_issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("Category", "Parking");
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        suspicious_issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("Category", "Suspicious");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        topup_issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("Category", "Top Up");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        other_issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("Category", "Others");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        entry_issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("Category", "Entry");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return view;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }



}
