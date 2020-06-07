package com.galphie.dietme;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;

public class PdfViewerActivity extends AppCompatActivity {

    PDFView pdfView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        pdfView = findViewById(R.id.pdf_viewer);
        progressBar = findViewById(R.id.progressBar);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String viewType = getIntent().getStringExtra("ViewType");
            if (viewType != null) {
                if (viewType.equals("storage")) {
                    Uri pdfFile = Uri.parse(bundle.getString("FileUri"));
                    pdfView.fromUri(pdfFile)
                            .password(null)
                            .defaultPage(0)
                            .enableSwipe(true)
                            .swipeHorizontal(false)
                            .enableDoubletap(true)
                            .onDraw((canvas, pageWidth, pageHeight, displayedPage) -> {
                            })
                            .onDrawAll((canvas, pageWidth, pageHeight, displayedPage) -> {
                            })
                            .onPageError((page, t) -> Utils.toast(getApplicationContext(), getString(R.string.error_opening_page) + page))
                            .onPageChange((page, pageCount) -> {
                            })
                            .onTap(e -> true)
                            .onRender((nbPages, pageWidth, pageHeight) -> pdfView.fitToWidth())
                            .enableAnnotationRendering(true)
                            .invalidPageColor(Color.WHITE)
                            .load();
                } else if (viewType.equals("external")) {
                    progressBar.setVisibility(View.VISIBLE);
                    FileLoader.with(this)
                            .load(bundle.getString("Url"))
                            .asFile(new FileRequestListener<File>() {
                                @Override
                                public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                                    progressBar.setVisibility(View.GONE);

                                    File pdfFile = response.getBody();

                                    pdfView.fromFile(pdfFile)
                                            .password(null)
                                            .defaultPage(0)
                                            .enableSwipe(true)
                                            .swipeHorizontal(false)
                                            .enableDoubletap(true)
                                            .onDraw((canvas, pageWidth, pageHeight, displayedPage) -> {
                                            })
                                            .onDrawAll((canvas, pageWidth, pageHeight, displayedPage) -> {
                                            })
                                            .onPageError((page, t) -> Utils.toast(getApplicationContext(), getString(R.string.error_opening_page) + page))
                                            .onPageChange((page, pageCount) -> {
                                            })
                                            .onTap(e -> true)
                                            .onRender((nbPages, pageWidth, pageHeight) -> pdfView.fitToWidth())
                                            .enableAnnotationRendering(true)
                                            .invalidPageColor(Color.WHITE)
                                            .load();
                                }

                                @Override
                                public void onError(FileLoadRequest request, Throwable t) {
                                    progressBar.setVisibility(View.GONE);
                                    Utils.toast(getApplicationContext(), t.getMessage());
                                    Utils.copyToClipboard(getApplicationContext(), t.getMessage());
                                }
                            });
                }
            }
        }
    }
}
