package maingroup.wordbound.bookreaders;

import javafx.scene.control.Alert;
import maingroup.wordbound.accounts.AccountClass;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.mozilla.universalchardet.UniversalDetector;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Iterator;
import java.util.Objects;


public class Fb2Reader {
    private String jsonPath = new File("").getAbsolutePath() + "\\src\\main\\resources\\maingroup\\wordbound\\books\\bookinfo.json";
    public String bookPath;
    public String charset;
    public String bookName;
    public String realBookName;
    public String autor;
    public String bookdirPath;
    public String coverPath;
    public String lastDeck="default";
    public long lastPage=0;

    public Fb2Reader(String path, AccountClass account, boolean initialisation) throws IOException, ParseException {
        this.bookPath = path;

        if (initialisation) {

            this.bookName = "temp";
            String pathToProject = new File("").getAbsolutePath();
            String pathToBooks = pathToProject + "\\src\\main\\resources\\maingroup\\wordbound";
            pathToBooks = pathToBooks + "\\" + "books";
            String outputDir = pathToBooks + "\\" + this.bookName;
            this.bookdirPath = outputDir;
            String outputTxt = outputDir + "\\" + this.bookName + ".txt";
            File theDir = new File(outputDir);

            if (!theDir.exists()) {
                theDir.mkdirs();
            } else {
                System.out.println("temp directory is already present");
                return;
            }

            try {
                File myObj = new File(outputTxt);
                if (myObj.createNewFile()) {
                    System.out.println("temp text file created");
                } else {
                    System.out.println("temp txt file is already present");
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            try {
                copyFileUsingStream(new File(this.bookPath), new File(outputTxt));
            } catch (java.nio.file.NoSuchFileException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("file not found");
                alert.setHeaderText("error with file opening");
                alert.setContentText("file doesn't exsist");

                alert.showAndWait();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String charset = detectCharset(outputTxt);
            String content = readText(outputTxt, charset);
            this.charset = charset;

            int start = content.indexOf("<book-title>");
            int end = content.lastIndexOf("</book-title>");

            start += 12;
            char[] dest = new char[end - start];
            content.getChars(start, end, dest, 0);
            String bookname = new String(dest);
            this.realBookName=bookname;
            this.bookName = bookname.replaceAll(" ","");

            if (account.jsonWritter.alreadyadded(this.bookName)){
                deleteFiles();
                this.bookdirPath = pathToBooks + "\\" + this.bookName;
                this.bookPath = this.bookdirPath + "\\" + this.bookName + ".txt";
                this.autor = this.getAutor();
                System.out.println("book was already added");

            } else {
                File newdir = new File(pathToBooks + "\\" + this.bookName);
                File olddir = new File(outputDir);
                olddir.renameTo(newdir);

                this.bookdirPath = pathToBooks + "\\" + this.bookName;

                File newtxt = new File(this.bookdirPath + "\\" + this.bookName + ".txt");
                File oldtxt = new File(this.bookdirPath + "\\" + "temp" + ".txt");
                oldtxt.renameTo(newtxt);


                this.bookPath = this.bookdirPath + "\\" + this.bookName + ".txt";
                this.autor = this.getAutor();
                createСover();
                Bookdata bookdata= new Bookdata(this.bookPath,this.charset,this.realBookName,this.autor,this.bookdirPath, this.coverPath,this.bookName);
                account.jsonWritter.addNewBookToWordInBoundJson(this.bookName,this.realBookName);
                account.jsonWritter.updateJson(bookdata);
            }
        } else {

            Object obj = new JSONParser().parse(new FileReader(this.jsonPath));
            JSONObject jo = (JSONObject) obj;
            JSONObject booksInfo = (JSONObject) jo.get("books");
            JSONObject data = (JSONObject) booksInfo.get(this.bookPath);
            this.bookName=(String) data.get("name");
            this.realBookName=(String) data.get("realBookName");
            this.bookdirPath=(String) data.get("dirPath");
            this.autor=(String) data.get("author");
            this.lastDeck=(String) data.get("lastDeck");
            this.lastPage=(Long) data.get("lastPage");
            this.bookPath=(String) data.get("bookPath");
            this.charset=(String) data.get("charset");
            this.coverPath=(String) data.get("coverPath");

        }
    }

    private static String detectCharset(String filePath) {
        try (InputStream inputStream = new FileInputStream(filePath)) {
            byte[] bytes = new byte[4096];
            UniversalDetector detector = new UniversalDetector(null);
            int nread;
            while ((nread = inputStream.read(bytes)) > 0 && !detector.isDone()) {
                detector.handleData(bytes, 0, nread);
            }
            detector.dataEnd();
            return detector.getDetectedCharset();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String readText(String filePath, String charset) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(filePath), charset))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    public String getcontent() {

        return readText(this.bookPath, this.charset);
    }

    public String getText() {

        String content = getcontent();
        int startBody = content.indexOf("<body>");
        int endBody = content.lastIndexOf("</body>");

        endBody = endBody + 7;

        char[] dst = new char[endBody - startBody];
        content.getChars(startBody, endBody, dst, 0);
        String body = new String(dst);
        body = body.replace("image l:href=\"#", "img src=\"file://" + new File(this.bookdirPath).toString().replace("\\", "/") + "/");
        return body;
    }

    private void createСover() throws IOException {
        String content = getcontent();
        int startBin = content.indexOf("<binary");
        int endBin = content.lastIndexOf("</binary>");

        if (startBin != -1 || endBin != -1) {

            endBin = endBin + 9;

            char[] dstb = new char[endBin - startBin];
            content.getChars(startBin, endBin, dstb, 0);
            String binContent = new String(dstb);
            //System.out.println(binContent);

            // Объявляем массивы для данных о картинках
            ArrayList<String> binCode = new ArrayList<>();
            ArrayList<String> binImg = new ArrayList<>();

            // Получение отдельных binary
            do {
                int nextBin = binContent.indexOf("<binary");
                int lastBin = binContent.indexOf("</binary>");

                if (nextBin != -1) {
                    lastBin = lastBin + 9;

                    // Получение первой из оставшихся binary
                    char[] dstBin = new char[lastBin - nextBin];
                    binContent.getChars(nextBin, lastBin, dstBin, 0);
                    String binaryEl = new String(dstBin);

                    // Удаление текущей картинки из текста со всеми картинками
                    binContent = binContent.replace(binaryEl, "");

                    // Удаление закрывающего тега binary из текущей картинки
                    binaryEl = binaryEl.replace("</binary>", "");

                    // Получение строки с открывающим тегом binary
                    int findString = binaryEl.indexOf(">");
                    String tag = binaryEl.substring(0, findString + 1);

                    // Удаление открываюшего тега из картинки
                    binaryEl = binaryEl.replace(tag, "");

                    // Получение названия картинки
                    int findId = tag.indexOf("id=");
                    findId = findId + 4;
                    String imageName = tag.substring(findId);
                    int newFindId = imageName.indexOf("\"");
                    String delText = imageName.substring(newFindId);
                    imageName = imageName.replace(delText, "");

                    // Кладем данные о картинках в массивы
                    binCode.add(binaryEl);
                    binImg.add(imageName);
                } else {
                    break;
                }

            } while (binContent.indexOf("<binary") != -1);

            // Сохраняем картинки во временную директорию temp
            for (int i = 0; i < binCode.size(); i++) {

                // Декодируем код Base64 картинок и сохраняем как байтовый массив
                Decoder decoder = Base64.getMimeDecoder();
                byte[] decodedBytes = decoder.decode(binCode.get(i));

                try {
                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(decodedBytes));

                    // Получаем расширения картинок
                    String extImage = "";
                    int extNum = 0;
                    extNum = binImg.get(i).indexOf(".");
                    extImage = binImg.get(i).substring(extNum + 1);

                    // В зависимости от расширения сохраняем в соответствующий формат
                    this.coverPath=this.bookdirPath + "\\" + binImg.get(i);
                    if (extImage.equals("jpg")) {
                        File outputfile = new File(this.coverPath);
                        ImageIO.write(img, "jpg", outputfile);
                    } else if (extImage.equals("png")) {
                        File outputfile = new File(this.coverPath);
                        ImageIO.write(img, "png", outputfile);
                    } else if (extImage.equals("gif")) {
                        File outputfile = new File(this.coverPath);
                        ImageIO.write(img, "gif", outputfile);
                    }
                } catch (javax.imageio.IIOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getdesctiption() {
        String content = getcontent();

        int start = content.indexOf("<description>");
        int end = content.lastIndexOf("</description>");

        end = end + 14  ;

        char[] dest = new char[end - start];
        content.getChars(start, end, dest, 0);
        String description = new String(dest);
        description = description.replace("image l:href=\"#", "img src=\"file://" + new File(this.bookdirPath).toString().replace("\\", "/") + "/");

        return description;
    }

    private String getAutor() {

        String content = getdesctiption();

        int start = content.indexOf("<first-name>");
        int end = content.lastIndexOf("</first-name>");

        start = start + 12;

        char[] dest = new char[end - start];
        content.getChars(start, end, dest, 0);
        String firstName = new String(dest);

        start = content.indexOf("<middle-name>");
        end = content.lastIndexOf("</middle-name>");
        String middleName="";
        if(start!=-1&&end!=-1){
            start = start + 13;

            dest = new char[end - start];
            content.getChars(start, end, dest, 0);
            middleName = new String(dest);
        }


        start = content.indexOf("<last-name>");
        end = content.lastIndexOf("</last-name>");

        start = start + 11;

        dest = new char[end - start];
        content.getChars(start, end, dest, 0);
        String lastName = new String(dest);

        return firstName + " " + middleName + " " + lastName;
    }


    public void deleteFiles() throws IOException {
        System.out.println(this.bookdirPath);
        FileUtils.deleteDirectory(new File(this.bookdirPath));
    }
}
