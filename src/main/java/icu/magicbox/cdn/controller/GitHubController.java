package icu.magicbox.cdn.controller;

import icu.magicbox.cdn.utils.FileUploader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

/**
 * Description:
 * date: 2020/6/22 9:32
 *
 * @author ahqj
 * @since JDK 1.8
 */
@Controller
@ResponseBody
public class GitHubController {


    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) throws IOException {
        String TOKEN = "7dad21c361bf6bd76a7e600b0f99a7a03e91d76a";
        String USER = "wangchuanli001";
        String REPO = "cdn";
//        String USER ="";

        System.out.println(file.getName());
//
//        String fileName = file.getName();
//        byte[] textByte = file.getBytes();
//        String encodedText = Base64.getEncoder().encodeToString(textByte);
//        System.out.println(encodedText);

        File localFile = FileUploader.MultipartFile2File(file);

        String fileName = localFile.getName();
        String url = "https://api.github.com/repos/" + USER + "/" + REPO + "/contents/imgtemp/" + fileName;


        FileUploader.create(url, localFile, TOKEN);
//        localFile.delete();

//        file_name = ""  #文件名
//        21     token = "[token]"
//        22     url = "https://api.github.com/repos/[user]/[repo]/contents/[path]/"+file_name  # 用户名、库名、路径
//        23     headers = {"Authorization": "token " + token}
//        24     content = file_base64(file_data)
//        25     data = {
//                26         "message": "message",
//                27         "committer": {
//            28             "name": "[user]",
//                    29             "email": "user@163.com"
//            30         },
//        31         "content": content
//        32     }
//        33     data = json.dumps(data)
//        34     req = requests.put(url=url, data=data, headers=headers)
//        35     req.encoding = "utf-8"
//        36     re_data = json.loads(req.text)
//        37     print(re_data)
//        38     print(re_data['content']['sha'])
//        39     print("https://cdn.jsdelivr.net/gh/[user]/[repo]/[path]"+file_name)

        return "https://cdn.jsdelivr.net/gh/" + USER + "/" + REPO + "/contents/imgtemp/" + fileName;
    }
}
