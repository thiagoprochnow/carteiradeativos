package br.com.guiainvestimento.fragment;


import android.Manifest;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.activity.MainActivity;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.DbHelper;
import br.com.guiainvestimento.util.ApiClientAsyncTask;
import br.com.guiainvestimento.util.Util;
import br.com.guiainvestimento.utils.FileUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.DOWNLOAD_SERVICE;

public class BackupRestoreFragment extends BaseFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String LOG_TAG = BackupRestoreFragment.class.getSimpleName();
    private DbHelper mDBHelper;
    private GoogleApiClient mGoogleApiClient;

    private String NAME;
    private String PACKAGE_NAME;
    private String DB_FILEPATH;
    private File mCurrentDB;
    private String mBackupName;

    @BindView(R.id.google_drive_backup_db)
    LinearLayout googleDriveBackupDB;

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

        NAME = "Portfolio.db";
        PACKAGE_NAME = getActivity().getApplicationContext().getPackageName();
        DB_FILEPATH = "/data/data/" + PACKAGE_NAME + "/databases/" + NAME;
        mCurrentDB = new File(DB_FILEPATH);
        mBackupName = "GIBackup.db";

        // Set all click listeners
        googleDriveBackupDB.setOnClickListener(googleDriveBackupOnClick());
        googleDriveRestoreDB.setOnClickListener(googleDriveRestoreOnClick());

        getActivity().findViewById(R.id.fab).setVisibility(View.INVISIBLE);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        mGoogleApiClient = null;
                    }
                })
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    private View.OnClickListener googleDriveBackupOnClick() {
        View.OnClickListener onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Establish Drive Connection
                establishDriveConnection();

                if (mGoogleApiClient.isConnected()){

                    final ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
                            ResultCallback<DriveFolder.DriveFileResult>() {
                                @Override
                                public void onResult(DriveFolder.DriveFileResult result) {
                                    if (!result.getStatus().isSuccess()) {
                                        Log.e(LOG_TAG, "Error on fileCallback");
                                        return;
                                    }
                                }
                            };

                    // create new contents resource
                    final ResultCallback<DriveApi.DriveContentsResult> contentsCallback = new
                            ResultCallback<DriveApi.DriveContentsResult>() {
                                @Override
                                public void onResult(DriveApi.DriveContentsResult result) {
                                    if (!result.getStatus().isSuccess()) {
                                        Log.e(LOG_TAG, "Error on contentsCallback");
                                        return;
                                    }

                                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                            .setTitle(mBackupName)
                                            .setMimeType("application/vnd.oasis.opendocument.database").build();

                                    //Put backup data in the file
                                    DriveContents contents = result.getDriveContents();
                                    try {
                                        ParcelFileDescriptor parcelFileDescriptor = contents.getParcelFileDescriptor();
                                        FileInputStream fileInputStream = new FileInputStream(parcelFileDescriptor
                                                .getFileDescriptor());
                                        // Read to the end of the file.
                                        fileInputStream.read(new byte[fileInputStream.available()]);

                                        // Append to the file.
                                        FileOutputStream fileOutputStream = new FileOutputStream(parcelFileDescriptor
                                                .getFileDescriptor());

                                        FileUtils.copyFile(new FileInputStream(mCurrentDB), fileOutputStream);

                                        // Create a file in the root folder
                                        Drive.DriveApi.getRootFolder(mGoogleApiClient)
                                                .createFile(mGoogleApiClient, changeSet, contents)
                                                .setResultCallback(fileCallback);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };

                    Drive.DriveApi.newDriveContents(mGoogleApiClient)
                            .setResultCallback(contentsCallback);
                    Toast.makeText(mContext, getActivity().getResources().getString(R.string.backup_drive_success), Toast.LENGTH_SHORT).show();
                } else if(mGoogleApiClient.isConnecting()){
                    Toast.makeText(mContext, getActivity().getResources().getString(R.string.google_drive_connection), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, getActivity().getResources().getString(R.string.google_drive_connection_error), Toast.LENGTH_SHORT).show();
                }
            }
        };
        return onclick;
    }

    private View.OnClickListener googleDriveRestoreOnClick() {
        View.OnClickListener onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Establish Drive Connection
                establishDriveConnection();
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()){
                    IntentSender intentSender = Drive.DriveApi.newOpenFileActivityBuilder()
                            .setMimeType(new String[] { DriveFolder.MIME_TYPE , "application/vnd.oasis.opendocument.database", "text/plain"})
                            .build(mGoogleApiClient);
                    try {
                        startIntentSenderForResult(intentSender, Constants.Intent.GET_DRIVE_FILE, null, 0, 0, 0, null);
                    } catch (IntentSender.SendIntentException e){
                        Log.e(LOG_TAG, "IntentSender error: " + e.toString());
                    }
                } else if(mGoogleApiClient.isConnecting()){
                    Toast.makeText(mContext, getActivity().getResources().getString(R.string.google_drive_connection), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, getActivity().getResources().getString(R.string.google_drive_connection_error), Toast.LENGTH_SHORT).show();
                }
            }
        };
        return onclick;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == Constants.Intent.DRIVE_CONNECTION_RESOLUTION){
            if(mGoogleApiClient != null && !mGoogleApiClient.isConnecting()){
                mGoogleApiClient.connect();
            }
        } else if (requestCode == Constants.Intent.GET_DRIVE_FILE){
            if (resultCode == RESULT_OK) {
                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getContext().getString(R.string.restore_confirm_title))
                        .setMessage(getContext().getString(R.string.restore_confirm))
                        .setPositiveButton(getContext().getString(R.string.yes), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Get file from Google Drive server
                                DriveId driveId = (DriveId) data.getParcelableExtra(
                                        OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                                new RetrieveDriveFileContentsAsyncTask(getActivity()).execute(driveId);
                            }
                        }).setNegativeButton(getContext().getString(R.string.no), null)
                        .show();
            }
        }
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

    private void establishDriveConnection(){
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {

    }

    @Override
    public void onConnectionSuspended(int cause) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(getActivity(), Constants.Intent.DRIVE_CONNECTION_RESOLUTION);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), getActivity(), 0).show();
        }
    }

    final private class RetrieveDriveFileContentsAsyncTask
            extends ApiClientAsyncTask<DriveId, Boolean, String> {

        public RetrieveDriveFileContentsAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected String doInBackgroundConnected(DriveId... params) {
            String contents = null;
            DriveFile file = params[0].asDriveFile();
            if(file != null && mGoogleApiClient != null) {

                DriveResource.MetadataResult metadata = file.getMetadata(mGoogleApiClient).await();
                if (metadata != null && metadata.getStatus().isSuccess()) {

                    String fileName = metadata.getMetadata().getTitle();
                    if(isDBExtension(fileName)) {
                        DriveApi.DriveContentsResult driveContentsResult =
                                file.open(getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null).await();
                        if (!driveContentsResult.getStatus().isSuccess()) {
                            return null;
                        }
                        DriveContents driveContents = driveContentsResult.getDriveContents();
                        FileInputStream inputStream = (FileInputStream) driveContents.getInputStream();

                        try {
                            FileUtils.copyFile(inputStream, new FileOutputStream(mCurrentDB));
                        } catch (IOException e) {
                            Log.e(LOG_TAG, e.toString());
                        }

                        driveContents.discard(getGoogleApiClient());
                        return "true";
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.restore_wrong_extension), Toast.LENGTH_SHORT).show();
                        return "false";
                    }
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.restore_wrong_extension), Toast.LENGTH_SHORT).show();
                    return "false";
                }
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.google_drive_connection_error), Toast.LENGTH_SHORT).show();
                return "false";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != "true") {
                Log.d(LOG_TAG, "Error while reading from the file");
                return;
            } else {
                getActivity().finish();
                startActivity(getActivity().getIntent());
                Toast.makeText(mContext, mContext.getString(R.string.restore_success), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
