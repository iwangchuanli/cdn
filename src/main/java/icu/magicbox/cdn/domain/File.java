package icu.magicbox.cdn.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class File {

    @Id
    @GeneratedValue
    private Long id;

    private String fileName;
    /**
     * bytes
     */
    private Long fileLength;

    private String path;

    private String repo;

    private String url;

}
