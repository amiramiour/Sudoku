package com.example.sudoku;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class PresentationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new PresentationPagerAdapter(getSupportFragmentManager()));

        Button buttonSkip = findViewById(R.id.button_skip);
        buttonSkip.setVisibility(View.VISIBLE);
        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Passer à l'écran suivant (MenuActivity dans votre cas)
                Intent intent = new Intent(PresentationActivity.this, MenuActivity.class);
                startActivity(intent);
                finish(); // Optionnel, pour fermer l'activité de présentation après avoir sauté
            }
        });
    }

    private class PresentationPagerAdapter extends FragmentPagerAdapter {

        PresentationPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new PresentationFragment1();
                case 1:
                    return new PresentationFragment2();
                case 2:
                    return new PresentationFragment3();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3; // Nombre total de pages dans la présentation
        }
    }
    public static class PresentationFragment1 extends Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.presentationfragment1, container, false);
            return view;
        }
    }
    public static class PresentationFragment2 extends Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.presentationfragment2, container, false);
            return view;
        }
    }
    public static class PresentationFragment3 extends Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.presentationfragment3, container, false);
            return view;
        }
    }

}
