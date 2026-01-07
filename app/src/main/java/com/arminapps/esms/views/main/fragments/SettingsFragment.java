package com.arminapps.esms.views.main.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.arminapps.esms.R;
import com.arminapps.esms.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SettingsFragment extends Fragment {

    private SessionManager session;
    private ImageView qrImage;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        session = new SessionManager(requireContext());
        TextInputEditText editText = getView().findViewById(R.id.txt_my_security_key);
        qrImage = getView().findViewById(R.id.img_qr);
        editText.setText(session.getString("my_security_key"));
        generateQRCode(session.getString("my_security_key"));

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                session.setString("my_security_key", editText.getText().toString());
                generateQRCode(editText.getText().toString());
            }
        });
    }

    private void generateQRCode(String text) {
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            // Optional: Add hints for better error correction
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 1); // Smaller margin for cleaner look

            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 500, 500, hints);

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            (qrImage).setImageBitmap(bitmap);

        } catch (WriterException e) {
            Toast.makeText(requireContext(), "Error generating QR code", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
}