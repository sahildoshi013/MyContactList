package com.example.sahilj.mycontactlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.sahilj.mycontactlist.LoginSignup.LoginFragment;
import com.example.sahilj.mycontactlist.Utils.MyUtilities;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh_screen);


        try {
            ImageView imgLogo = findViewById(R.id.imgWelcomeComapanyLogo);

            final boolean isLogin = MyUtilities.isUserLogin();
            //FadeOut Animation
            Animation startFadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out_animation);

            startFadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (isLogin) {
                        //redirect to Welcome activity
                        Intent welcomeIntent = new Intent(getApplicationContext(), Welcome.class);
                        startActivity(welcomeIntent);
                        finish();

                    } else {
                        //redirect to login page
                        getSupportFragmentManager().beginTransaction().add(R.id.frmLoginContainer, new LoginFragment()).commit();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            startFadeOutAnimation.setDuration(3000);
            imgLogo.startAnimation(startFadeOutAnimation);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
