package com.luop;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Controller
public class HandlePdf {

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @PostMapping("/readPdf")
    @ResponseBody
    public String readPdf(@RequestParam("file") MultipartFile multipartFile, Integer page) throws IOException {
        if (multipartFile.isEmpty()) {
            return "上传的文件不能为空";
        }
        //获取文件名
        String filename = multipartFile.getOriginalFilename();
        //获取文件后缀名
        assert filename != null;
        String suffix = filename.substring(filename.lastIndexOf("."));
        //将MultipartFile转为File
        File file = File.createTempFile(System.currentTimeMillis() + "", suffix);
        multipartFile.transferTo(file);
        //加载pdf文档
        PDDocument pdDocument = PDDocument.load(file);
        //实例化Splitter类
        Splitter splitter = new Splitter();
        //分割pdf文档
        List<PDDocument> pages = splitter.split(pdDocument);
        if (pages.size() < page) {
            return "输入页码大于文件总页码数，请重新输入";
        }
        //创建迭代器对象
        Iterator<PDDocument> iterator = pages.listIterator();
        int i = 1;
        String filePath = "";
        while (iterator.hasNext()) {
            PDDocument pd = iterator.next();
            if (i == page) {
                //判断并创建文件夹路径
                File path = new File("D:\\pdfbox");
                if (!path.exists()) {
                    path.mkdir();
                }
                //生成指定页码文件
                filePath = "D:\\pdfbox\\temp_" + page + "_" + System.currentTimeMillis() + ".txt";
                pd.save(filePath);
                //关闭文档
                pdDocument.close();
                break;
            }
            i++;
        }


        //读取pdf内容
        File newFile = new File(filePath);
        PDDocument document = PDDocument.load(newFile);
//        FileInputStream in = new FileInputStream(newFile);
//        PDFParser parser = new PDFParser(new RandomAccessBuffer(in));
//        parser.parse();
//        PDDocument document = parser.getPDDocument();
        //实例化PDFTextStripper类
        PDFTextStripper stripper = new PDFTextStripper();
        //排序
        stripper.setSortByPosition(true);
        stripper.setAddMoreFormatting(true);
        //词分隔符
        stripper.setWordSeparator("&");
        //行分隔符
        stripper.setLineSeparator("*");
        //检索文本
        String content = stripper.getText(document);
        document.close();


//        return "文档加载完毕";
        return content;
    }
}
