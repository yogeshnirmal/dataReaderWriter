package com.datawriter.datawriter.controller;

import com.datawriter.datawriter.service.PinCodeService;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RequestMapping("/pinCode")
@RestController
public class PinCodeController {
    @Autowired
    PinCodeService pinCodeService;

    @PostMapping("/upload")
    public String exceltoDb(@RequestParam("file") MultipartFile file) {
       return pinCodeService.excelToDto(file);
    }

    @PostMapping("/download")
    public ResponseEntity<?> exceltoDb() {
        try {
            ByteArrayOutputStream stream= new ByteArrayOutputStream();
            Workbook workbook = pinCodeService.dtoToExcel();
            workbook.write(stream);
            HttpHeaders headers =pinCodeService.getHeaderDataXLSX();
            return new ResponseEntity<>(new ByteArrayResource(stream.toByteArray()),headers, HttpStatus.CREATED);
        }catch (Exception e){
            System.out.println("Exception while retriving pincode"+e);
        }

        return null;
    }

    @PostMapping("/download/csv")
    public ResponseEntity<?> exceltoDbcsv() {
        try {
            ByteArrayOutputStream stream= new ByteArrayOutputStream();
            Workbook workbook = pinCodeService.dtoToCsvFile();
            workbook.write(stream);
            HttpHeaders headers =pinCodeService.getHeaderDataCSV();
            return new ResponseEntity<>(new ByteArrayResource(stream.toByteArray()),headers, HttpStatus.CREATED);
        }catch (Exception e){
            System.out.println("Exception while retriving pincode"+e);
        }
        return null;
    };

    @PostMapping("/download/export")
    public byte[] exceltoDbbyteArray() {
        return pinCodeService.dtoToByteCsv();
    };

    @PostMapping("/sample/download")
    public ResponseEntity<?> sampleFileDownload() throws IOException {
        ByteArrayOutputStream stream= new ByteArrayOutputStream();
        Workbook workbook=   pinCodeService.sampleFileDownload();
        workbook.write(stream);
        HttpHeaders headers=pinCodeService.sampleDownloadHeader();
        return new ResponseEntity<>(new ByteArrayResource(stream.toByteArray()),headers, HttpStatus.CREATED);
    };

    @PostMapping("/sentByEmail/csv")
    public void sentByEmail() {
        try {
            ByteArrayOutputStream stream= new ByteArrayOutputStream();
            Workbook workbook = pinCodeService.dtoToCsvFile();
            workbook.write(stream);
            HttpHeaders headers =pinCodeService.getHeaderDataCSV();
            pinCodeService.sentMail(new ByteArrayResource(stream.toByteArray()));
          //  return new ResponseEntity<>(new ByteArrayResource(stream.toByteArray()),headers, HttpStatus.CREATED);
        }catch (Exception e){
            System.out.println("Exception while retriving pincode"+e);
        }

    };
}