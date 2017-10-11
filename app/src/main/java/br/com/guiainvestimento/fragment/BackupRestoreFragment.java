package br.com.guiainvestimento.fragment;


import android.Manifest;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.DbHelper;
import br.com.guiainvestimento.util.Util;
import br.com.guiainvestimento.utils.FileUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.DOWNLOAD_SERVICE;

public class BackupRestoreFragment extends BaseFragment{
    private static final String LOG_TAG = BackupRestoreFragment.class.getSimpleName();
    private DbHelper mDBHelper;

    @BindView(R.id.download_backup_db)
    LinearLayout downloadBackupDB;

    @BindView(R.id.sdcard_backup_db)
    LinearLayout sdcardBackupDB;

    @BindView(R.id.google_drive_backup_db)
    LinearLayout googleDriveBackupDB;

    @BindView(R.id.file_restore_db)
    LinearLayout fileRestoreDB;

    @BindView(R.id.google_drive_restore_db)
    LinearLayout googleDriveRestoreDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_backup_restore, container, false);
        ButterKnife.bind(this, view);

        // Gets SD Card write permission if needed
        mDBHelper = new DbHelper(getActivity().getBaseContext());
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }

        // Set all click listeners
        downloadBackupDB.setOnClickListener(downloadBackupOnClick());
        sdcardBackupDB.setOnClickListener(sdcardBackupOnClick());
        googleDriveBackupDB.setOnClickListener(googleDriveBackupOnClick());
        fileRestoreDB.setOnClickListener(fileRestoreOnClick());
        googleDriveRestoreDB.setOnClickListener(googleDriveRestoreOnClick());

        getActivity().findViewById(R.id.fab).setVisibility(View.INVISIBLE);
        return view;
    }

    private View.OnClickListener downloadBackupOnClick() {
        View.OnClickListener onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String downloadedPath = exportDBtoDowloads(mContext);
                    if(downloadedPath != "") {
                        Toast.makeText(mContext, mContext.getString(R.string.backup_download_write_success_toast, downloadedPath), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.backup_download_write_error_toast), Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e){
                    Log.e(LOG_TAG, e.toString());
                }
            }
        };
        return onclick;
    }

    private View.OnClickListener sdcardBackupOnClick() {
        View.OnClickListener onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String downloadedPath = exportDBtoSD(mContext);
                    if (downloadedPath != ""){
                        Toast.makeText(mContext, mContext.getString(R.string.backup_sdcard_write_success_toast, downloadedPath), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.backup_sdcard_write_error_toast), Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e){
                    Log.e(LOG_TAG, e.toString());
                }
            }
        };
        return onclick;
    }

    private View.OnClickListener googleDriveBackupOnClick() {
        View.OnClickListener onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
        return onclick;
    }

    private View.OnClickListener fileRestoreOnClick() {
        View.OnClickListener onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    importDB(mContext);
                } catch (IOException e){
                    Log.e(LOG_TAG, e.toString());
                }
            }
        };
        return onclick;
    }

    private View.OnClickListener googleDriveRestoreOnClick() {
        View.OnClickListener onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        };
        return onclick;
    }


    private String exportDBtoDowloads(Context context) throws IOException {
        File dir = new File("//sdcard//Download//");

        String NAME = "Portfolio.db";
        String PACKAGE_NAME = context.getApplicationContext().getPackageName();
        String DB_FILEPATH = "/data/data/" + PACKAGE_NAME + "/databases/" + NAME;
        String backupName = "GIBackup.db";

        if (dir.canWrite()) {
            File currentDB = new File(DB_FILEPATH);
            File backupDB = new File(dir, backupName);
            backupDB.setReadable(true);
            backupDB.setWritable(true);
            backupDB.setExecutable(true);
            backupDB.createNewFile();
            FileUtils.copyFile(new FileInputStream(currentDB), new FileOutputStream(backupDB));

            return backupDB.getAbsolutePath();
        } else{
            return "";
        }
    }

    private String exportDBtoSD(Context context) throws IOException{
        File sdCardDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        String NAME = "Portfolio.db";
        String PACKAGE_NAME = context.getApplicationContext().getPackageName();
        String DB_FILEPATH = "/data/data/" + PACKAGE_NAME + "/databases/" + NAME;

        if (sdCardDir.canWrite()) {
            File currentDB = new File(DB_FILEPATH);
            File backupDB = new File(sdCardDir, "GIBackup.db");
            backupDB.setReadable(true);
            backupDB.setWritable(true);
            backupDB.setExecutable(true);
            backupDB.createNewFile();
            FileUtils.copyFile(new FileInputStream(currentDB), new FileOutputStream(backupDB));

            // Adds to Download Manager

            return backupDB.getAbsolutePath();
        } else {
            return "";
        }
    }

    private boolean importDB(Context context) throws IOException{
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile = Intent.createChooser(chooseFile, "Choose the file");
        startActivityForResult(chooseFile, Constants.Intent.IMPORT_DB);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String NAME = "Portfolio.db";
        String PACKAGE_NAME = getContext().getApplicationContext().getPackageName();
        String DB_FILEPATH = "/data/data/" + PACKAGE_NAME + "/databases/" + NAME;

        if (requestCode == Constants.Intent.IMPORT_DB) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                File currentDB = new File(DB_FILEPATH);
                try {
                    Uri backupUri = data.getData();
                    String fileExtension = getFileExtension(backupUri);
                    if(fileExtension.equalsIgnoreCase("db")) {
                        FileInputStream backupInputStream = (FileInputStream) getContext().getContentResolver().openInputStream(backupUri);
                        FileUtils.copyFile(backupInputStream, new FileOutputStream(currentDB));

                        // Restart application
                        Intent applicationIntent = getContext().getApplicationContext().getPackageManager()
                                .getLaunchIntentForPackage(PACKAGE_NAME);
                        applicationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getActivity().finish();
                        startActivity(applicationIntent);
                    } else {
                        // File extension is wrong, not a valid file to import
                        Toast.makeText(getContext(), getString(R.string.restore_wrong_extension), Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e){
                    Toast.makeText(getContext(), getString(R.string.backup_download_write_error_toast), Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, e.toString());
                }
            }
        }
    }

    public String getFileExtension(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
                if (result != null && result != ""){
                    int cut2 = result.lastIndexOf('.');
                    if (cut2 != -1){
                        result = result.substring(cut2 + 1);
                    }
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
            // Turns into extension
            if (result != null && result != ""){
                int cut2 = result.lastIndexOf('.');
                if (cut2 != -1){
                    result = result.substring(cut2 + 1);
                }
            }
        }
        return result;
    }
}
