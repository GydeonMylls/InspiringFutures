/*
 * Copyright 2017 Gideon Mills
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.cam.crim.inspiringfutures.ESM;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.ac.cam.crim.inspiringfutures.MainActivity;
import uk.ac.cam.crim.inspiringfutures.R;

/**
 * <p> Created by  Gideon Mills on 15/09/2017 for InspiringFutures-client. </p>
 */

public class ESM_AudioVideo extends ESM_Question implements ESM_FileCreator {

    public static final String TAG = "ESM_AudioVideo";
    private static final int REQUEST_PHOTO = 1;
    private static final int LAYOUT_ID = R.layout.esm_audiovideo;

    private static final String QUESTION_FILE_PREFIX = "IF_";

    private TextView mQuestion;
    private TextView mInstructions;
    private Button mPhotoButton;
//    private Button mAudioButton;
//    private Button mVideoButton;

    private ImageView mImageView;
    private File mPhotoFile;
    @Override
    public int getLayoutId() {
        return LAYOUT_ID;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mQuestion = view.findViewById(R.id.esm_question);
        mQuestion.setText(question());                      // Can't be compulsory, for now anyway

        mInstructions = view.findViewById(R.id.esm_instructions);
        mInstructions.setText(instructions());

        mPhotoButton = view.findViewById(R.id.esm_imagebutton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File photosDirectory = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                // TODO Change directory?
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                mPhotoFile = new File(photosDirectory, QUESTION_FILE_PREFIX + MainActivity.getDeviceId() + "_" + timeStamp + ".jpg");
                photosDirectory.mkdir();

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri photoURI = FileProvider.getUriForFile(getContext(), "uk.ac.cam.crim.inspiringfutures.fileprovider", mPhotoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                Log.d(TAG, Uri.fromFile(mPhotoFile).toString());
                if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {      // Check the intent can be handled
                    startActivityForResult(takePictureIntent, REQUEST_PHOTO);
                }

            }
        });

        mImageView = view.findViewById(R.id.esm_imageview);
        if (null != mPhotoFile && mPhotoFile.exists()) {
            showImage();
        } else {
            mImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public Serializable getResponse() {
        return mPhotoFile.getName();
    }

    @Override
    public boolean isAnswered() {
        // Can't be compulsory, for now anyway
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (REQUEST_PHOTO == requestCode) {
            Point displaySize = new Point();
            getActivity().getWindowManager().getDefaultDisplay().getSize(displaySize);

//            Bitmap scaledPhoto = getScaledBitmap(mPhotoFile, displaySize.x, displaySize.y);
//            mImageView.setImageBitmap(scaledPhoto);
//            mImageView.setVisibility(View.VISIBLE);
            showImage();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Resuming");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Pausing");
    }

    private void showImage() {
        if (null != mPhotoFile && mPhotoFile.exists()) {
            Point displaySize = new Point();
            getActivity().getWindowManager().getDefaultDisplay().getSize(displaySize);
            Bitmap scaledPhoto = getScaledBitmap(mPhotoFile, displaySize.x, displaySize.y);
//            mImageView.setImageBitmap(scaledPhoto);
            mImageView.setImageURI(Uri.fromFile(mPhotoFile));
            mImageView.setVisibility(View.VISIBLE);

            Log.d(TAG, "");
        }
    }

    public static Bitmap getScaledBitmap(File image, int targetWidth, int targetHeight) {
        // Read original dimensions
        BitmapFactory.Options inOptions = new BitmapFactory.Options();
        inOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(image.getPath(), inOptions);
        float originalWidth = inOptions.outWidth;
        float originalHeight = inOptions.outHeight;

        // Choose scale factor
        int scaleFactor = 1;
        if ( originalWidth > targetWidth || originalHeight > targetHeight) {
            if (originalWidth > originalHeight) {
                // Landscape image
                scaleFactor = Math.round(originalHeight / targetHeight);
            } else {
                // Portrait image
                scaleFactor = Math.round(originalHeight / originalWidth);
            }
        }

        BitmapFactory.Options outOptions = new BitmapFactory.Options();
        outOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeFile(image.getPath(), outOptions);
    }

    @Override
    public File[] getFiles() {
        return new File[]{
                mPhotoFile
        };
    }
}
