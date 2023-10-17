package com.apex.model.locale;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LocaleData {

    private String type;
    private List<LocaleEntry> entries;
}
