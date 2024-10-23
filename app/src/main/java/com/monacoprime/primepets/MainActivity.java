package com.monacoprime.primepets;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.monacoprime.primepets.adapter.AutoCompleteAdapter;
import com.monacoprime.primepets.adapter.TabsAdapter;
import com.monacoprime.primepets.dialogs.FilterDialog;
import com.monacoprime.primepets.eventbus.MessageEB;
import com.monacoprime.primepets.services.JobSchedulerService;
import com.monacoprime.primepets.slider.Slider;
import com.monacoprime.primepets.utils.ConnectionUtils;
import com.monacoprime.primepets.utils.MetricsUtils;
import com.monacoprime.primepets.views.SwipeDetectLinearLayout;
import com.monacoprime.primepets.widgets.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import me.tatarka.support.job.JobInfo;
import me.tatarka.support.job.JobScheduler;

public class MainActivity extends BaseAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private static final String TAG = "MainActivity";

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private TabsAdapter mTabsAdapter;
    private ViewGroup mTabStrip;
    private static final int[] THEME_ATTRS = new int[]{
            android.R.attr.homeAsUpIndicator
    };
    private EditText etBusca;
    private TextView tvToolbarTitle;
    private LinearLayout llWraperToolbar, llListAutoComplete;
    private CoordinatorLayout clThis;
    private View overlayAllContent, overlayToolbar;
    private ListView lvAutoComplete;
    private Toolbar toolbar;
    private FrameLayout flBusca;
    private AutoCompleteAdapter autoCompleteAdapter;
    private Button btClearBusca;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private boolean searchModeOpened = false, listAutoCompleteOpen = false;
    private DrawerArrowDrawable drawerArrowDrawableManually;
    private SwipeRefreshLayout srlHome;

    private ScrollView svHome;
    private RelativeLayout rlOptionsCat;
    private LinearLayout llFundoToolbar;
    private SwipeDetectLinearLayout llMainLayer;
    private GestureDetectorCompat mDetector;
    private LinearLayout llWrapperOptions;
    private LayoutInflater inflater;
    private SwitchCompat svListPlaces;
    private RelativeLayout rlWrapperList;
    private LinearLayout llListPlaces;
    private TextView tvMode;
    private SupportMapFragment mapFragment;
    private int initialPaddingTopMainLayer;

    private GoogleApiClient client;
    private GoogleMap map;
    private Marker markerUser;
    private Circle mCircle;
    private static final double DEFAULT_RADIUS_IN_METERS = 100.0;

    private HomeStatus homeStatus = HomeStatus.INITIAL_HOME;

    public enum HomeStatus {
        LIST_PLACES,
        INITIAL_HOME,
        MAP
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getSupportFragmentManager().beginTransaction().hide(mapFragment).commit();
        startTracking();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configuracao da sliding tabs
//        mTabsAdapter = new TabsAdapter(getSupportFragmentManager(), this);
//        mViewPager = (ViewPager) findViewById(R.id.vp_tabs);
//        mViewPager.setAdapter(mTabsAdapter);
//        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs);
//        mSlidingTabLayout.setDistributeEvenly(true);
//        mSlidingTabLayout.setCustomTabView(R.layout.tab_view, R.id.tv_tab);
//        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(android.R.color.white));
//        mTabStrip = (ViewGroup) mSlidingTabLayout.getTabStrip();
//        mSlidingTabLayout.setViewPager(mViewPager);

        // Configuracao do Navigation Drawer e o botao toggle pra abri-lo
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        getSupportActionBar().setHomeButtonEnabled(true);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        inflater = getLayoutInflater();
        initializeComponentsToolbar();
        showHome();
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    // Metodos toolbar
    public void changeHambButton(boolean open) {
        if (open) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            toggle.setDrawerIndicatorEnabled(false);
            toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleSearchMode(false);
                }
            });

            drawerArrowDrawableManually = new DrawerArrowDrawable(this);
            drawerArrowDrawableManually.setColor(ContextCompat.getColor(this, R.color.colorSecondaryText));
            toolbar.setNavigationIcon(drawerArrowDrawableManually);

            // animacao de hamburguer para arrows
            ObjectAnimator.ofFloat(drawerArrowDrawableManually, "progress", 1).start();
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            // animacao de arrows para hamburguer
            ObjectAnimator o = ObjectAnimator.ofFloat(drawerArrowDrawableManually, "progress", 0);
            o.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    toggle.setDrawerIndicatorEnabled(true);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            o.start();
        }
    }
    public void initializeComponentsToolbar() {
        clThis = (CoordinatorLayout) findViewById(R.id.cl_main_activity);
        llWraperToolbar = (LinearLayout) findViewById(R.id.wrapper_toolbar);
        llListAutoComplete = (LinearLayout) findViewById(R.id.ll_list_auto_complete);
        lvAutoComplete = (ListView) findViewById(R.id.lv_auto_complete);
        flBusca = (FrameLayout) findViewById(R.id.fl_busca);

        overlayToolbar = findViewById(R.id.overlay_search_toolbar);
        overlayAllContent = findViewById(R.id.overlay_all_content);
        overlayAllContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                //verifica se nao esta clicando em cima da toolbar
                float x = llWraperToolbar.getX();
                float y;
                float w = llWraperToolbar.getWidth();
                float h = llWraperToolbar.getHeight();
                Rect rect = new Rect();
                llWraperToolbar.getGlobalVisibleRect(rect);
                y = rect.top;
                if (!(e.getX() >= x && e.getX() <= x + w && e.getRawY() >= y && e.getRawY() <= y + h) && searchModeOpened) {
                    toggleSearchMode(false);
                }
                return false;
            }
        });
        tvToolbarTitle = (TextView) findViewById(R.id.tv_title_toolbar);
        tvToolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSearchMode(true);
            }
        });

        btClearBusca = (Button) findViewById(R.id.bt_clear_busca);
        btClearBusca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchModeOpened && etBusca != null && etBusca.getText().length() > 0) {
                    etBusca.setText("");
                    btClearBusca.setVisibility(View.GONE);
                }
            }
        });

        etBusca = (EditText) findViewById(R.id.et_busca);
        etBusca.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });
        etBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchModeOpened) {
                    if (s.toString().length() > 0) {
                        btClearBusca.setVisibility(View.VISIBLE);
                    } else {
                        btClearBusca.setVisibility(View.GONE);
                    }
                    populateAutoCompleteList(getAutoCompleteMatchs(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        llFundoToolbar = (LinearLayout) findViewById(R.id.fundo_toolbar); // fundo
        rlOptionsCat = (RelativeLayout) findViewById(R.id.ll_options_cats); // categorias
        llWrapperOptions = (LinearLayout) findViewById(R.id.ll_wrapp_options_cat); // wrapp categorias

        initializeOptionsToolbar();
        initializeSwipeDetect();
    }
    public List<SpannableString> getAutoCompleteMatchs(String textoDigitado) {
        String[] textos = {"Ivoti", "Sapucaia", "Canoas", "Ivoti alterado", "Sapucaia alterado", "Canoas alterado"};
        List<SpannableString> matches = new ArrayList<SpannableString>();

        if (!textoDigitado.isEmpty()) {
            for (int i = 0; i < textos.length; i++) {
                String texto = textos[i].toLowerCase();

                int pos = texto.indexOf(textoDigitado);
                if (pos != -1) {
                    SpannableString match = new SpannableString(texto);
                    int endCharPos = pos + textoDigitado.length();
                    int color = getResources().getColor(R.color.colorAutoCompliteMatch);
                    match.setSpan(new ForegroundColorSpan(color), pos, endCharPos, Spanned.SPAN_POINT_POINT);
                    match.setSpan(new StyleSpan(Typeface.BOLD), pos, endCharPos, Spanned.SPAN_POINT_POINT);
                    matches.add(match);
                }
            }
        } else if (autoCompleteAdapter != null) {
            autoCompleteAdapter.clear();
        }
        return matches;
    }
    public void populateAutoCompleteList(List<SpannableString> matchs) {
        if (matchs.size() > 0) {
            if (autoCompleteAdapter == null) {
                autoCompleteAdapter = new AutoCompleteAdapter(this, R.layout.item_autocomplete, matchs);
                lvAutoComplete.setAdapter(autoCompleteAdapter);
                lvAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SpannableString textChosed = autoCompleteAdapter.getItem(position);
                        etBusca.setText(textChosed.toString());
                        etBusca.setSelection(etBusca.getText().toString().length());
                        slideToolbarHeight(false);
                        search();
                    }
                });
            } else {
                autoCompleteAdapter.reloadList(matchs);
            }

            slideToolbarHeight(true);
        } else {
            slideToolbarHeight(false);
        }
    }
    public void slideToolbarHeight(boolean down) {
        int heightToolbarCurrent = llWraperToolbar.getMeasuredHeight();
        int heightToolbar = (int) getResources().getDimension(R.dimen.toolbar_height);
        int heightItemList = 129, heightList = heightToolbar;

        if (autoCompleteAdapter != null && autoCompleteAdapter.getCount() > 0) {
            heightList = (heightItemList * autoCompleteAdapter.getCount()) + heightToolbar;
        }

        if (down) {
            toggleAnimationToolbarHeight(heightToolbarCurrent, heightList);
            listAutoCompleteOpen = true;
        } else {
            toggleAnimationToolbarHeight(heightToolbarCurrent, heightToolbar);
            listAutoCompleteOpen = false;
            if (autoCompleteAdapter != null) {
                autoCompleteAdapter.clear();
            }
        }
    }

    public void toggleSearchMode(boolean opening) {
        if (opening) {
            searchModeOpened = true;
            changeHambButton(true);
            tvToolbarTitle.setVisibility(View.GONE);
            flBusca.setVisibility(View.VISIBLE);
            requestFocusAndForceKeyboardOpen(etBusca);
            overlayAllContent.setVisibility(View.VISIBLE);
            animationOverlayFade(false, overlayAllContent, ContextCompat.getColor(this, R.color.colorTransparent), ContextCompat.getColor(this, R.color.colorOverlay));
            if (homeStatus != HomeStatus.MAP) {
                overlayToolbar.setVisibility(View.VISIBLE);
                animationOverlayFade(false, overlayToolbar, ContextCompat.getColor(this, R.color.colorTransparent), ContextCompat.getColor(this, R.color.colorOverlay));
            }
        } else {
            searchModeOpened = false;
            if (listAutoCompleteOpen) {
                slideToolbarHeight(false);
            }
            closeKeyborad(etBusca);
            changeHambButton(false);
            animationOverlayFade(true, overlayAllContent, ContextCompat.getColor(this, R.color.colorOverlay), ContextCompat.getColor(this, R.color.colorTransparent));
            animationOverlayFade(true, overlayToolbar, ContextCompat.getColor(this, R.color.colorOverlay), ContextCompat.getColor(this, R.color.colorTransparent));
            tvToolbarTitle.setVisibility(View.VISIBLE);
            flBusca.setVisibility(View.GONE);
            btClearBusca.setVisibility(View.GONE);
            etBusca.setText("");
        }
    }
    public void animationOverlayFade(final boolean hiddeOnEnd, final View viewAnimate, int colorFrom, int colorTo) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(250); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                viewAnimate.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
        colorAnimation.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) { }
            @Override
            public void onAnimationEnd(Animator animation) {
                if (hiddeOnEnd) {
                    viewAnimate.setVisibility(View.GONE);
                }
            }
            @Override
            public void onAnimationCancel(Animator animation) {
                if (hiddeOnEnd) {
                    viewAnimate.setVisibility(View.GONE);
                }
            }
            @Override public void onAnimationRepeat(Animator animation) { }
        });
    }

    public boolean requestFocusAndForceKeyboardOpen(EditText et) {
        if (et != null) {
            et.requestFocus();
            // Força o teclado a abrir apos o request focus
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
            return true;
        }
        return false;
    }

    public boolean closeKeyborad(EditText et) {
        if (et != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
            return true;
        }
        return false;
    }

    public void search() {
        String textBusca = etBusca.getText().toString();
        if (!textBusca.isEmpty()) {
            Snackbar.make(clThis, "Action da busca", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(clThis, "Campo vazio!", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void toggleAnimationToolbarHeight(int heightStart, int heightTo) {

        int duration = 250;

        ValueAnimator anim = ValueAnimator.ofInt(heightStart, heightTo);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = llWraperToolbar.getLayoutParams();
                layoutParams.height = val;
                llWraperToolbar.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(duration);
        anim.start();
    }
    public void initializeOptionsToolbar() {
        LinearLayout llbuttonDogs = (LinearLayout) findViewById(R.id.ll_button_option_cachorros);
        llbuttonDogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Cachorros", Toast.LENGTH_SHORT).show();
                loadPlacesByCat(1);
            }
        });
        LinearLayout llbuttonCats = (LinearLayout) findViewById(R.id.ll_button_option_gatos);
        llbuttonCats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Gatos", Toast.LENGTH_SHORT).show();
                loadPlacesByCat(2);
            }
        });
        LinearLayout llbuttonBirds = (LinearLayout) findViewById(R.id.ll_button_option_passaros);
        llbuttonBirds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Passaros", Toast.LENGTH_SHORT).show();
                loadPlacesByCat(3);
            }
        });
        LinearLayout llbuttonFishes = (LinearLayout) findViewById(R.id.ll_button_option_peixes);
        llbuttonFishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Peixes", Toast.LENGTH_SHORT).show();
                loadPlacesByCat(4);
            }
        });
        LinearLayout llbuttonExotics = (LinearLayout) findViewById(R.id.ll_button_option_exoticos);
        llbuttonExotics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Exoticos", Toast.LENGTH_SHORT).show();
                loadPlacesByCat(5);
            }
        });
    }
    public void loadPlacesByCat(int catId) {
        if (llMainLayer != null) {
            showLoading();
            // carrega da web
            if (ConnectionUtils.checkConnection(this)) {
                Timer simulateLoadWeb = new Timer(true);
                simulateLoadWeb.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showListPlaces();
                            }
                        });
                    }
                }, 3000);
            } else {
                showSemConexao();
            }
        }
    }

    // inicializacao do swipe detect da main layer
    public void initializeSwipeDetect() {
        llMainLayer = (SwipeDetectLinearLayout) findViewById(R.id.ll_main_layer);
        initialPaddingTopMainLayer = llMainLayer.getPaddingTop();

        llMainLayer.registerToSwipeEvents(new SwipeDetectLinearLayout.onSwipeEventDetected() {
            @Override
            public void swipeEventDetected(boolean hideToolbarOptions, int diff) {
                if (homeStatus != HomeStatus.MAP) {
                    ViewGroup.LayoutParams lpFundo = llFundoToolbar.getLayoutParams();
                    ViewGroup.LayoutParams lpOverlay = overlayToolbar.getLayoutParams();
                    ViewGroup.LayoutParams lpWrappOptions = llWrapperOptions.getLayoutParams();
                    if (hideToolbarOptions) {
                        int toolbarHeight = (int) getResources().getDimension(R.dimen.toolbar_height_with_margins2);
                        if (lpFundo.height - diff >= toolbarHeight) {
                            int newPaddingTop = llMainLayer.getPaddingTop() - diff;
                            llMainLayer.setPadding(0, newPaddingTop, 0, 0);
                            initialPaddingTopMainLayer = newPaddingTop;
                            lpFundo.height -= diff;
                            llFundoToolbar.setLayoutParams(lpFundo);
                            lpOverlay.height -= diff;
//                        if (lpFundo.height < toolbarHeight + 1 && lpFundo.height > toolbarHeight - 1) {
//                            lpFundo.height = toolbarHeight;
//                        }
                            overlayToolbar.setLayoutParams(lpOverlay);
                            //mudar a posicao y relativa do box de opcoes
                            float positionOptionsBox = rlOptionsCat.getTranslationY();
                            rlOptionsCat.setTranslationY(positionOptionsBox - ((float) diff) / 2);
                            rlOptionsCat.setAlpha(rlOptionsCat.getAlpha() - ((float) diff) / 200);
                        }
                        if (lpFundo.height == toolbarHeight) {
                            llMainLayer.setToolbarOptionsOpened(false);
                            rlOptionsCat.setAlpha(0f);
                        }
                    } else {
                        int toolbarHeight = (int) getResources().getDimension(R.dimen.toolbar_height_with_margins);
                        if (lpFundo.height + diff <= toolbarHeight) {
                            int newPaddingTop = llMainLayer.getPaddingTop() + diff;
                            llMainLayer.setPadding(0, newPaddingTop, 0, 0);
                            initialPaddingTopMainLayer = newPaddingTop;
                            lpFundo.height += diff;
                            llFundoToolbar.setLayoutParams(lpFundo);
                            lpOverlay.height += diff;
//                        if (lpFundo.height < toolbarHeight + 1 && lpFundo.height > toolbarHeight - 1) {
//                            lpFundo.height = toolbarHeight;
//                        }
                            overlayToolbar.setLayoutParams(lpOverlay);
                            //mudar a posicao y relativa do box de opcoes
                            float positionOptionsBox = rlOptionsCat.getTranslationY();
                            rlOptionsCat.setTranslationY(positionOptionsBox + ((float) diff) / 2);
                            rlOptionsCat.setAlpha(rlOptionsCat.getAlpha() + ((float) diff) / 200);
                        }
                        if (lpFundo.height == toolbarHeight) {
                            llMainLayer.setToolbarOptionsOpened(true);
                            rlOptionsCat.setAlpha(1f);
                        }
                    }
                }
            }
        });
    }

    // metodos da home
    public void showHome() {
        if (llMainLayer != null) {
            llMainLayer.setToolbarOptionsOpened(true);
            srlHome = (SwipeRefreshLayout) findViewById(R.id.srl_home);
            srlHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshHome();
                }
            });
            initializeBannerHome();
        }
    }
    public void initializeBannerHome() {
        int[] imagesBanner = new int[]{R.drawable.hospedagem_pet_anjo_3, R.drawable.kimpton_blogpetsad, R.drawable.pets_fofos};
        Slider sliderBanner = new Slider(this, imagesBanner, 3);
    }
    public void refreshHome() {
        if (srlHome.isRefreshing()) {
            srlHome.setRefreshing(false);
        }
    }

    // mostrar layout de loading
    public void showLoading() {
        llMainLayer.removeAllViews();
        inflater.inflate(R.layout.loading_layout, llMainLayer);
    }

    // mostrar layout de sem conexao
    public void showSemConexao() {
        llMainLayer.removeAllViews();
        inflater.inflate(R.layout.sem_conexao_layout, llMainLayer);
    }

    // mostrar layout da listagem
    public void showListPlaces() {
        llMainLayer.removeAllViews();
        inflater.inflate(R.layout.list_places_layout, llMainLayer);
        // inicia layout escondido e transparente
        llListPlaces = (LinearLayout) findViewById(R.id.ll_list_places);
        int toolbarHeight = (int) getResources().getDimension(R.dimen.toolbar_height_with_margins2);
        int translateYTo = MetricsUtils.getMetrics(this).heightPixels - toolbarHeight;
        Log.i(TAG, "height list: " + translateYTo);
        llListPlaces.setTranslationY((float) translateYTo);
        llListPlaces.setAlpha(0);
        // animacao para translateY = 0
        ValueAnimator anim = ValueAnimator.ofInt(translateYTo, 0);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                llListPlaces.setTranslationY((float) val);
            }
        });
        anim.setDuration(500);
        anim.start();
        ValueAnimator animOpacity = ValueAnimator.ofFloat(0.0f, 1.0f);
        animOpacity.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float val = Float.parseFloat(valueAnimator.getAnimatedValue().toString());
                llListPlaces.setAlpha(val);
            }
        });
        animOpacity.setDuration(500);
        animOpacity.start();

        homeStatus = HomeStatus.LIST_PLACES;

        final SwipeRefreshLayout srlList = (SwipeRefreshLayout) findViewById(R.id.srl_list);
        srlList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //.... atualiza a lista
                srlList.setRefreshing(false);
            }
        });

        rlWrapperList = (RelativeLayout) findViewById(R.id.rl_btn_wrapper_mode);
        svListPlaces = (SwitchCompat) findViewById(R.id.switch_mode);
        tvMode = (TextView) findViewById(R.id.tv_mode);
        rlWrapperList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svListPlaces.setChecked(!svListPlaces.isChecked());
            }
        });
        svListPlaces.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleMapMode();
            }
        });
        initializeSwipeDetect();
    }

    // populate recycler view dos places
    public void populateListPlaces() {
    }

    public void toggleMapMode(){

        int heightSwitchWrapper = rlWrapperList.getMeasuredHeight();
//        int initialPaddingTop = (int) getResources().getDimension(R.dimen.toolbar_height_with_margins);
        int screenHeight = MetricsUtils.getMetrics(this).heightPixels;
        int paddingTopTo = screenHeight - (heightSwitchWrapper + getStatusBarHeight());

        if (svListPlaces.isChecked()) {
            homeStatus = HomeStatus.MAP;

            initializeSwipeDetect();
            getSupportFragmentManager().beginTransaction().show(mapFragment).commit();

            ValueAnimator animatorListOut = ValueAnimator.ofInt(initialPaddingTopMainLayer, paddingTopTo);
            animatorListOut.setDuration(500);
            animatorListOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    llMainLayer.setPadding(0, Integer.parseInt(valueAnimator.getAnimatedValue().toString()), 0, 0);
                }
            });
            animatorListOut.start();

            ObjectAnimator animatorFundoToolbarOut = ObjectAnimator.ofFloat(llFundoToolbar, "translationY", llFundoToolbar.getTranslationY(), -llFundoToolbar.getMeasuredHeight());
            animatorFundoToolbarOut.setDuration(500);
            animatorFundoToolbarOut.start();
        } else {
            homeStatus = HomeStatus.LIST_PLACES;

            ValueAnimator animatorListIn = ValueAnimator.ofInt(llMainLayer.getPaddingTop(), initialPaddingTopMainLayer);
            animatorListIn.setDuration(500);
            animatorListIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    llMainLayer.setPadding(0, Integer.parseInt(valueAnimator.getAnimatedValue().toString()), 0, 0);
                }
            });
            animatorListIn.start();

            ObjectAnimator animatorFundoToolbarIn = ObjectAnimator.ofFloat(llFundoToolbar, "translationY", llFundoToolbar.getTranslationY(), 0);
            animatorFundoToolbarIn.setDuration(500);
            animatorFundoToolbarIn.start();
            animatorFundoToolbarIn.addListener(new Animator.AnimatorListener() {
                @Override public void onAnimationStart(Animator animation) { }
                @Override
                public void onAnimationEnd(Animator animation) {
                    getSupportFragmentManager().beginTransaction().hide(mapFragment).commit();
                }
                @Override public void onAnimationCancel(Animator animation) { }
                @Override  public void onAnimationRepeat(Animator animation) { }
            });

        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (searchModeOpened) {
                Log.i(TAG, "searchmode on");
                toggleSearchMode(false);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_filter) {
//            Snackbar.make(clThis, "Abrir FilterDialog", Snackbar.LENGTH_SHORT).show();
            FilterDialog.openNew(null, this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    // Metodos do mapa, e jobscheduler
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapReady");

        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        double latitude = Double.parseDouble(getInSharedPreferences(this, LATITUDE_KEY, "-29.8797056"));
        double longitude = Double.parseDouble(getInSharedPreferences(this, LONGITUDE_KEY, "-51.1652569"));
        LatLng latLng = new LatLng(latitude, longitude);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(13).tilt(0).build();
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.moveCamera(update);

        customAddMarker(latLng, "Marcador 1", "O Marcador 1 foi reposicionado");
//        drawMarkerWithCircle(latLng, 5500);
    }

    public void customAddMarker(LatLng latLng, String title, String snippet) {
        MarkerOptions options = new MarkerOptions();
        options.position(latLng).title(title).snippet(snippet).draggable(false);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation));

        markerUser = map.addMarker(options);
    }
    private void drawMarkerWithCircle(LatLng position, double radiusInMetersChoice){

        double radiusInMeters;
        if (radiusInMetersChoice == 0.0) {
            radiusInMeters = DEFAULT_RADIUS_IN_METERS;
        } else {
            radiusInMeters = radiusInMetersChoice;
        }

        int strokeColor = Color.BLACK; //red outline
        int shadeColor = Color.argb(150, 0,0,0); //opaque red fill

        CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
        mCircle = map.addCircle(circleOptions);
    }
    private void updatePosition(LatLng latLng) {
//        Toast.makeText(this, "location update", Toast.LENGTH_SHORT).show();
        markerUser.setPosition(latLng);
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    //Event Bus
    public void onEvent(MessageEB m) {
        if (m.getClassName().equalsIgnoreCase(MainActivity.class.getName())) {
            switch (m.getAction()){
                case "update_position":
                    Log.i("PrimePets", "location update ****");
                    LatLng latLng = new LatLng(m.getLocation().getLatitude(), m.getLocation().getLongitude());
                    updatePosition(latLng);
                    break;
                case "get_activty_instance":
                    MessageEB messageEB = new MessageEB();
                    messageEB.setObject(this);
                    messageEB.setClassName(JobSchedulerService.class.getName());
                    EventBus.getDefault().post(messageEB);
                    break;
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTracking();
    }

    // Tracking
    public void startTracking() {
        ComponentName cp = new ComponentName(this, JobSchedulerService.class);

        JobInfo jb = new JobInfo.Builder(1, cp)
                .setBackoffCriteria(2000, JobInfo.BACKOFF_POLICY_LINEAR)
                .setPersisted(true)
                .setPeriodic(1000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build();

        JobScheduler js = JobScheduler.getInstance(this);
        js.schedule(jb);

    }

    public void stopTracking() {
        JobScheduler js = JobScheduler.getInstance(this);
        js.cancelAll();
    }
}
