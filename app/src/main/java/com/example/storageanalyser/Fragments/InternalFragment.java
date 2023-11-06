package com.example.storageanalyser.Fragments;

import static android.content.ContentValues.TAG;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;



public class InternalFragment extends Fragment implements OnFileSelectedListener {


    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private List<File> fileList;
    private ImageView img_back,img_sort;
    private TextView tv_pathHolder;
    File storage;
    View view;
    String data;
    String[] items={"Open","Details","Rename","Delete Permanantly","Share","Move To","Copy To"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_internal,container,false);

        tv_pathHolder=view.findViewById(R.id.tv_pathHolder);
        img_back=view.findViewById(R.id.img_back);
        img_sort=view.findViewById(R.id.img_sort);

        Spinner dropdown = view.findViewById(R.id.spinner1);
        CustomAdapter adapter1=new CustomAdapter();
        dropdown.setAdapter(adapter1);
        dropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String SItem=adapter1.getItem(i).toString();
                switch (SItem){
                    case "Name":
                        //arrayList.sort(Comparator.comparing(File::getName));
                        break;
                    case "Size":
                        //arrayList.sort(Comparator.comparing(File::length));
                        break;
                    case "Date":
//                        arrayList.sort(Comparator.comparing(File::lastModified));
                        break;
                }
            }
        });
        String [] items = new String[]{"","Name","Size","Date"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_dropdown_item,items);
        dropdown.setAdapter(adapter);

        img_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



        String internalStorage=System.getenv("EXTERNAL_STORAGE");
        storage=new File(internalStorage);


        try{
            data=getArguments().getString("path");
            File file=new File(data);
            storage=file;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        tv_pathHolder.setText(storage.getAbsolutePath());
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
//        Spinner spinnerLanguages=view.findViewById(R.id.spinner1);
//        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(getContext(), R.array.languages, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(R.layout.option_layout);
//        spinnerLanguages.setAdapter(adapter);

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
            if(singleFile.isDirectory() && !singleFile.isHidden()){
                arrayList.add(singleFile);
            }
        }
        for(File singleFile:files) {
            if (singleFile.getName().toLowerCase().endsWith(".jpeg") || singleFile.getName().toLowerCase().endsWith(".jpg") ||
                    singleFile.getName().toLowerCase().endsWith(".png") || singleFile.getName().toLowerCase().endsWith(".mp3") ||
                    singleFile.getName().toLowerCase().endsWith(".wav") || singleFile.getName().toLowerCase().endsWith(".mp4") ||
                    singleFile.getName().toLowerCase().endsWith(".ogg") || singleFile.getName().toLowerCase().endsWith(".mkv") ||
                    singleFile.getName().toLowerCase().endsWith(".pdf") || singleFile.getName().toLowerCase().endsWith(".doc") ||
                    singleFile.getName().toLowerCase().endsWith(".xls") || singleFile.getName().toLowerCase().endsWith(".docx") ||
                    singleFile.getName().toLowerCase().endsWith(".xlsx") || singleFile.getName().toLowerCase().endsWith(".apk") ||
                    singleFile.getName().toLowerCase().endsWith(".ppt") || singleFile.getName().toLowerCase().endsWith(".pptx")) {
                arrayList.add(singleFile);
            }
        }
        arrayList.sort(Comparator.comparing(File::getName));
        return arrayList;
    }

    private void displayFiles() {
        recyclerView = view.findViewById(R.id.recycler_internal);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        fileList=new ArrayList<>();
        fileList.addAll(findFiles(storage));
        fileAdapter=new FileAdapter(getContext(),fileList,this);
        recyclerView.setAdapter(fileAdapter);

    }

    @Override
    public void onFileClicked(File file) {
        if(file.isDirectory()){
            Bundle bundle=new Bundle();
            bundle.putString("path",file.getAbsolutePath());
            InternalFragment internalFragment=new InternalFragment();
            internalFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container,internalFragment).addToBackStack(null).commit();
        }
        else{
            try {
                FileOpener.openFile(getContext(),file);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onFileLongClicked(File file,int position) {
        final Dialog optionDialog=new Dialog(getContext());
        optionDialog.setContentView(R.layout.option_dialog);
        optionDialog.setTitle("Select Options.");
        ListView options=(ListView) optionDialog.findViewById(R.id.List);
        CustomAdapter customAdapter=new CustomAdapter();
        options.setAdapter(customAdapter);
        optionDialog.show();

        options.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem=adapterView.getItemAtPosition(i).toString();
                switch (selectedItem){
                    case "Open":
                        if(file.isDirectory()){
                            Bundle bundle=new Bundle();
                            bundle.putString("path",file.getAbsolutePath());
                            CardFragment internalFragment=new CardFragment();
                            internalFragment.setArguments(bundle);
                            getFragmentManager().beginTransaction().replace(R.id.fragment_container,internalFragment).addToBackStack(null).commit();
                        }
                        else{
                            try {
                                FileOpener.openFile(getContext(),file);
                            } catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                        break;
                    case "Details":
                        AlertDialog.Builder detailDialog=new AlertDialog.Builder(getContext());
                        detailDialog.setTitle("Details");
                        final TextView details=new TextView(getContext());
                        detailDialog.setView(details);
                        Date lastModified=new Date(file.lastModified());
                        SimpleDateFormat formatter=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        String formattedDate=formatter.format(lastModified);

                        details.setText("\tFile Name: "+file.getName()+"\n"+
                                "\tSize: "+ Formatter.formatShortFileSize(getContext(),file.length())+"\n" +
                                "\tPath: "+file.getAbsolutePath()+"\n"+
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

                    case "Rename":
                        AlertDialog.Builder renameDialog=new AlertDialog.Builder(getContext());
                        renameDialog.setTitle("Rename file: "+file.getName()+"?");
                        final EditText name=new EditText(getContext());
                        renameDialog.setView(name);
                        renameDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String new_name=name.getEditableText().toString();
                                String extention= file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                                File current=new File(file.getAbsolutePath());
                                File destination=new File(file.getAbsolutePath().replace(file.getName(),new_name)+extention);
                                if(current.renameTo(destination)){
                                    fileList.set(position,destination);
                                    fileAdapter.notifyItemChanged(position);
                                    Toast.makeText(getContext(), "Renamed Successfully", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(getContext(), "Couldn't Rename!!", Toast.LENGTH_SHORT).show();
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
                        deleteDialog.setTitle("Delete"+file.getName()+"?\n(Caution: It cannot be restored)");
                        deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                file.delete();
                                fileList.remove(position);
                                fileAdapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "File has been deleted", Toast.LENGTH_SHORT).show();
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

                    case "Share":
                        String fileName=file.getName();
                        Intent share=new Intent();
                        share.setAction(Intent.ACTION_SEND);
                        share.setType("image/jpeg");
                        share.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file));
                        startActivity(Intent.createChooser(share,"Share "+fileName));
                        break;
                    case "Move To":
                        // the file to be moved or copied
                        File sourceLocation = new File (file.getAbsolutePath());

                        // make sure your target location folder exists!
                        File targetLocation = new File (storage + "/MyNewFolder/"+file.getName());
                        try {

                            // 1 = move the file, 2 = copy the file
                            int actionChoice = 1;

                            // moving the file to another directory
                            if(actionChoice==1){

                                if(sourceLocation.renameTo(targetLocation)){
                                    Log.v(TAG, "Move file successful.");
                                }else{
                                    Log.v(TAG, "Move file failed.");
                                }

                            }

                            // we will copy the file
                            else{

                                // make sure the target file exists

                                if(sourceLocation.exists()){

                                    InputStream in = new FileInputStream(sourceLocation);
                                    OutputStream out = new FileOutputStream(targetLocation);

                                    // Copy the bits from instream to outstream
                                    byte[] buf = new byte[1024];
                                    int len;

                                    while ((len = in.read(buf)) > 0) {
                                        out.write(buf, 0, len);
                                    }

                                    in.close();
                                    out.close();

                                    Log.v(TAG, "Copy file successful.");

                                }else{
                                    Log.v(TAG, "Copy file failed. Source file missing.");
                                }

                            }

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case "Copy To":
                        // the file to be moved or copied
                        sourceLocation = new File (file.getAbsolutePath());

                        // make sure your target location folder exists!
                        targetLocation = new File (storage + "/MyNewFolder/"+file.getName());
                        try {

                            // 1 = move the file, 2 = copy the file
                            int actionChoice = 2;

                            // moving the file to another directory
                            if(actionChoice==1){

                                if(sourceLocation.renameTo(targetLocation)){
                                    Log.v(TAG, "Move file successful.");
                                }else{
                                    Log.v(TAG, "Move file failed.");
                                }

                            }

                            // we will copy the file
                            else{

                                // make sure the target file exists

                                if(sourceLocation.exists()){

                                    InputStream in = new FileInputStream(sourceLocation);
                                    OutputStream out = new FileOutputStream(targetLocation);

                                    // Copy the bits from instream to outstream
                                    byte[] buf = new byte[1024];
                                    int len;

                                    while ((len = in.read(buf)) > 0) {
                                        out.write(buf, 0, len);
                                    }

                                    in.close();
                                    out.close();

                                    Log.v(TAG, "Copy file successful.");

                                }else{
                                    Log.v(TAG, "Copy file failed. Source file missing.");
                                }

                            }

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;


//                        File source = new File(file.getAbsolutePath());
//                        File destination = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toURI());
//
//                        try {
//                            FileInputStream inputStream = new FileInputStream(source);
//                            FileOutputStream outputStream = new FileOutputStream(new File(destination, source.getName()));
//
//                            byte[] buffer = new byte[1024];
//                            int length;
//                            while ((length = inputStream.read(buffer)) > 0) {
//                                outputStream.write(buffer, 0, length);
//                            }
//
//                            inputStream.close();
//                            outputStream.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                }
            }
        });
    }

    class CustomAdapter extends BaseAdapter{

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
            } else if (items[i].equals("Rename")){
                imgOptions.setImageResource(R.drawable.ic_rename);
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