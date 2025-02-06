package com.example.webbrowser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import android.webkit.URLUtil;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TabAdapter tabAdapter;
    private EditText addressBar;
    private ImageButton goButton;
    private ImageButton newTabButton;
    private List<WebFragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupTabSystem();
        setupClickListeners();
    }

    private void initializeViews() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        addressBar = findViewById(R.id.addressBar);
        goButton = findViewById(R.id.goButton);
        newTabButton = findViewById(R.id.newTabButton);
        goButton.setImageResource(android.R.drawable.ic_media_play);
        newTabButton.setImageResource(android.R.drawable.ic_input_add);
        fragments = new ArrayList<>();
    }

    private void setupTabSystem() {
        fragments.add(WebFragment.newInstance("https://www.google.com"));
        tabAdapter = new TabAdapter(this, fragments);
        viewPager.setAdapter(tabAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            View customView = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            ImageView iconView = customView.findViewById(R.id.tab_icon);
            TextView titleView = customView.findViewById(R.id.tab_title);
            titleView.setText("New Tab");
            tab.setCustomView(customView);
        }).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                WebFragment fragment = fragments.get(position);
                addressBar.setText(fragment.getCurrentUrl());
            }
        });
    }

    private void setupClickListeners() {
        goButton.setOnClickListener(v -> {
            String url = addressBar.getText().toString();
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            if (URLUtil.isValidUrl(url)) {
                int currentItem = viewPager.getCurrentItem();
                fragments.get(currentItem).loadUrl(url);
                addressBar.setText(url);
            }
        });

        newTabButton.setOnClickListener(v -> {
            fragments.add(WebFragment.newInstance("https://www.google.com"));
            tabAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(fragments.size() - 1);
        });
    }
}