package com.apex.model.comparison;

import com.apex.model.Item;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ItemComparisonResult {
    private List<Item> newItems;
    private List<Item> removedItems;
}
