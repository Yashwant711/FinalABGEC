package com.nikhil.finalabgec.Fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.nikhil.finalabgec.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ViewPhoto extends Fragment {

    View view;
    String image_link;
    private Context contextNullSafe;
    boolean share_ready=false;
    ImageView back;
    PhotoView image;
    TextView download;

    Bitmap myBitmap; // Your bitmap image
    String folderName = "abgec";
    String fileName = "MyImage_" + System.currentTimeMillis();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_view_photo, container, false);
        if (contextNullSafe == null) getContextNullSafety();

        try{
            assert getArguments() != null;
            image_link=getArguments().getString("image_sending_profile");
        } catch (Exception e) {
            e.printStackTrace();
        }

//        download = view.findViewById(R.id.download);
        image=view.findViewById(R.id.imageView8 );
        back=view.findViewById(R.id.imageView4);
        back.setOnClickListener(v-> back());

        Glide.with(getContextNullSafety())
                .asBitmap()
                .load(image_link)
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        share_ready=false;
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        share_ready=true;
                        image.setImageBitmap(resource);
                        return false;
                    }
                })
                .placeholder(R.drawable.ic_abgec_loading)
                .into(image);


//        download.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                ExecutorService executor = Executors.newSingleThreadExecutor();
//                Handler handler = new Handler(Looper.getMainLooper());
//
//                executor.execute(() -> {
//                    File directory = new File(requireContext().getExternalFilesDir(null), "abgec");
//                    boolean success = directory.mkdirs();
//                    if (!success && !directory.exists()) {
//                        Log.w("DownloadImage", "Directory not created");
//                    } else {
//                        try {
//                            URL url = new URL(image_link);
//                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                            connection.setDoInput(true);
//                            connection.connect();
//                            InputStream input = connection.getInputStream();
//                            Bitmap myBitmap = BitmapFactory.decodeStream(input);
//
//                            File imageFile = new File(directory, System.currentTimeMillis() + ".jpg");
//                            try (FileOutputStream stream = new FileOutputStream(imageFile)) {
//                                myBitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream);
//                                stream.flush();
//                            }
//
//                            handler.post(() -> Toast.makeText(getContext(), "Image downloaded and saved at: " + imageFile.getAbsolutePath(), Toast.LENGTH_LONG).show());
//                            Log.i("DownloadImage", "Image downloaded and saved: " + imageFile.getAbsolutePath());
//                        } catch (Exception e) {
//                            Log.e("DownloadImage", "Error downloading image", e);
//                            handler.post(() -> Toast.makeText(getContext(), "Download Failed", Toast.LENGTH_SHORT).show());
//                        }
//                    }
//                });
//            }
//        });



        return view;
    }
    /**CALL THIS IF YOU NEED CONTEXT*/
    public Context getContextNullSafety() {
        if (getContext() != null) return getContext();
        if (getActivity() != null) return getActivity();
        if (contextNullSafe != null) return contextNullSafe;
        if (getView() != null && getView().getContext() != null) return getView().getContext();
        if (requireContext() != null) return requireContext();
        if (requireActivity() != null) return requireActivity();
        if (requireView() != null && requireView().getContext() != null)
            return requireView().getContext();

        return null;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextNullSafe = context;
    }
    private void back(){
        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if(fm.getBackStackEntryCount()>0) {
            fm.popBackStack();
        }
        ft.commit();
    }


   /* public void saveImageToGallery(Context context, Bitmap bitmap, String folderName, String fileName) throws IOException {

        Uri imageUri;

        // For Android Q and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName + ".jpg");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + folderName);

            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            Toast.makeText(context, "wwqwwq", Toast.LENGTH_SHORT).show();
            if (imageUri != null) {
                try (OutputStream fos = resolver.openOutputStream(imageUri)) {
                    if (fos == null) {
                        throw new IOException("Failed to get output stream for URI: " + imageUri);
                    }
                    // If the bitmap is null, compress will fail. Ensure bitmap is not null here.
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)) {
                        throw new IOException("Failed to compress bitmap");
                    }
                    // No need to manually close fos here, try-with-resources handles it
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle exception: log it, inform the user, etc.
                }
            } else {
                throw new IOException("Image URI is null");
            }


        } else {
            // Pre-Q behavior using File
            String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + File.separator + folderName;
            File file = new File(imagesDir);
            if (!file.exists()) {
                file.mkdir();
            }
            File image = new File(imagesDir, fileName + ".jpg");
            try {
                OutputStream fos = new FileOutputStream(image);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                // Pre Android Q, manually add the file to the gallery
                MediaScannerConnection.scanFile(context, new String[]{image.getAbsolutePath()}, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

}