package com.skillzoomer_Attendance.com.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.skillzoomer_Attendance.com.Model.ModelDate;
import com.skillzoomer_Attendance.com.R;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HeaderFooterPageEvent extends PdfPageEventHelper {

    private Context context;
    private long siteId1;
    private String siteName1;
    private ArrayList<ModelDate> modelDateArrayList;
    private String reportType;



    public HeaderFooterPageEvent(Context context, long siteId1, String siteName1, ArrayList<ModelDate> modelDateArrayList, String reportType) {
        this.context = context;
        this.siteId1 = siteId1;
        this.siteName1 = siteName1;
        this.modelDateArrayList = modelDateArrayList;
        this.reportType = reportType;
    }

    public void onStartPage(PdfWriter writer, Document document) {


        com.itextpdf.text.Font fontHeaderNormal=new com.itextpdf.text.Font();
        fontHeaderNormal.setStyle(com.itextpdf.text.Font.BOLD);
        fontHeaderNormal.setSize(12);
        fontHeaderNormal.setColor(BaseColor.BLACK);
        fontHeaderNormal.setFamily(String.valueOf(Paint.Align.CENTER));

        com.itextpdf.text.Font fontHeaderBold=new com.itextpdf.text.Font();
        fontHeaderBold.setStyle(com.itextpdf.text.Font.BOLD);
        fontHeaderBold.setSize(14);
        fontHeaderBold.setColor(BaseColor.BLACK);
        fontHeaderBold.setFamily(String.valueOf(Paint.Align.CENTER));

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        String currentDate = df.format(c);
        PdfPTable my_table_header = new PdfPTable( 20);
        my_table_header.setWidthPercentage(95);

        PdfPCell table_cell_industry_name;
        PdfPCell table_cell_company_name;
        PdfPCell table_cell_siteId=new PdfPCell();
        PdfPCell table_cell_siteName = new PdfPCell();
        PdfPCell table_cell_generated_on;
        PdfPCell table_cell_workers_advances_report;
        PdfPCell table_cell_to= new PdfPCell(new Phrase(""));
        PdfPCell table_cell_from = new PdfPCell(new Phrase(""));
        PdfPCell table_cell_total_no_of_days;
        table_cell_industry_name=new PdfPCell(new Phrase("Industry Name: " + context.getSharedPreferences("UserDetails",Context.MODE_PRIVATE).getString("industryName",""),fontHeaderBold));
        table_cell_company_name=new PdfPCell(new Phrase("Company Name:"+context.getSharedPreferences("UserDetails",Context.MODE_PRIVATE).getString("companyName",""),fontHeaderBold));
        if(siteId1>0&& !siteName1.equals("")){
            table_cell_siteId=new PdfPCell(new Phrase( "Site Id: " + siteId1,fontHeaderNormal));
            table_cell_siteName=new PdfPCell(new Phrase("Site Name: " + siteName1,fontHeaderNormal));
        }else{
            table_cell_siteId=new PdfPCell(new Phrase( " ",fontHeaderNormal));
            table_cell_siteName=new PdfPCell(new Phrase("",fontHeaderNormal));
            table_cell_siteId.setBorder(Rectangle.NO_BORDER);
            table_cell_siteName.setBorder(Rectangle.NO_BORDER);
        }

        table_cell_generated_on=new PdfPCell(new Phrase("Generated On: " +currentDate,fontHeaderNormal));
        table_cell_workers_advances_report=new PdfPCell(new Phrase(reportType,fontHeaderBold));
        if(modelDateArrayList.size()>1){
            table_cell_from=new PdfPCell(new Phrase("From:"+modelDateArrayList.get(0).getDate(),fontHeaderNormal));
            table_cell_to=new PdfPCell(new Phrase("To:"+modelDateArrayList.get(modelDateArrayList.size()-1).getDate(),fontHeaderNormal));
        }else if(modelDateArrayList.size()==1){
            table_cell_from=new PdfPCell(new Phrase("From:"+currentDate));
            table_cell_to=new PdfPCell(new Phrase("To:"+currentDate));
        }else if(modelDateArrayList.size()==0){
            table_cell_from=new PdfPCell(new Phrase("From:"+"NA",fontHeaderNormal));
            table_cell_to=new PdfPCell(new Phrase("To:"+"NA"));
        }


        if(modelDateArrayList.size()>0){
            table_cell_total_no_of_days=new PdfPCell(new Phrase("Total No of days:"+modelDateArrayList.size(),fontHeaderNormal));
        }else{
            table_cell_total_no_of_days=new PdfPCell(new Phrase("",fontHeaderNormal));
            table_cell_total_no_of_days.setBorder(Rectangle.NO_BORDER);


        }



        table_cell_industry_name.setColspan(7);
        table_cell_industry_name.setBorder(Rectangle.NO_BORDER);
        table_cell_industry_name.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell_generated_on.setColspan(7);
        table_cell_generated_on.setBorder(Rectangle.NO_BORDER);
        table_cell_generated_on.setHorizontalAlignment(Element.ALIGN_CENTER);
        table_cell_company_name.setColspan(6);
        table_cell_company_name.setBorder(Rectangle.NO_BORDER);
        table_cell_company_name.setHorizontalAlignment(Element.ALIGN_CENTER);
        if(siteId1>0&& !siteName1.equals("")){
            table_cell_siteName.setColspan(7);
            table_cell_siteName.setBorder(Rectangle.NO_BORDER);
            table_cell_siteName.setHorizontalAlignment(Element.ALIGN_CENTER);
        }

        table_cell_workers_advances_report.setColspan(7);
        table_cell_workers_advances_report.setBorder(Rectangle.NO_BORDER);
        table_cell_workers_advances_report.setHorizontalAlignment(Element.ALIGN_CENTER);
        if(siteId1>0&& !siteName1.equals("")){
            table_cell_siteId.setColspan(6);
            table_cell_siteId.setBorder(Rectangle.NO_BORDER);
            table_cell_siteId.setHorizontalAlignment(Element.ALIGN_CENTER);
        }

        table_cell_from.setColspan(3);
        table_cell_from.setBorder(Rectangle.NO_BORDER);
        table_cell_from.setHorizontalAlignment(Element.ALIGN_LEFT);
        table_cell_to.setColspan(3);
        table_cell_to.setBorder(Rectangle.NO_BORDER);
        table_cell_to.setHorizontalAlignment(Element.ALIGN_LEFT);
        table_cell_total_no_of_days.setColspan(14);
        table_cell_total_no_of_days.setBorder(Rectangle.NO_BORDER);
        table_cell_total_no_of_days.setHorizontalAlignment(Element.ALIGN_RIGHT);


        my_table_header.addCell(table_cell_industry_name);
        my_table_header.addCell(table_cell_generated_on);
        my_table_header.addCell(table_cell_company_name);
        my_table_header.addCell(table_cell_siteName);
        my_table_header.addCell(table_cell_workers_advances_report);
        my_table_header.addCell(table_cell_siteId);
        my_table_header.addCell(table_cell_from);
        my_table_header.addCell(table_cell_to);
        my_table_header.addCell(table_cell_total_no_of_days);


        Paragraph p7 = new Paragraph("\n");
        p7.setAlignment(Paragraph.ALIGN_CENTER);

        try {
            document.add(my_table_header);
            document.add(p7);
            document.add(p7);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    public void onEndPage(PdfWriter writer, Document document) {
//        Image image=null;
//        try {
//            Drawable d = context.getResources().getDrawable(R.drawable.logo23);
//            BitmapDrawable bitDw = ((BitmapDrawable) d);
//            Bitmap bmp = bitDw.getBitmap();
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            image = Image.getInstance(stream.toByteArray());
//
//            image.setAlignment(Element.ALIGN_LEFT);
//            image.setWidthPercentage(50);
//            image.setScaleToFitHeight(true);
////            document.add(image);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        if(document.getPageNumber()>1){
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Generated by: Hajiri Register"), 220, 30, 0);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Powered by: " + " " + "Skill Zoomers"), 220, 20, 0);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase( "To know us more visit https://skillzoomers.com/hajiri-register/ "), 220, 10, 0);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("page " + document.getPageNumber()), 550, 30, 0);
        }


    }

}