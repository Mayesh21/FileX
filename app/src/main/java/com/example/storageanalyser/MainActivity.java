package com.example.storageanalyser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.storageanalyser.Fragments.AppFragment;
import com.example.storageanalyser.Fragments.CardFragment;
import com.example.storageanalyser.Fragments.CategorizedFragment;
import com.example.storageanalyser.Fragments.HomeFragment;
import com.example.storageanalyser.Fragments.InternalFragment;
import com.example.storageanalyser.Fragments.TrashFragment;
import com.example.storageanalyser.Fragments.StorageFragment;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView; //mriganki
    private AppAdapter appAdapter; //mriganki
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.Open_Drawer, R.string.Close_Drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_home);


        //mrigankistart

//        recyclerView = findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        List<AppInfo> appList = getInstalledApps();
//        appAdapter = new AppAdapter(appList);
//        recyclerView.setAdapter(appAdapter);

        //mrigankiend
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                HomeFragment homeFragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).addToBackStack(null).commit();
                break;
            case R.id.nav_internal:
                InternalFragment internalFragment = new InternalFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, internalFragment).addToBackStack(null).commit();
                break;
            case R.id.nav_card:
                CardFragment cardFragment = new CardFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, cardFragment).addToBackStack(null).commit();
                break;
            case R.id.nav_trash:
                TrashFragment trashFragment = new TrashFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, trashFragment).addToBackStack(null).commit();
                break;
            case R.id.nav_apps:
                AppFragment AppFragment = new AppFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, AppFragment).addToBackStack(null).commit();
                break;
            case R.id.nav_storage:
                StorageFragment storageFragment = new StorageFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, storageFragment).addToBackStack(null).commit();
                break;
            case R.id.nav_about:
                Toast.makeText(this, "By Pratik Shinde", Toast.LENGTH_SHORT).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        getSupportFragmentManager().popBackStackImmediate();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
        }
    }

    //mrigankistart

    private List<AppInfo> getInstalledApps() {
        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> applicationInfos = packageManager.getInstalledApplications(0);
        List<AppInfo> appList = new ArrayList<>();

        for (ApplicationInfo applicationInfo : applicationInfos) {
            String name = packageManager.getApplicationLabel(applicationInfo).toString();
            Drawable icon = packageManager.getApplicationIcon(applicationInfo);
            String packageName = applicationInfo.packageName;
            appList.add(new AppInfo(name, icon, packageName));
        }
        return appList;
    }


    public void OnStorageDisplay() {
        // Get the external storage directory
        File externalStorageDirectory = getExternalFilesDir(null);

        // Get the total storage size
        StatFs statFs = new StatFs(externalStorageDirectory.getAbsolutePath());
        long totalBytes = (long) statFs.getBlockCount() * (long) statFs.getBlockSize();

        // Get the available storage size
        long availableBytes = (long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize();

        // Calculate the used storage size
        long usedBytes = totalBytes - availableBytes;

        // Convert the storage sizes to human-readable format
        String totalStorage = Formatter.formatFileSize(this, totalBytes);
        String usedStorage = Formatter.formatFileSize(this, usedBytes);

        // Display the storage sizes in the UI
        TextView totalStorageTextView = findViewById(R.id.total_storage_text_view);
        TextView usedStorageTextView = findViewById(R.id.used_storage_text_view);
        totalStorageTextView.setText("Total Storage: " + totalStorage);
        usedStorageTextView.setText("Used Storage: " + usedStorage);
    }

//mrigankiend
}
