package icu.magicbox.cdn.service;

import icu.magicbox.cdn.domain.File;

import java.util.List;

/**
 * Description:
 * date: 2020/6/22 15:36
 *
 * @author ahqj
 * @since JDK 1.8
 */
public interface FileService {

    boolean add(String fileName, Long fileLength, String path, String repo, String url);

    List<File> list();
}
