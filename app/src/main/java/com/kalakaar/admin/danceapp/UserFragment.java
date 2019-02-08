package com.kalakaar.admin.danceapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class UserFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";
    private static final String ARG_PARAM6 = "param6";
    private static final String ARG_PARAM7 = "param7";

    TextView nametext,addresstxt,mobiletxt,emailtxt,gendertxt,statetxt,citytxt;
    private String name, address,mobile,email,gender,state,city;

    private OnFragmentInteractionListener mListener;

    public UserFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2,String param3,String param4, String param5,String param6,String param7) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        args.putString(ARG_PARAM5, param5);
        args.putString(ARG_PARAM6, param6);
        args.putString(ARG_PARAM7, param7);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_PARAM1);
            address = getArguments().getString(ARG_PARAM2);
            mobile=getArguments().getString(ARG_PARAM3);
            email=getArguments().getString(ARG_PARAM4);
            gender=getArguments().getString(ARG_PARAM5);
            state=getArguments().getString(ARG_PARAM6);
            city=getArguments().getString(ARG_PARAM7);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_user, container, false);

        getDialog().setTitle("Simple Dialog");

        nametext=(TextView)rootView.findViewById(R.id.editnames);
        addresstxt=(TextView)rootView.findViewById(R.id.editaddress);
        mobiletxt=(TextView)rootView.findViewById(R.id.editcont);
        emailtxt=(TextView)rootView.findViewById(R.id.editemail);
        gendertxt=(TextView)rootView.findViewById(R.id.editgen);
        statetxt=(TextView)rootView.findViewById(R.id.editstate);
        citytxt=(TextView)rootView.findViewById(R.id.editcity);

        nametext.setText(name);
        addresstxt.setText(address);
        mobiletxt.setText(mobile);
        emailtxt.setText(email);
        gendertxt.setText(gender);
        statetxt.setText(state);
        citytxt.setText(city);

        Button dismiss = (Button) rootView.findViewById(R.id.button);
        dismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            /*throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");*/
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
