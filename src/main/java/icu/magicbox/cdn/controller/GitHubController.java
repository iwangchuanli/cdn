package icu.magicbox.cdn.controller;

import icu.magicbox.cdn.config.GithubConfig;
import icu.magicbox.cdn.domain.model.ResultModel;
import icu.magicbox.cdn.service.FileService;
import icu.magicbox.cdn.utils.FileUploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description:
 * date: 2020/6/22 9:32
 *
 * @author ahqj
 * @since JDK 1.8
 */
@Controller
public class GitHubController {
//    String TOKEN = "589a882800fac67f924deceaa508de148c847370";
//    String USER = "wangchuanli001";
//    String REPO = "FileHub01";


    @Autowired
    private GithubConfig githubConfig;
    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    @ResponseBody
    public ResultModel upload(@RequestParam MultipartFile pic) throws IOException {
        File localFile = FileUploader.MultipartFile2File(pic);

        String fileName = UUID.randomUUID().toString().replaceAll("-", "") +
                localFile.getName();

        String path = new SimpleDateFormat("yyyy/MM/dd").format(new Date()) + "/";

        Long fileLength = localFile.length();

        String url = "https://api.github.com/repos/" + githubConfig.getUSER() + "/" + githubConfig.getREPO() + "/contents/" + path + fileName;


        FileUploader.create(url, localFile, githubConfig.getTOKEN());

        String returnUrl = "https://cdn.jsdelivr.net/gh/" + githubConfig.getUSER() + "/" + githubConfig.getREPO() + "/" + path + fileName;
        fileService.add(fileName, fileLength, path, githubConfig.getREPO(), returnUrl);
        Map returnMap = new HashMap();
        returnMap.put("filemd5", FileUploader.calcMD5(localFile));
        returnMap.put("url", returnUrl);

        localFile.delete();
        return new ResultModel("success", returnMap);
    }


    @GetMapping("/list")
    public String getList(Model model) {
        model.addAttribute("FileList", fileService.list());
        return "list";
    }
}
