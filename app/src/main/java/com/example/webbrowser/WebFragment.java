package com.example.webbrowser;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;

public class WebFragment extends Fragment {
    private WebView webView;
    private String currentUrl;
    private String lastVisitedUrl;
    private String currentTitle;
    private Bitmap currentIcon;
    private static final String ARG_URL = "url";
    private ViewPager2 viewPager;

    public static WebFragment newInstance(String url) {
        WebFragment fragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            lastVisitedUrl = savedInstanceState.getString("lastVisitedUrl");
            currentTitle = savedInstanceState.getString("currentTitle");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web, container, false);
        webView = view.findViewById(R.id.webView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = requireActivity().findViewById(R.id.viewPager);
        setupWebView();
        if (lastVisitedUrl != null) {
            loadUrl(lastVisitedUrl);
        } else {
            loadInitialUrl();
        }
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setLoadsImagesAutomatically(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                currentUrl = url;
                lastVisitedUrl = url;
                if (favicon != null) {
                    currentIcon = favicon;
                    updateTabAppearance();
                }
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        EditText addressBar = getActivity().findViewById(R.id.addressBar);
                        addressBar.setText(url);
                    });
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                currentUrl = url;
                lastVisitedUrl = url;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                currentTitle = title;
                updateTabAppearance();
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                if (icon != null && !icon.isRecycled()) {
                    currentIcon = icon;
                    updateTabAppearance();
                }
            }
        });
    }

    private void updateTabAppearance() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                TabLayout tabLayout = getActivity().findViewById(R.id.tabLayout);
                int position = viewPager.getCurrentItem();
                TabLayout.Tab tab = tabLayout.getTabAt(position);
                if (tab != null && tab.getCustomView() != null) {
                    ImageView iconView = tab.getCustomView().findViewById(R.id.tab_icon);
                    TextView titleView = tab.getCustomView().findViewById(R.id.tab_title);

                    if (currentTitle != null) {
                        titleView.setText(currentTitle);
                    }

                    if (currentIcon != null && !currentIcon.isRecycled()) {
                        iconView.setImageBitmap(currentIcon);
                    }
                }
            });
        }
    }

    private void loadInitialUrl() {
        if (getArguments() != null) {
            String url = getArguments().getString(ARG_URL);
            if (url != null) {
                loadUrl(url);
            }
        }
    }

    public void loadUrl(String url) {
        if (webView != null) {
            webView.loadUrl(url);
            currentUrl = url;
            lastVisitedUrl = url;
        }
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("lastVisitedUrl", lastVisitedUrl);
        outState.putString("currentTitle", currentTitle);
    }

    @Override
    public void onDestroyView() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        if (webView != null) {
            webView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (webView != null) {
            webView.onResume();
        }
        super.onResume();
    }
}