package com.example.sahilj.mycontactlist.LoginSignup;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sahilj.mycontactlist.R;
import com.example.sahilj.mycontactlist.Welcome;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private static final int RC_SIGN_IN_FACEBOOK = 11;
    private static final int RC_SIGN_IN_GOOGLE = 12;
    private static final String TAG = "Login Fragment";
    private static final int LOGIN_VIEW = 1;
    private static final int CODE_VERIFICATION_VIEW = 2;

    private LinearLayout llVerificationLayout;
    private RelativeLayout rvSignLayOut;
    private EditText etMobileNumber;
    private EditText etVerificationField;
    private ProgressBar progressBar;
    private CountryCodePicker ccp;
    private View barView;


    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private boolean mVerificationInProgress = false;
    private Activity mActivity;


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        ccp = view.findViewById(R.id.ccPicker);
        etMobileNumber = view.findViewById(R.id.etMobileNumber);
        ImageView btnSignUp = view.findViewById(R.id.btnSignUp);
        ImageView btnFacebook = view.findViewById(R.id.btnFacebook);
        ImageView btnGoogle = view.findViewById(R.id.btnGoogle);
        progressBar = view.findViewById(R.id.progressbar);
        barView = view.findViewById(R.id.bar);
        etVerificationField = view.findViewById(R.id.etVerificationCode);
        ImageView btnVerifyCode = view.findViewById(R.id.btnVerification);
        rvSignLayOut = view.findViewById(R.id.rvLoginLayout);
        llVerificationLayout = view.findViewById(R.id.llCodeVerificationLayout);
        TextView btnResendCode = view.findViewById(R.id.btnCodeResend);
        mAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(this);
        btnFacebook.setOnClickListener(this);
        btnGoogle.setOnClickListener(this);
        btnResendCode.setOnClickListener(this);
        btnVerifyCode.setOnClickListener(this);


        //change view to initial
        //changeView(LOGIN_VIEW, false);


        //Attach Carrier Number Edit text
        ccp.registerCarrierNumberEditText(etMobileNumber);

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                //changeView(CODE_VERIFICATION_VIEW, false);
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    etMobileNumber.setError("Invalid phone number.");
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                changeView(LOGIN_VIEW,true);
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // [START_EXCLUDE]
                // Update UI
                changeProgressBar();
                changeView(CODE_VERIFICATION_VIEW,false);
                // [END_EXCLUDE]
            }
        };
        // [END phone_auth_callbacks]

        return view;
    }


    //get which activity calling this fragment
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            mActivity=(Activity) context;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        //check verification is in process and user entered number or not and continue process
        if (mVerificationInProgress && ccp.isValidFullNumber()) {
            startPhoneNumberVerification(ccp.getFullNumberWithPlus());
        }
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        try {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    mActivity,               // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks
            // [END start_phone_auth]

            mVerificationInProgress = true;
        }catch (Exception e){
            Log.v(TAG,e.getMessage());
            Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
        }
    }

    //resend verification code
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                mActivity,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        //Verify With Code
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        //Check Verified or Not
        signInWithPhoneAuthCredential(credential);
    }

    // sign in with Mobile Number
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            changeActivity();
                            // [END_EXCLUDE]
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                etVerificationField.setError("Invalid code.");
                                // [END_EXCLUDE]
                            }
                            Toast.makeText(mActivity, "Oops!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignUp:
                if (ccp.isValidFullNumber()) {
                    etMobileNumber.setError(null);
                    changeProgressBar();

                    //Verify Mobile Number
                    startPhoneNumberVerification(ccp.getFullNumberWithPlus());
                } else{
                    // set error message
                    etMobileNumber.setError(getString(R.string.invalid_phone_number));
                }
            break;
            case R.id.btnFacebook:

                // Configure Google Sign In
                List<AuthUI.IdpConfig> providers = Collections.singletonList(
                        new AuthUI.IdpConfig.FacebookBuilder().build());

                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        RC_SIGN_IN_FACEBOOK);

                break;
            case R.id.btnGoogle:

                // Configure Google Sign In
                providers = Collections.singletonList(
                        new AuthUI.IdpConfig.GoogleBuilder().build());

                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        RC_SIGN_IN_GOOGLE);
                break;
            case R.id.btnVerification:
                String code = etVerificationField.getText().toString();

                //Verify Phone Number Using Verification Code
                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.btnCodeResend:

                //Resend Verification Code
                resendVerificationCode(etMobileNumber.getText().toString(),mResendToken);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN_FACEBOOK || requestCode == RC_SIGN_IN_GOOGLE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                changeActivity();
            } else {
                // Sign in failed, check response for error code
                try {
                    Log.v(TAG, "Error Code : " + response.getError().getErrorCode());
                }catch (Exception e){
                    e.printStackTrace();
                }
                Toast.makeText(getContext(), "Oops!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void changeProgressBar() {
        if(progressBar.getVisibility() == View.VISIBLE){
            progressBar.setVisibility(View.INVISIBLE);
            barView.setVisibility(View.VISIBLE);
        }
        else{
            progressBar.setVisibility(View.VISIBLE);
            barView.setVisibility(View.INVISIBLE);
        }
    }

    private void changeView(int loginView, boolean isError) {
        if(isError) {
            changeProgressBar();
            Toast.makeText(getContext(), "No Internet!", Toast.LENGTH_SHORT).show();
            return;
        }

        Animation startFadeOutAnimation;
        final Animation startFadeInAnimation;
        switch (loginView){
            case LOGIN_VIEW:
                startFadeOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out_animation);
                startFadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_animation);
                startFadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        rvSignLayOut.startAnimation(startFadeInAnimation);
                        llVerificationLayout.setVisibility(View.GONE);
                        rvSignLayOut.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                llVerificationLayout.startAnimation(startFadeOutAnimation);

                break;
            case CODE_VERIFICATION_VIEW:
                startFadeOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out_animation);
                startFadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_animation);
                startFadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        llVerificationLayout.startAnimation(startFadeInAnimation);
                        llVerificationLayout.setVisibility(View.VISIBLE);
                        rvSignLayOut.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                rvSignLayOut.startAnimation(startFadeOutAnimation);
                break;
        }
    }

    private void changeActivity() {
        Intent welcomeIntent = new Intent(getContext(), Welcome.class);
        startActivity(welcomeIntent);
        mActivity.finish();
    }

}
