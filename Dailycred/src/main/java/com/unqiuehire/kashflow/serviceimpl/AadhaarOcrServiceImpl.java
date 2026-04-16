package com.unqiuehire.kashflow.serviceimpl;

import com.unqiuehire.kashflow.service.AadhaarOcrService;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AadhaarOcrServiceImpl implements AadhaarOcrService {

    @Value("${app.ocr.tessdata.path}")
    private String tessdataPath;

    @Override
    public String extractText(MultipartFile file) {
        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());

            if (bufferedImage == null) {
                throw new RuntimeException("Uploaded Aadhaar file is not a valid image");
            }

            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath(tessdataPath);
            tesseract.setLanguage("eng");

            return tesseract.doOCR(bufferedImage);
        } catch (TesseractException e) {
            throw new RuntimeException("OCR failed: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unable to read Aadhaar image: " + e.getMessage(), e);
        }
    }

    @Override
    public String extractAadhaarNumber(String ocrText) {
        if (ocrText == null || ocrText.isBlank()) {
            return null;
        }

        Pattern pattern = Pattern.compile("\\b\\d{4}\\s?\\d{4}\\s?\\d{4}\\b");
        Matcher matcher = pattern.matcher(ocrText);

        if (matcher.find()) {
            return matcher.group().replaceAll("\\D", "");
        }

        return null;
    }
}
