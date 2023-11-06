package com.example.storageanalyser.Fragments;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storageanalyser.FileAdapter;
import com.example.storageanalyser.OnFileSelectedListener;
import com.example.storageanalyser.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.List;

public class StorageFragment extends Fragment implements OnFileSelectedListener {
    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private List<File> fileList;
    File storage;
    View view;
    String data;
    String[] items={"Details","Restore","Delete Permanantly"};
    File path;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.picch,container,false);
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        runtimePermission();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                //todo when permission is granted
            } else {
                //request for the permission
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
        return view;
    }
    private void runtimePermission() {
        Dexter.withContext(getContext()).withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                OnStorageDisplay();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();

            }
        }).check();

    }

    private void OnStorageDisplay() {
        // Get the external storage directory
        //File externalStorageDirectory = getExternalFilesDir(null);

        // Get the total storage size
        View myView=getLayoutInflater().inflate(R.layout.fragment_storage,null);
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        long totalBytes = (long) statFs.getBlockCountLong() * (long) statFs.getBlockSizeLong();

        // Get the available storage size
        long availableBytes = (long) statFs.getAvailableBlocksLong() * (long) statFs.getBlockSizeLong();

        // Calculate the used storage size
        long usedBytes = totalBytes - availableBytes;

        // Convert the storage sizes to human-readable format
        String totalStorage = Formatter.formatFileSize(getContext(), totalBytes);
        //Log.d("StorageAnalyser","Total Storage "+totalStorage);
        String usedStorage = Formatter.formatFileSize(getContext(), usedBytes);

        // Display the storage sizes in the UI
        TextView totalStorageTextView = myView.findViewById(R.id.total_storage_text_view);
        TextView usedStorageTextView = myView.findViewById(R.id.used_storage_text_view);
        totalStorageTextView.setText("Total Storage: " + totalStorage);
        usedStorageTextView.setText("Used Storage: " + usedStorage);
    }


    @Override
    public void onFileClicked(File file) {

    }

    @Override
    public void onFileLongClicked(File file, int position) {

    }
}
