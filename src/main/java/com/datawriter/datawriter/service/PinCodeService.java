package com.datawriter.datawriter.service;

import com.datawriter.datawriter.domain.PinCode;
import com.datawriter.datawriter.repository.PinCodeRepository;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
public class PinCodeService {
    @Autowired
    PinCodeRepository pinCodeRepository;


    public String excelToDto(MultipartFile file) {
        Long startTime = System.currentTimeMillis();
        try {


            InputStream inputStream = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            // Skip header row
            Row headerRow = rowIterator.next();
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            ExecutorService executorService = Executors.newFixedThreadPool(availableProcessors);

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next(); //2nd row
           //     executorService.submit(() -> {
                    PinCode pinCode = mapRowToPinCode(row);
                    pinCodeRepository.save(pinCode);
              //  });
            }
          //  executorService.shutdown();
         //   while (!executorService.isTerminated()){
        //        System.out.println("Multithreading Terminated");
        //    }

            System.out.println("Time Required foe execution "+ ( System.currentTimeMillis()-startTime));
            return "File Uploaded Successfully";
        } catch (Exception ex) {
            System.out.println("Time Required foe execution "+ ( System.currentTimeMillis()-startTime));
            System.out.println("Error occurred while Writing data: " + ex);
            return "File Uploading Failed";
        }
    }

    private PinCode mapRowToPinCode(Row row) {
//Another Way To Do it
//        PinCode pinCode = new PinCode();
//        Iterator<Cell> cellIterator = row.cellIterator();
//        while (cellIterator.hasNext()) {
//            Cell cell = cellIterator.next();
//            int columnIndex = cell.getColumnIndex();
//            switch (columnIndex) {
//                case 0 -> {
//                    int pinCodeValue = (int) cell.getNumericCellValue();
//                    pinCode.setPinCode(pinCodeValue);
//                }
//                case 1 -> {
//                    String cityName = cell.getStringCellValue();
//                    pinCode.setCityName(cityName);
//                }
//                case 2 -> {
//                    String stateValue = cell.getStringCellValue();
//                    pinCode.setStateName(stateValue);
//                }
//            }
//        }

        int pinCodeValue = (int) row.getCell(0).getNumericCellValue();
        String cityName = row.getCell(1).getStringCellValue();
        String stateName = row.getCell(2).getStringCellValue();
        return PinCode.builder()
                .pinCode(pinCodeValue)
                .cityName(cityName)
                .stateName(stateName)
                .build();
    }

    public Workbook dtoToExcel() {
        List<PinCode> all = pinCodeRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("PinCode Data");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"PinCode", "CityName", "StateName"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        int rownum = 1;
        for (PinCode pinCode : all) {
            Row row = sheet.createRow(rownum++);
            row.createCell(0).setCellValue(pinCode.getPinCode());
            row.createCell(1).setCellValue(pinCode.getCityName());
            row.createCell(2).setCellValue(pinCode.getStateName());
        }

        return workbook;
    }


    public Workbook dtoToCsvFile() {
        List<PinCode> all = pinCodeRepository.findAll();
        File csvFile = new File("pincode_data.csv");
        Workbook workbook = new HSSFWorkbook(); // Create a new Workbook (HSSFWorkbook for .csv format)

        Sheet sheet = workbook.createSheet("PinCode Data"); // Create a sheet
        Row headerRow = sheet.createRow(0);
        String[] headers = {"PinCode", "CityName", "StateName"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Populate data rows
        int rowNum = 1;
        for (PinCode pinCode : all) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(pinCode.getPinCode());
            row.createCell(1).setCellValue(pinCode.getCityName());
            row.createCell(2).setCellValue(pinCode.getStateName());
        }

        return workbook;

    }

    public byte[] dtoToByteCsv() {
        List<PinCode> all = pinCodeRepository.findAll();
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8);
        ) {
            writer.write("PinCode,CityName,StateName");

            for (PinCode pinCode : all) {
                writer.write(pinCode.getPinCode() + ",");
                writer.write(pinCode.getCityName() + ",");
                writer.write(pinCode.getStateName() + "\n");
            }
            writer.flush();
            System.out.println("CSV data generated successfully.");
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            System.out.println("Error occurred while generating CSV data: " + e.getMessage());
        }
        return new byte[0];
    }

    public HttpHeaders getHeaderDataXLSX() {
        String fileName="PinCodeData.xlsx";
        HttpHeaders header=new HttpHeaders();
        header.setContentType(new MediaType("application","force-download"));
        header.set(HttpHeaders.CONTENT_DISPOSITION,String.format("attachment;filename=%s",fileName));
        return header;
    }
    public HttpHeaders getHeaderDataCSV() {
        String fileName="PinCodeData.csv";
        HttpHeaders header=new HttpHeaders();
        header.setContentType(new MediaType("application","force-download"));
        header.set(HttpHeaders.CONTENT_DISPOSITION,String.format("attachment;filename=%s",fileName));
        return header;
    }

    public HttpHeaders sampleDownloadHeader() {
        String fileName="SampleDownloadForUpload.xlsx";
        HttpHeaders header= new HttpHeaders();
        header.setContentType(new MediaType("application","force-download"));
        header.set(HttpHeaders.CONTENT_DISPOSITION,String.format("attachment;filename=%s",fileName));
        return header;
    }

    public Workbook sampleFileDownload() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("PinCode Data");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"PinCode", "CityName", "StateName", "Action"};
        for(int i=0;i<=3;i++){
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        String[] dataForDrowDown = getDataForDrowDown();
        int rownum = 1;
        while (rownum <= 50) {
            Row row = sheet.createRow(rownum);
            Cell cell = row.createCell(3);
            // Create a cell range for the dropdown for column index is 3
            CellRangeAddressList addressList = new CellRangeAddressList(rownum, rownum, 3, 3);

            // Create a DataValidation object
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();
            DataValidationConstraint explicitListConstraint = validationHelper.createExplicitListConstraint(dataForDrowDown);

            DataValidation validation = sheet.getDataValidationHelper().createValidation(explicitListConstraint, addressList);

            sheet.addValidationData(validation);
            rownum++;
        }
        return workbook;
    }

    private String [] getDataForDrowDown() {
        String[] headers = {"PinCode", "CityName", "StateName", "Action"};
        String colDropDownData [] = new String[2];
        for (String header : headers) {
            switch (header) {
                case "Action" -> {
                    colDropDownData = List.of("AddPicode", "DeletePinCode", "ShiftOfPinCode").toArray(new String[0]);
                }
                default -> {
                    continue;
                }
            }
        }
        return colDropDownData;
    }

    public void sentMail(ByteArrayResource workbook) {
        // Set properties for SMTP server
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com"); // SMTP server address for Gmail
        properties.put("mail.smtp.port", "465"); // Port for SMTP server (SMTPS port for Gmail)
        properties.put("mail.smtp.auth", "true"); // Enable authentication
        properties.put("mail.smtp.starttls.enable", "true"); // Enable TLS (not needed for SMTPS)
        properties.put("mail.smtp.ssl.enable", "true"); // Enable SSL/TLS (required for SMTPS)

        // Set up authentication
        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("shivanigiri08@gmail.com", "iqfurdkusqdgtevi"); // Gmail account credentials
            }
        };

        // Create a Session object
        Session session = Session.getInstance(properties, authenticator);

        try {
            // Create a MimeMessage object
            MimeMessage message = new MimeMessage(session);

            // Set From: header field
            message.setFrom(new InternetAddress("shivanigiri08@gmail.com")); // Replace with your email address

            // Set To: header field
            message.addRecipient(Message.RecipientType.TO, new InternetAddress("rudranilkantha@gmail.com")); // Replace with recipient's email address

            // Set Subject: header field
            message.setSubject("subject- Mail sent for testing purpose");

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Set the actual message
            messageBodyPart.setText("Dear Team, Kindly acknowledge the attached email from dataserver");

            // Create a multipart message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Part two is the workbook attachment
            messageBodyPart = new MimeBodyPart();
            DataSource source = new ByteArrayDataSource(workbook.getInputStream(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName("PinCodeData.xlsx");
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart);

            // Send message
            Transport.send(message);
            System.out.println("Email sent successfully!");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}