package icu.magicbox.cdn.repo;

import icu.magicbox.cdn.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface FileRepo extends JpaRepository<File, Long>, JpaSpecificationExecutor<File> {
}
