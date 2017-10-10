package br.com.guiainvestimento.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.data.DbHelper;
import br.com.guiainvestimento.util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;

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

    @BindView(R.id.sdcard_restore_db)
    LinearLayout sdcardRestoreDB;

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
        sdcardRestoreDB.setOnClickListener(sdcardRestoreOnClick());
        googleDriveRestoreDB.setOnClickListener(googleDriveRestoreOnClick());

        getActivity().findViewById(R.id.fab).setVisibility(View.INVISIBLE);
        return view;
    }

    private View.OnClickListener downloadBackupOnClick() {
        View.OnClickListener onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String downloadedPath = Util.exportDBtoDowloads(mContext);
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
                    String downloadedPath = Util.exportDBtoSD(mContext);
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
                Log.d(LOG_TAG, "Google Drive Backup");
            }
        };
        return onclick;
    }

    private View.OnClickListener fileRestoreOnClick() {
        View.OnClickListener onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    File sdCardDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    if (sdCardDir.canRead()) {
                        File backupDB = new File(sdCardDir, "GIBackup.db");
                        mDBHelper.importDBfromDowloads();
                    }
                } catch (IOException e){
                    Log.e(LOG_TAG, e.toString());
                }
            }
        };
        return onclick;
    }

    private View.OnClickListener sdcardRestoreOnClick() {
        View.OnClickListener onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    File sdCardDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    if (sdCardDir.canRead()) {
                        File backupDB = new File(sdCardDir, "GIBackup.db");
                        mDBHelper.importDBfromSD();
                    }
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
                Log.d(LOG_TAG, "Google Drive Restore");
            }
        };
        return onclick;
    }
}
