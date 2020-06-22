package icu.magicbox.cdn.domain.model;

import lombok.Data;

import java.util.Map;

@Data
public class ResultModel {

    public String code;

    public Object data;

    public ResultModel(String code, Object data) {
        this.code = code;
        this.data = data;
    }
}
