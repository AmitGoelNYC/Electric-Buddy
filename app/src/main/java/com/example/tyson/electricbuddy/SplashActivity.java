package com.example.tyson.electricbuddy;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.RandomTransitionGenerator;
import com.flaviofaria.kenburnsview.Transition;

import static com.flaviofaria.kenburnsview.KenBurnsView.TransitionListener;

public class SplashActivity extends AppCompatActivity implements TransitionListener{

    KenBurnsView kbv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_acitvity);

        kbv = (KenBurnsView) findViewById(R.id.img);
        AccelerateDecelerateInterpolator ACCELERATE_DECELERATE = new AccelerateDecelerateInterpolator();
        RandomTransitionGenerator generator = new RandomTransitionGenerator(4000, ACCELERATE_DECELERATE);
        kbv.setTransitionGenerator(generator);
        kbv.setTransitionListener(this);

        final ImageView imageView = (ImageView) findViewById(R.id.imageAnim);
        Animation an = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.tweeen_animation);
        imageView.startAnimation(an);
    }

    private void loadApp() {
        Intent intent = new Intent(this, SecondActivity.class);
        if(android.os.Build.VERSION.SDK_INT > 20) {
            getWindow().setExitTransition(new Slide());
            startActivity(intent, ActivityOptions
                    .makeSceneTransitionAnimation(this).toBundle());
        }else
            startActivity(intent);
    }

    @Override
    public void onTransitionStart(Transition transition) {
        Log.d("start", "transition");
    }

    @Override
    public void onTransitionEnd(Transition transition) {
        kbv.pause();
        Log.d("stop", "transition");
        loadApp();
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }
}
