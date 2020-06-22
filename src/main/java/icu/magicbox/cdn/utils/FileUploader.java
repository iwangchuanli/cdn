package icu.magicbox.cdn.utils;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.security.NoSuchAlgorithmException;


public class FileUploader {

    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();


    /**
     * 由于没有考虑sha，故而只能新建文件，而不能更新文件(更新文件需要先get访问得到sha，然后再put)
     *
     * @param url   https://api.github.com/repos/:owner/:repo/contents/:path
     * @param file  需确保文件存在
     * @param token 用于鉴权
     * @return
     */
    public static boolean create(String url, File file, String token) {
        long begin = System.currentTimeMillis();
        System.out.println("上传开始...");
        //StringBuffer result = new StringBuffer();
        BufferedReader in = null;
        HttpURLConnection conn = null;
        try {
            URL realUrl = new URL(url);
            System.out.println(url);
            conn = (HttpURLConnection) realUrl.openConnection();
            conn.setConnectTimeout(120000);
            conn.setReadTimeout(120000);
            // 设置
            conn.setDoOutput(true); // 需要输出
            conn.setDoInput(true); // 需要输入
            conn.setUseCaches(false); // 不允许缓存
            conn.setRequestMethod("PUT"); // 设置PUT方式连接
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "token " + token);
            conn.setRequestProperty("User-Agent", "Github File Uploader App");

            conn.connect();
            // 传输数据
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            // 传输json头部
            dos.writeBytes("{\"message\":\".\",\"content\":\"");
            // 传输文件内容
            byte[] buffer = new byte[1024 * 1002]; // 3的倍数
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            long size = raf.read(buffer);
            while (size > -1) {
                if (size == buffer.length) {
                    dos.write(Base64.getEncoder().encode(buffer));
                } else {
                    byte tmp[] = new byte[(int) size];
                    System.arraycopy(buffer, 0, tmp, 0, (int) size);
                    dos.write(Base64.getEncoder().encode(tmp));
                }
                size = raf.read(buffer);
            }
            raf.close();
            // 传输json尾部
            dos.writeBytes("\"}");
            dos.flush();
            dos.close();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                //result.append(line).append("\n");
            }
        } catch (Exception e) {
            System.out.println("发送PUT请求出现异常！");
            e.printStackTrace();
            conn.disconnect();
            return false;
        } finally {
            try {
                in.close();
                conn.disconnect();
            } catch (Exception e2) {
            }
        }
        long end = System.currentTimeMillis();
        System.out.printf("上传结束，耗时 %ds\n", (end - begin) / 1000);
//        result.toString();
        return true;
    }

    /**
     * 更新文件(先get访问得到sha，然后再put)
     *
     * @param url   https://api.github.com/repos/:owner/:repo/contents/:path
     * @param file  需确保文件存在
     * @param token 用于鉴权
     * @return
     */
    public static boolean update(String url, File file, String token) {
        long begin = System.currentTimeMillis();
        System.out.println("获取文件SHA...");
        String sha = getSHA(url);
        if (sha == null)
            return false;
        long end = System.currentTimeMillis();
        System.out.printf("获取文件SHA 耗时 %ds\n", (end - begin) / 1000);
        System.out.println("上传开始...");
        //StringBuffer result = new StringBuffer();
        BufferedReader in = null;
        HttpURLConnection conn = null;
        try {
            URL realUrl = new URL(url);
            conn = (HttpURLConnection) realUrl.openConnection();
            conn.setConnectTimeout(120000);
            conn.setReadTimeout(120000);
            // 设置
            conn.setDoOutput(true); // 需要输出
            conn.setDoInput(true); // 需要输入
            conn.setUseCaches(false); // 不允许缓存
            conn.setRequestMethod("PUT"); // 设置PUT方式连接

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "token " + token);
            conn.setRequestProperty("User-Agent", "Github File Uploader App");
            conn.connect();
            // 传输数据
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            // 传输json头部
            dos.writeBytes("{\"message\":\".\",\"sha\":\"" + sha + "\",\"content\":\"");
            // 传输文件内容
            byte[] buffer = new byte[1024 * 1002]; // 3的倍数
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            long size = raf.read(buffer);
            while (size > -1) {
                if (size == buffer.length) {
                    dos.write(Base64.getEncoder().encode(buffer));
                } else {
                    byte tmp[] = new byte[(int) size];
                    System.arraycopy(buffer, 0, tmp, 0, (int) size);
                    dos.write(Base64.getEncoder().encode(tmp));
                }
                size = raf.read(buffer);
            }
            raf.close();
            // 传输json尾部
            dos.writeBytes("\"}");
            dos.flush();
            dos.close();

            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                //result.append(line).append("\n");
            }
        } catch (Exception e) {
            System.out.println("发送PUT请求出现异常！");
            e.printStackTrace();
            return false;
        } finally {
            try {
                in.close();
            } catch (Exception e2) {
            }
        }
        end = System.currentTimeMillis();
        System.out.printf("上传结束，耗时 %ds\n", (end - begin) / 1000);
        //result.toString()
        return true;
    }

    /**
     * 获取url 对应的SHA
     *
     * @param url
     * @param token
     * @return
     */
    static Pattern pattern = Pattern.compile("\"sha\": *\"([^\"]+)\"");

    public static String getSHA(String url) {
        StringBuffer result = new StringBuffer();
        BufferedReader in = null;
        HttpURLConnection conn = null;
        try {
            URL realUrl = new URL(url);
            conn = (HttpURLConnection) realUrl.openConnection();
            conn.setConnectTimeout(120000);
            conn.setReadTimeout(120000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Github File Uploader App");
            conn.connect();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line).append("\n");
            }
            Matcher matcher = pattern.matcher(result.toString());
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            System.out.println("请求SHA出现异常！");
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (Exception e2) {
            }
        }
        return null;
    }


    public static File MultipartFile2File(MultipartFile multipartFile) {
        //文件上传前的名称
        String fileName = multipartFile.getOriginalFilename();
        File file = new File(fileName);
        OutputStream out = null;
        try {
            //获取文件流，以文件流的方式输出到新文件
            //InputStream in = multipartFile.getInputStream();
            out = new FileOutputStream(file);
            byte[] ss = multipartFile.getBytes();
            for (int i = 0; i < ss.length; i++) {
                out.write(ss[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }


    private MultipartFile getMultipartFile(String picPath) {
        FileItem fileItem = createFileItem(picPath);
        MultipartFile mfile = new CommonsMultipartFile(fileItem);
        return mfile;
    }

    private static FileItem createFileItem(String filePath) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        String textFieldName = "textField";
        int num = filePath.lastIndexOf(".");
        String extFile = filePath.substring(num);
        FileItem item = factory.createItem(textFieldName, "text/plain", true,
                "MyFileName" + extFile);
        File newfile = new File(filePath);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        try {
            FileInputStream fis = new FileInputStream(newfile);
            OutputStream os = item.getOutputStream();
            while ((bytesRead = fis.read(buffer, 0, 8192))
                    != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return item;
    }

    /**
     * 计算文件 MD5
     *
     * @param file
     * @return 返回文件的md5字符串，如果计算过程中任务的状态变为取消或暂停，返回null， 如果有其他异常，返回空字符串
     */
    public static String calcMD5(File file) {
        try (InputStream stream = Files.newInputStream(file.toPath(), StandardOpenOption.READ)) {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buf = new byte[8192];
            int len;
            while ((len = stream.read(buf)) > 0) {
                digest.update(buf, 0, len);
            }
            return toHexString(digest.digest());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String toHexString(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

}
