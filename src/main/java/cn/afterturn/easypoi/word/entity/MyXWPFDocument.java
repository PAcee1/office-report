/**
 * Copyright 2013-2015 JueYue (qrb.jueyue@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.afterturn.easypoi.word.entity;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlToken;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTAnchor;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * 扩充document,修复图片插入失败问题问题
 *
 * @author JueYue
 * 2013-11-20
 * @version 1.0
 */
public class MyXWPFDocument extends XWPFDocument {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyXWPFDocument.class);

    private static String PICXML = ""
            + "<a:graphic xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">"
            + "   <a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">"
            + "      <pic:pic xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">"
            + "         <pic:nvPicPr>"
            + "            <pic:cNvPr id=\"%s\" name=\"Generated\"/>"
            + "            <pic:cNvPicPr/>" + "         </pic:nvPicPr>"
            + "         <pic:blipFill>"
            + "            <a:blip r:embed=\"%s\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"/>"
            + "            <a:stretch>"
            + "               <a:fillRect/>"
            + "            </a:stretch>" + "         </pic:blipFill>"
            + "         <pic:spPr>" + "            <a:xfrm>"
            + "               <a:off x=\"0\" y=\"0\"/>"
            + "               <a:ext cx=\"%s\" cy=\"%s\"/>"
            + "            </a:xfrm>"
            + "            <a:prstGeom prst=\"rect\">"
            + "               <a:avLst/>" + "            </a:prstGeom>"
            + "         </pic:spPr>" + "      </pic:pic>"
            + "   </a:graphicData>" + "</a:graphic>";

    public MyXWPFDocument() {
        super();
    }

    public MyXWPFDocument(InputStream in) throws Exception {
        super(in);
    }

    public MyXWPFDocument(OPCPackage opcPackage) throws Exception {
        super(opcPackage);
    }

    public void createPicture(String blipId, int id, int width, int height) {
        final int emu = 9525;
        width *= emu;
        height *= emu;
        CTInline inline   = createParagraph().createRun().getCTR().addNewDrawing().addNewInline();
        String   picXml   = String.format(PICXML, id, blipId, width, height);
        XmlToken xmlToken = null;
        try {
            xmlToken = XmlToken.Factory.parse(picXml);
        } catch (XmlException xe) {
            LOGGER.error(xe.getMessage(), xe);
        }
        inline.set(xmlToken);

        inline.setDistT(0);
        inline.setDistB(0);
        inline.setDistL(0);
        inline.setDistR(0);

        CTPositiveSize2D extent = inline.addNewExtent();
        extent.setCx(width);
        extent.setCy(height);

        CTNonVisualDrawingProps docPr = inline.addNewDocPr();
        docPr.setId(id);
        docPr.setName("Picture " + id);
        docPr.setDescr("Generated");
    }

    public void createPicture(XWPFRun run, String blipId, int id, int width, int height) {
        final int emu = 9525;
        width *= emu;
        height *= emu;
        CTInline inline   = run.getCTR().addNewDrawing().addNewInline();
        String   picXml   = String.format(PICXML, id, blipId, width, height);
        XmlToken xmlToken = null;
        try {
            xmlToken = XmlToken.Factory.parse(picXml);
        } catch (XmlException xe) {
            LOGGER.error(xe.getMessage(), xe);
        }
        inline.set(xmlToken);

        inline.setDistT(0);
        inline.setDistB(0);
        inline.setDistL(0);
        inline.setDistR(0);

        CTPositiveSize2D extent = inline.addNewExtent();
        extent.setCx(width);
        extent.setCy(height);

        CTNonVisualDrawingProps docPr = inline.addNewDocPr();
        docPr.setId(id);
        docPr.setName("Picture " + id);
        docPr.setDescr("Generated");
    }

    public void createPicture(XWPFRun run, String blipId, int id, int width, int height, boolean isAbove) {
        createPicture(run, blipId, id, width, height);
        CTDrawing         drawing         = run.getCTR().getDrawingArray(0);
        CTGraphicalObject graphicalObject = drawing.getInlineArray(0).getGraphic();
        //拿到新插入的图片替换添加CTAnchor 设置浮动属性 删除inline属性
        CTAnchor anchor = getAnchorWithGraphic(graphicalObject, "EasyPoi" + RandomStringUtils.randomAlphanumeric(10),
                Units.toEMU(width), Units.toEMU(height),//图片大小
                Units.toEMU(50), Units.toEMU(0), isAbove);
        drawing.setAnchorArray(new CTAnchor[]{anchor});
        drawing.removeInline(0);
    }


    /**
     * @param graphicalObject 图片数据
     * @param deskFileName    图片描述
     * @param width           宽
     * @param height          高
     * @param leftOffset      水平偏移 left
     * @param topOffset       垂直偏移 top
     * @param behind          文字上方，文字下方
     * @return
     * @throws Exception
     */
    public static CTAnchor getAnchorWithGraphic(CTGraphicalObject graphicalObject,
                                                String deskFileName, int width, int height,
                                                int leftOffset, int topOffset, boolean behind) {
        String anchorXML =
                "<wp:anchor xmlns:wp=\"http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing\" "
                        + "simplePos=\"0\" relativeHeight=\"0\" behindDoc=\"" + ((behind) ? 1 : 0) + "\" locked=\"0\" layoutInCell=\"1\" allowOverlap=\"1\">"
                        + "<wp:simplePos x=\"0\" y=\"0\"/>"
                        + "<wp:positionH relativeFrom=\"column\">"
                        + "<wp:posOffset>" + leftOffset + "</wp:posOffset>"
                        + "</wp:positionH>"
                        + "<wp:positionV relativeFrom=\"paragraph\">"
                        + "<wp:posOffset>" + topOffset + "</wp:posOffset>" +
                        "</wp:positionV>"
                        + "<wp:extent cx=\"" + width + "\" cy=\"" + height + "\"/>"
                        + "<wp:effectExtent l=\"0\" t=\"0\" r=\"0\" b=\"0\"/>"
                        + "<wp:wrapNone/>"
                        + "<wp:docPr id=\"1\" name=\"Drawing 0\" descr=\"" + deskFileName + "\"/><wp:cNvGraphicFramePr/>"
                        + "</wp:anchor>";

        CTDrawing drawing = null;
        try {
            drawing = CTDrawing.Factory.parse(anchorXML);
        } catch (XmlException e) {
            e.printStackTrace();
        }
        CTAnchor anchor = drawing.getAnchorArray(0);
        anchor.setGraphic(graphicalObject);
        return anchor;
    }

}
