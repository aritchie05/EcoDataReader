package com.apex.model.locale;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocaleEntry {
    private String id;
    private String en;
    private String fr;
    private String es;
    private String de;
    private String pt;
    private String it;
    private String tr;
    private String pl;
    private String ru;
    private String uk;
    private String ko;
    private String zh;
    private String ja;
}
