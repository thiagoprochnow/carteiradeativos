package br.com.guiainvestimento.fragment;


import android.Manifest;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
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

    @BindView(R.id.download_folder_restore_db)
    LinearLayout downloadFolderRestoreDB;

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
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }

        // Set all click listeners
        downloadBackupDB.setOnClickListener(downloadBackupOnClick());
        sdcardBackupDB.setOnClickListener(sdcardBackupOnClick());
        googleDriveBackupDB.setOnClickListener(googleDriveBackupOnClick());
        downloadFolderRestoreDB.setOnClickListener(downloadFolderRestoreOnClick());
        fileRestoreDB.setOnClickListener(fileRestoreOnClick());
        googleDriveRestoreDB.setOnClickListener(googleDriveRestoreOnClick());

        getActivity().findViewById(R.id.fab).setVisibility(View.INVISIBLE);
        return view;
    }

    private View.OnClickListener downloadBackupOnClick() {
        View.OnClickListener onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File dir = new File("//sdcard//Download//");
                String backupName = "GIBackup.db";
                File backupFile = new File(dir, backupName);
                if (backupFile.exists()){
                    new AlertDialog.Builder(getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(getContext().getString(R.string.backup_overwrite_title))
                            .setMessage(getContext().getString(R.string.backup_overwrite))
                            .setPositiveButton(getContext().getString(R.string.yes), new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        String downloadedPath = exportDBtoDowloads(mContext);
                                        if (downloadedPath != "") {
                                            Toast.makeText(mContext, mContext.getString(R.string.backup_download_write_success_toast, downloadedPath), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(mContext, mContext.getString(R.string.backup_download_write_error_toast), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (IOException e) {
                                        Log.e(LOG_TAG, e.toString());
                                    }
                                }
                            })
                            .setNegativeButton(getContext().getString(R.string.no), new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                } else {
                    try {
                        String downloadedPath = exportDBtoDowloads(mContext);
                        if (downloadedPath != "") {
                            Toast.makeText(mContext, mContext.getString(R.string.backup_download_write_success_toast, downloadedPath), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, mContext.getString(R.string.backup_download_write_error_toast), Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Log.e(LOG_TAG, e.toString());
                    }
                }
            }
        };
        return onclick;
    }

    private View.OnClickListener sdcardBackupOnClick() {
        View.OnClickListener onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File sdCardDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                String backupName = "GIBackup.db";
                File backupFile = new File(sdCardDir, backupName);
                if (backupFile.exists()){
                    new AlertDialog.Builder(getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(getContext().getString(R.string.backup_overwrite_title))
                            .setMessage(getContext().getString(R.string.backup_overwrite))
                            .setPositiveButton(getContext().getString(R.string.yes), new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        String downloadedPath = exportDBtoSD(mContext);
                                        if (downloadedPath != "") {
                                            Toast.makeText(mContext, mContext.getString(R.string.backup_sdcard_write_success_toast, downloadedPath), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(mContext, mContext.getString(R.string.backup_sdcard_write_error_toast), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (IOException e) {
                                        Log.e(LOG_TAG, e.toString());
                                    }
                                }
                            })
                            .setNegativeButton(getContext().getString(R.string.no), new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                } else {
                    try {
                        String downloadedPath = exportDBtoSD(mContext);
                        if (downloadedPath != "") {
                            Toast.makeText(mContext, mContext.getString(R.string.backup_sdcard_write_success_toast, downloadedPath), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, mContext.getString(R.string.backup_sdcard_write_error_toast), Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Log.e(LOG_TAG, e.toString());
                    }
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

    private View.OnClickListener downloadFolderRestoreOnClick() {
        View.OnClickListener onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getContext().getString(R.string.restore_confirm_title))
                        .setMessage(getContext().getString(R.string.restore_confirm))
                        .setPositiveButton(getContext().getString(R.string.yes), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try{
                                    importDBfromFolder(mContext);
                                } catch (IOException e){
                                    Log.e(LOG_TAG, e.toString());
                                }
                            }
                        })
                        .setNegativeButton(getContext().getString(R.string.no), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
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


    private boolean exportDBtoDrive(Context context) throws IOException {

        String NAME = "Portfolio.db";
        String PACKAGE_NAME = context.getApplicationContext().getPackageName();
        String DB_FILEPATH = "/data/data/" + PACKAGE_NAME + "/databases/" + NAME;
        String backupName = "GIBackup.db";

        File currentDB = new File(DB_FILEPATH);
        File backupDB = new File(backupName);
        backupDB.setReadable(true);
        backupDB.setWritable(true);
        backupDB.setExecutable(true);
        FileUtils.copyFile(new FileInputStream(currentDB), new FileOutputStream(backupDB));


        return true;
    }

    private String exportDBtoDowloads(Context context) throws IOException {
        File dir = new File("/sdcard/Download/");

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

    private boolean importDBfromFolder(Context context) throws IOException{
        String BACKUP_NAME = "GIBackup.db";
        String BACKUP_PATH = "/sdcard/Download/";
        String NAME = "Portfolio.db";
        String PACKAGE_NAME = getContext().getApplicationContext().getPackageName();
        String DB_FILEPATH = "/data/data/" + PACKAGE_NAME + "/databases/" + NAME;

        File currentDB = new File(DB_FILEPATH);
        File backupDB = new File(BACKUP_PATH, BACKUP_NAME);

        if(backupDB.exists() && backupDB.canRead() && isDBExtension(backupDB.getName())){
            FileUtils.copyFile(new FileInputStream(backupDB), new FileOutputStream(currentDB));
            Toast.makeText(context, context.getString(R.string.restore_success), Toast.LENGTH_LONG).show();

            // Restart application
            Intent applicationIntent = getContext().getApplicationContext().getPackageManager()
                    .getLaunchIntentForPackage(PACKAGE_NAME);
            applicationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().finish();
            startActivity(applicationIntent);
        } else {
            Toast.makeText(context, context.getString(R.string.restore_download_folder_error, BACKUP_PATH), Toast.LENGTH_LONG).show();
        }

        return true;
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
        final String PACKAGE_NAME = getContext().getApplicationContext().getPackageName();
        String DB_FILEPATH = "/data/data/" + PACKAGE_NAME + "/databases/" + NAME;

        if (requestCode == Constants.Intent.IMPORT_DB) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                final File currentDB = new File(DB_FILEPATH);
                final Uri backupUri = data.getData();
                String fileExtension = getFileExtension(backupUri);
                if(fileExtension.equalsIgnoreCase("db")) {
                    new AlertDialog.Builder(getContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getContext().getString(R.string.restore_confirm_title))
                    .setMessage(getContext().getString(R.string.restore_confirm))
                    .setPositiveButton(getContext().getString(R.string.yes), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try{
                                FileInputStream backupInputStream = (FileInputStream) getContext().getContentResolver().openInputStream(backupUri);
                                FileUtils.copyFile(backupInputStream, new FileOutputStream(currentDB));

                                // Restart application
                                Intent applicationIntent = getContext().getApplicationContext().getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
                                applicationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                Toast.makeText(getContext().getApplicationContext(), getContext().getApplicationContext().getString(R.string.restore_success), Toast.LENGTH_LONG).show();
                                getActivity().finish();
                                startActivity(applicationIntent);
                            } catch (IOException e){
                                Log.e(LOG_TAG, e.toString());
                            }
                        }
                    }).setNegativeButton(getContext().getString(R.string.no), null)
                    .show();

                } else {
                   // File extension is wrong, not a valid file to import
                   Toast.makeText(getContext(), getString(R.string.restore_wrong_extension), Toast.LENGTH_SHORT).show();
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

    public boolean isDBExtension(String fileName) {
        if (fileName != null && fileName != ""){
            int cut = fileName.lastIndexOf('.');
            if (cut != -1){
                String ext = fileName.substring(cut + 1);
                if (ext.equalsIgnoreCase("db")){
                    return true;
                }
            }
        }
        return false;
    }
}
