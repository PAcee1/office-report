package cn.afterturn.easypoi.util;

import cn.afterturn.easypoi.cache.manager.POICacheManager;
import cn.afterturn.easypoi.exception.excel.ExcelExportException;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.ImageUtils;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.XmlObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.apache.poi.ooxml.POIXMLTypeLoader.DEFAULT_XML_OPTIONS;

/**
 * 水印工具类,WPS仅支持打印水印
 * 参考 https://jingyan.baidu.com/article/6d704a137cd29d28db51ca87.html
 *
 * @author jueyue on 20-4-29.
 */
public class PoiWatermarkUtil {

    /**
     * 为Excel打上水印工具函数仅支持XLSX格式
     * @param sheet           需要打水印的Excel
     * @param waterRemarkPath 水印地址，classPath，目前只支持png格式的图片，
     *                        <p> 因为非png格式的图片打到Excel上后可能会有图片变红的问题，且不容易做出透明效果。 <p>
     * @param   position 位置   LEFT,CENTER,RIGHT
     * @throws IOException
     */
    public static void putWaterRemarkToExcel(Sheet sheet, String waterRemarkPath, String position) throws Exception {
        int                  pictureIdx     = sheet.getWorkbook().addPicture(IOUtils.toByteArray(POICacheManager.getFile(waterRemarkPath)), Workbook.PICTURE_TYPE_PNG);
        OPCPackage           opcpackage     = ((XSSFWorkbook) sheet.getWorkbook()).getPackage();
        PackagePartName      partname       = PackagingURIHelper.createPartName("/xl/drawings/vmlDrawing" + pictureIdx + ".vml");
        PackagePart          part           = opcpackage.createPart(partname, "application/vnd.openxmlformats-officedocument.vmlDrawing");
        VmlDrawing      vmldrawing = new VmlDrawing(part);
        //创建页眉,位置LEFT,下面headerPos填写对应的
        Header          header     = sheet.getHeader();
        switch (position){
            case "LEFT":
                header.setLeft("&G");
                vmldrawing.setHeaderPos("LH");
                break;
            case "CENTER":
                header.setCenter("&G");
                vmldrawing.setHeaderPos("CH");
                break;
            case "RIGHT":
                header.setRight("&G");
                vmldrawing.setHeaderPos("RH");
                break;
            default:
                throw new ExcelExportException("输入的position参数不合法");
        }
        XSSFPictureData picData    = (XSSFPictureData) sheet.getWorkbook().getAllPictures().get(pictureIdx);
        String               rIdPic         = vmldrawing.addRelation(null, XSSFRelation.IMAGES, picData).getRelationship().getId();
        ByteArrayInputStream is             = new ByteArrayInputStream(picData.getData());
        java.awt.Dimension   imageDimension = ImageUtils.getImageDimension(is, picData.getPictureType());
        IOUtils.closeQuietly(is);
        vmldrawing.setRIdPic(rIdPic);
        vmldrawing.setPictureTitle(waterRemarkPath);
        vmldrawing.setImageDimension(imageDimension);

        String rIdExtLink = ((XSSFSheet) sheet).addRelation(null, XSSFRelation.VML_DRAWINGS, vmldrawing).getRelationship().getId();
        ((XSSFSheet) sheet).getCTWorksheet().addNewLegacyDrawingHF().setId(rIdExtLink);
    }

    /**
     * 为Excel打上水印工具函数仅支持XLSX格式
     * @param sheet           需要打水印的Excel
     * @param waterRemarkPath 水印地址，classPath，目前只支持png格式的图片，
     *                        <p> 因为非png格式的图片打到Excel上后可能会有图片变红的问题，且不容易做出透明效果。 <p>
     * @throws IOException
     */
    public static void putWaterRemarkToExcel(Sheet sheet, String waterRemarkPath) throws Exception {
        putWaterRemarkToExcel(sheet,waterRemarkPath,"LEFT");
    }

    static class VmlDrawing extends POIXMLDocumentPart {

        String             rIdPic         = "";
        String             pictureTitle   = "";
        java.awt.Dimension imageDimension = null;
        String             headerPos      = "";

        VmlDrawing(PackagePart part) {
            super(part);
        }

        void setRIdPic(String rIdPic) {
            this.rIdPic = rIdPic;
        }

        void setPictureTitle(String pictureTitle) {
            this.pictureTitle = pictureTitle;
        }

        void setHeaderPos(String headerPos) {
            this.headerPos = headerPos;
        }

        void setImageDimension(java.awt.Dimension imageDimension) {
            this.imageDimension = imageDimension;
        }

        @Override
        protected void commit() throws IOException {
            PackagePart  part = getPackagePart();
            OutputStream out  = part.getOutputStream();
            try {
                // WPS测试通过版本
                /*XmlObject doc = XmlObject.Factory.parse(

                        "<xml xmlns:oa=\"urn:schemas-microsoft-com:office:activation\"" +
                                "    xmlns:p=\"urn:schemas-microsoft-com:office:powerpoint\"" +
                                "    xmlns:x=\"urn:schemas-microsoft-com:office:excel\"" +
                                "    xmlns:o=\"urn:schemas-microsoft-com:office:office\"" +
                                "    xmlns:v=\"urn:schemas-microsoft-com:vml\">" +
                                "    <o:shapelayout v:ext=\"edit\">" +
                                "        <o:idmap v:ext=\"edit\" data=\"1\"/>" +
                                "    </o:shapelayout>" +
                                "    <v:shape id=\""+headerPos+"\" " +
                                "        o:spid=\"_x0000_s1028\" o:spt=\"75\" alt=\"" + pictureTitle + "\"" +
                                "        type=\"#_x0000_t75\"" +
                                "        style=\"position:absolute;left:0pt;top:0pt;margin-left:0pt;margin-top:0pt;height:" + (int) imageDimension.getHeight() + "pt;width:" + (int) imageDimension.getWidth() + "pt;\"" +
                                "        filled=\"f\" o:preferrelative=\"t\" stroked=\"f\" coordsize=\"21600,21600\">" +
                                "        <v:path/>" +
                                "        <v:fill on=\"f\" focussize=\"0,0\"/>" +
                                "        <v:stroke on=\"f\"/>" +
                                "        <v:imagedata o:relid=\"" + rIdPic + "\" o:title=\"" + pictureTitle + "\"/>" +
                                "        <o:lock v:ext=\"edit\" rotation=\"t\" aspectratio=\"t\"/>" +
                                "    </v:shape>" +
                                "</xml>"

                );*/
                XmlObject doc = XmlObject.Factory.parse(

                        "<xml xmlns:v=\"urn:schemas-microsoft-com:vml\""
                                + " xmlns:o=\"urn:schemas-microsoft-com:office:office\""
                                + " xmlns:x=\"urn:schemas-microsoft-com:office:excel\">"
                                + " <o:shapelayout v:ext=\"edit\">"
                                + "  <o:idmap v:ext=\"edit\" data=\"1\"/>"
                                + " </o:shapelayout><v:shapetype id=\"_x0000_t75\" coordsize=\"21600,21600\" o:spt=\"75\""
                                + "  o:preferrelative=\"t\" path=\"m@4@5l@4@11@9@11@9@5xe\" filled=\"f\" stroked=\"f\">"
                                + "  <v:stroke joinstyle=\"miter\"/>"
                                + "  <v:formulas>"
                                + "   <v:f eqn=\"if lineDrawn pixelLineWidth 0\"/>"
                                + "   <v:f eqn=\"sum @0 1 0\"/>"
                                + "   <v:f eqn=\"sum 0 0 @1\"/>"
                                + "   <v:f eqn=\"prod @2 1 2\"/>"
                                + "   <v:f eqn=\"prod @3 21600 pixelWidth\"/>"
                                + "   <v:f eqn=\"prod @3 21600 pixelHeight\"/>"
                                + "   <v:f eqn=\"sum @0 0 1\"/>"
                                + "   <v:f eqn=\"prod @6 1 2\"/>"
                                + "   <v:f eqn=\"prod @7 21600 pixelWidth\"/>"
                                + "   <v:f eqn=\"sum @8 21600 0\"/>"
                                + "   <v:f eqn=\"prod @7 21600 pixelHeight\"/>"
                                + "   <v:f eqn=\"sum @10 21600 0\"/>"
                                + "  </v:formulas>"
                                + "  <v:path o:extrusionok=\"f\" gradientshapeok=\"t\" o:connecttype=\"rect\"/>"
                                + "  <o:lock v:ext=\"edit\" aspectratio=\"t\"/>"
                                + " </v:shapetype><v:shape id=\"" + headerPos + "\" o:spid=\"_x0000_s1025\" type=\"#_x0000_t75\""
                                + "  style='position:absolute;margin-left:0;margin-top:0;"
                                + "width:" + (int) imageDimension.getWidth() + "px;height:" + (int) imageDimension.getHeight() + "px;"
                                + "z-index:1'>"
                                + "  <v:imagedata o:relid=\"" + rIdPic + "\" o:title=\"" + pictureTitle + "\"/>"
                                + "  <o:lock v:ext=\"edit\" rotation=\"t\"/>"
                                + " </v:shape></xml>"

                );
                doc.save(out, DEFAULT_XML_OPTIONS);
                out.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

}