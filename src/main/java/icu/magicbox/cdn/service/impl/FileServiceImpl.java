package icu.magicbox.cdn.service.impl;

import icu.magicbox.cdn.domain.File;
import icu.magicbox.cdn.repo.FileRepo;
import icu.magicbox.cdn.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileRepo fileRepo;

    @Override
    public boolean add(String fileName, Long fileLength, String path, String repo, String url) {
        File file = new File();
        file.setFileName(fileName);
        file.setFileLength(fileLength);
        file.setPath(path);
        file.setRepo(repo);
        file.setUrl(url);
        fileRepo.save(file);
        return true;
    }

    @Override
    public List<File> list() {
        return fileRepo.findAll();
    }
}
