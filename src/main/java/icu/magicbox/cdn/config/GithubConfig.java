package icu.magicbox.cdn.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class GithubConfig {

    @Value("${github.user}")
    public String USER;

    @Value("${github.token}")
    public String TOKEN;

    @Value("${github.repo}")
    public String REPO;


}
