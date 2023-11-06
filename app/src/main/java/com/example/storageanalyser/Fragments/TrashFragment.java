package com.example.storageanalyser.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storageanalyser.FileAdapter;
import com.example.storageanalyser.FileOpener;
import com.example.storageanalyser.OnFileSelectedListener;
import com.example.storageanalyser.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class TrashFragment extends Fragment implements OnFileSelectedListener {
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
        view=inflater.inflate(R.layout.fragment_trash,container,false);
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
                displayFiles();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();

            }
        }).check();

    }
    public ArrayList<File> findFiles(File file){
        ArrayList<File> arrayList=new ArrayList<>();
        File[] files=file.listFiles();

        for(File singleFile:files){
            if(singleFile.getName().toLowerCase().startsWith(".trashed")){
                if(singleFile.isDirectory()){
                    arrayList.addAll(findFiles(singleFile));
                }
                else if (singleFile.getName().toLowerCase().endsWith(".jpeg") || singleFile.getName().toLowerCase().endsWith(".jpg") ||
                            singleFile.getName().toLowerCase().endsWith(".png") || singleFile.getName().toLowerCase().endsWith(".mp3") ||
                            singleFile.getName().toLowerCase().endsWith(".wav") || singleFile.getName().toLowerCase().endsWith(".mp4") ||
                            singleFile.getName().toLowerCase().endsWith(".ogg") || singleFile.getName().toLowerCase().endsWith(".mkv") ||
                            singleFile.getName().toLowerCase().endsWith(".pdf") || singleFile.getName().toLowerCase().endsWith(".doc") ||
                            singleFile.getName().toLowerCase().endsWith(".xls") || singleFile.getName().toLowerCase().endsWith(".docx") ||
                            singleFile.getName().toLowerCase().endsWith(".xlsx") || singleFile.getName().toLowerCase().endsWith(".apk") ||
                            singleFile.getName().toLowerCase().endsWith(".ppt") || singleFile.getName().toLowerCase().endsWith(".pptx") ){
                        arrayList.add(singleFile);
                    }

            }
        }
        arrayList.sort(Comparator.comparing(File::lastModified).reversed());
        return arrayList;
    }
    private void displayFiles() {
        recyclerView = view.findViewById(R.id.recycler_internal);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        fileList=new ArrayList<>();
        fileList.addAll(findFiles(path));
        fileAdapter=new FileAdapter(getContext(),fileList,this);
        recyclerView.setAdapter(fileAdapter);

    }

    @Override
    public void onFileClicked(File file) {

    }

    @Override
    public void onFileLongClicked(File file, int position) {
        final Dialog optionDialog=new Dialog(getContext());
        optionDialog.setContentView(R.layout.option_dialog);
        optionDialog.setTitle("Select Options.");
        ListView options=(ListView) optionDialog.findViewById(R.id.List);
        TrashFragment.CustomAdapter customAdapter=new TrashFragment.CustomAdapter();
        options.setAdapter(customAdapter);
        optionDialog.show();

        options.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem=adapterView.getItemAtPosition(i).toString();
                switch (selectedItem){
                    case "Details":
                        AlertDialog.Builder detailDialog=new AlertDialog.Builder(getContext());
                        detailDialog.setTitle("Details");
                        final TextView details=new TextView(getContext());
                        detailDialog.setView(details);
                        Date lastModified=new Date(file.lastModified());
                        SimpleDateFormat formatter=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        String formattedDate=formatter.format(lastModified);

                        details.setText("\tFile Name: "+file.getName().substring(20)+"\n"+
                                "\tSize: "+ Formatter.formatShortFileSize(getContext(),file.length())+"\n" +
                                "\tRestore Path: "+file.getAbsolutePath()+"\n"+
                                "\tLast Modified: "+formattedDate);

                        detailDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                optionDialog.cancel();
                            }
                        });
                        AlertDialog alertdialog_details=detailDialog.create();
                        alertdialog_details.show();
                        break;

                    case "Restore":
                        AlertDialog.Builder renameDialog=new AlertDialog.Builder(getContext());

                        String File_name=file.getName().substring(20);
                        renameDialog.setTitle("Restore file: "+file.getName().substring(20)+"?");
                        //final EditText name=new EditText(getContext());
                        //renameDialog.setView(name);
                        renameDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String new_name=File_name;
                                String extention= file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                                File current=new File(file.getAbsolutePath());
                                File destination=new File(file.getAbsolutePath().replace(file.getName(),new_name));
                                if(current.renameTo(destination)){
                                    fileList.set(position,destination);
                                    fileAdapter.notifyItemChanged(position);
                                    Toast.makeText(getContext(), "Restored Successfully", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(getContext(), "Couldn't Restore!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        renameDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                optionDialog.cancel();
                            }
                        });
                        AlertDialog alertdialog_rename=renameDialog.create();
                        alertdialog_rename.show();
                        break;

                    case "Delete Permanantly":
                        AlertDialog.Builder deleteDialog=new AlertDialog.Builder(getContext());
                        deleteDialog.setTitle("Delete"+file.getName().substring(20)+"?\n(Caution: It cannot be restored)");
                        deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                file.delete();
                                fileList.remove(position);
                                fileAdapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "File has been deleted Permanantly", Toast.LENGTH_SHORT).show();
                            }
                        });
                        deleteDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                optionDialog.cancel();
                            }
                        });
                        AlertDialog alertdialog_delete=deleteDialog.create();
                        alertdialog_delete.show();
                        break;
                }
            }
        });
    }
    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return items[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myView=getLayoutInflater().inflate(R.layout.option_layout,null);
            TextView txtOptions=myView.findViewById(R.id.txtOption);
            ImageView imgOptions=myView.findViewById(R.id.imgOption);
            txtOptions.setText(items[i]);
            if(items[i].equals("Details")){
                imgOptions.setImageResource(R.drawable.ic_details);
            } else if (items[i].equals("Restore")){
                imgOptions.setImageResource(R.drawable.ic_restore);
            }else if (items[i].equals("Open")){
                imgOptions.setImageResource(R.drawable.ic_open);
            }else if (items[i].equals("Delete Permanantly")){
                imgOptions.setImageResource(R.drawable.ic_delete);
            }else if (items[i].equals("Share")){
                imgOptions.setImageResource(R.drawable.ic_share);
            }else if (items[i].equals("Move To")){
                imgOptions.setImageResource(R.drawable.ic_moveto);
            }else if (items[i].equals("Copy To")){
                imgOptions.setImageResource(R.drawable.ic_copyto);
            }
            return myView;
        }
        }
    }