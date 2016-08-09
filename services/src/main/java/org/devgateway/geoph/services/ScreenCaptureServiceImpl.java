package org.devgateway.geoph.services;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;
import com.machinepublishers.jbrowserdriver.UserAgent;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.devgateway.geoph.core.request.PrintParams;
import org.devgateway.geoph.core.services.PrintService;
import org.devgateway.geoph.core.services.ScreenCaptureService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dbianco
 *         created on jun 20 2016.
 */
@Service
public class ScreenCaptureServiceImpl implements ScreenCaptureService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenCaptureServiceImpl.class);
    private static final String PNG_EXTENSION = ".png";
    private static final String PDF_EXTENSION = ".pdf";
    private static final String HTML_EXTENSION = ".html";
    private static final Color BLUE = new Color(2, 64, 114);
    private static final Color BLACK = new Color(0, 0, 0);
    private static final double UPPERCASE_FACTOR = 1.3;
    private static final int MAX_CHARS = 133;
    private static final int X_POS = 36;
    private static final int Y_POS = 695;
    private static final int MIN_Y_POS = 30;

    @Value("${screen.capture.templates.html}")
    private String htmlTemplate;

    @Value("${screen.capture.templates.pdf}")
    private String pdfTemplate;

    @Value("${screen.capture.waiting.time}")
    private Long timeToWait;

    @Value("${screen.capture.img.url}")
    private String urlToShare;

    @Value("#{environment['repository.path']}")
    private String repository;

    @Autowired
    PrintService printService;


    public String createPdfFromHtmlString(PrintParams params, String key) throws Exception {
        File target = buildPage(params.getWidth(), params.getHeight(), params.getHtml()); //merge template and the passed html and return URL to resulted file
        BufferedImage image = captureImage(params.getWidth(),params.getHeight(), target.toURI()); //create screen shoot from html file
        if(image==null){
           throw  new Exception("Wasn't able to generate image please check logs");
        }
        return createPdf(image, params.getName(),params.getData(), key).getName();
    }


    public BufferedImage captureImage(Integer width, Integer height, URI target) {
        LOGGER.debug("Starting JBrowserDriver ");
        BufferedImage image = null;
        try {
            Dimension screen = new Dimension(width, height);
            WebDriver driver = new JBrowserDriver(Settings
                    .builder()
                    .logWarnings(false)
                    .logger(null)
                    .screen(screen)
                    .userAgent(UserAgent.CHROME)
                    .timezone(Timezone.AMERICA_NEWYORK)
                    .build());
            //TODO:externalize time out
            driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
            driver.get(target.toString());


            byte[] imageByte=((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            driver.quit();
        } catch (Exception e) {
            LOGGER.error("Image error: " + e.getMessage());
        }
        return image;
    }

    @Override
    /**
     * Scale image keeping aspect ration
     */
    public BufferedImage scaleWidth(BufferedImage original, Integer newWidth) {
        Integer w=original.getWidth();
        Float ratio=((float)w)/newWidth;
        Float   newHeight =original.getHeight()/ratio;
        return  resize(original, newWidth, newHeight.intValue());
    }

    @Override
    /**
     * Scale image keeping aspect ration
     */
    public BufferedImage scaleHeight(BufferedImage original, Integer newHeight) {
        Integer h=original.getHeight();
        Integer ratio=h/newHeight;
        Integer  newWidth=original.getWidth()/ratio;
        return  resize(original, newWidth, newHeight);
    }


    private  BufferedImage resize(BufferedImage original, Integer width, Integer height) {
        BufferedImage scaledBI = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
        Graphics2D g = scaledBI.createGraphics();
        g.drawImage(original, 0, 0, width, height, null);
        g.dispose();
        return scaledBI;
    }

    @Override
    public String toBase64(BufferedImage image) throws IOException {
        BASE64Encoder base64Encoder=new BASE64Encoder();
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bos);
        byte[] imageBytes = bos.toByteArray();
        BASE64Encoder encoder = new BASE64Encoder();
        imageString = encoder.encode(imageBytes);
        bos.close();
        return imageString;
    }



    public File buildPage(Integer width, Integer height, String html) {
        LOGGER.debug("Merge html");
        File file = null;
        try {
            URL url=new URL(htmlTemplate);
            Document doc = Jsoup.parse(url.openConnection().getInputStream(), "utf-8",url.getPath());

            doc.getElementById("content").append(html);
            doc.getElementById("map1").attr("style", "width:" + width + "px;height:" + height + "px");

            //Fix translate3D element
            removeTranslate3dFromDocument(doc);

            file = File.createTempFile("map-print", HTML_EXTENSION);
            //System.out.println(file.getAbsolutePath());
            FileUtils.writeStringToFile(file, doc.outerHtml());
        } catch (Exception e) {
            LOGGER.error("File error: " + e.getMessage());

        }
        return file;
    }


    private void removeTranslate3dFromDocument(Document doc) {
        Element pane = doc.getElementsByClass("leaflet-map-pane").get(0);
        String style = pane.attr("style");
        String pattern = "[-|\\d]*.px";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(style);
        String left;
        if (m.find()) ;
        {
            left = m.group(0);
        }
        String top;
        if (m.find()) ;
        {
            top = m.group(0);
        }
        pane.attr("style", "left:" + left + ";top:" + top);
    }

    private File createPdf(BufferedImage image, String name, Object data , String key) {
        LOGGER.debug("CreatePdf");
        File pdfFile = new File(repository, key + PDF_EXTENSION);

        try {
            PDDocument doc = PDDocument.load(new URL(pdfTemplate).openConnection().getInputStream());
            PDPageTree pages = doc.getDocumentCatalog().getPages();
            PDPage pdpage = pages.get(0);
            PDPageContentStream pc;
            PDFDocument pdf = new PDFDocument(Y_POS, X_POS, pdpage, doc);

            //Map title
            if(StringUtils.isNotBlank(name)) {
                addPdfText(pdf, PDType1Font.HELVETICA_BOLD, 13, BLUE, name);
                pdf.yPos -= 15;
            }

            //URL
            if(StringUtils.isNotBlank(key)) {
                addPdfText(pdf, PDType1Font.HELVETICA, 10, BLACK, urlToShare + key);
                pdf.yPos -= 15;
            }

            //Image
            PDImageXObject imageObj = LosslessFactory.createFromImage(pdf.document, image);
                pc = new PDPageContentStream(pdf.document, pdf.page, PDPageContentStream.AppendMode.APPEND, false);
            Dimension scaledDim = getAdaptedDimension(imageObj.getWidth(), imageObj.getHeight());
            pdf.yPos -= scaledDim.height;
            pc.drawImage(imageObj, pdf.xPos, pdf.yPos, scaledDim.width, scaledDim.height);
            pc.close();
            checkEndOfPage(pdf, 20);

            //Applied Layers
            addPdfText(pdf, PDType1Font.HELVETICA, 10, BLUE, "Applied Layers");
            checkEndOfPage(pdf, 20);

            //Filter Options
            Map jsonFilters = (Map) ((Map) data).get("filters");
            Map<String, Set<String>> filterMap = printService.getFilterNamesFromJson(jsonFilters);
            if(filterMap!= null) {
                addPdfText(pdf, PDType1Font.HELVETICA, 10, BLUE, "Filter Options");
                checkEndOfPage(pdf, 15);

                for(String filter : filterMap.keySet()) {
                    List<String> strList = splitValues(MAX_CHARS, filter, filterMap.get(filter));
                    for(String strToPrint:strList) {
                        addPdfText(pdf, PDType1Font.HELVETICA, 9, BLACK, strToPrint);
                        checkEndOfPage(pdf, 12);
                    }
                }
            }

            pdf.document.save(pdfFile);
            pdf.document.close();
        } catch (IOException e) {
            LOGGER.error("Error at: " + e.getMessage());

        }
        return pdfFile;

    }

    private PDFDocument checkEndOfPage(PDFDocument pdf, Integer y) throws IOException {
        pdf.yPos -= y;
        if(pdf.yPos<= MIN_Y_POS){
            PDPageTree pages = PDDocument.load(new URL(pdfTemplate).openConnection().getInputStream())
                    .getDocumentCatalog()
                    .getPages();
            pdf.page = pages.get(0);
            pdf.document.addPage(pdf.page);
            pdf.yPos = Y_POS;
        }
        return pdf;
    }

    private List<String> splitValues(int maxChars, String title, Set<String> values){
        List<String> ret = new LinkedList<>();
        StringBuilder sb = new StringBuilder("- " +     title + ": ");
        boolean isCommaNeeded = false;
        for(String value : values){
            if(isCommaNeeded){
                sb.append(", ");
            } else {
                isCommaNeeded = true;
            }
            int upperCase = countCapitals(sb.toString());
            long helper = sb.length() - upperCase + Math.round(upperCase * UPPERCASE_FACTOR);
            if(helper + value.length()< maxChars ){
                sb.append(value);
            } else {
                ret.add(sb.toString());
                sb = new StringBuilder("    " + value);
            }
        }
        ret.add(sb.toString());
        return ret;
    }

    private int countCapitals(String s) {
        if (s.length() == 1) {
            return (Character.isUpperCase(s.charAt(0)) ? 1 : 0);
        } else {
            return countCapitals(s.substring(1)) +
                    (Character.isUpperCase(s.charAt(0)) ? 1 : 0);
        }
    }

    private void addPdfText(PDFDocument pdf, PDType1Font font, int fontSize, Color color, String text) throws IOException {
        PDPageContentStream pc;
        pc = new PDPageContentStream(pdf.document, pdf.page, PDPageContentStream.AppendMode.APPEND, true);
        pc.beginText();
        pc.setFont(font, fontSize);
        pc.setNonStrokingColor(color);
        pc.newLineAtOffset(pdf.xPos, pdf.yPos);
        pc.setLeading(15D);
        pc.showText(text);
        pc.endText();
        pc.close();
    }

    private Dimension getAdaptedDimension(final int imgWidth, final int imgHeight) {
        int newWidth = imgWidth;
        int newHeight = imgHeight;

        if (newWidth > 540) {
            newWidth = 540;
            newHeight = (newWidth * imgHeight) / imgWidth;
        }

        if (newHeight > 560) {
            newHeight = 560;
            newWidth = (newHeight * imgWidth) / imgHeight;
        }

        return new Dimension(newWidth, newHeight);
    }

    class PDFDocument {

        int yPos;

        int xPos;

        PDPage page;

        PDDocument document;

        PDFDocument(int yPos, int xPos, PDPage page, PDDocument document) {
            this.yPos = yPos;
            this.xPos = xPos;
            this.page = page;
            this.document = document;
        }
    }
}
