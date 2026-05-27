package ru.job4j.site.component;

import org.springframework.stereotype.Component;
import ru.job4j.site.dto.FilterDTO;
import ru.job4j.site.dto.FilterRequestParams;

import java.util.List;

@Component
public class FilterRequestParamsManager {

    public FilterRequestParams getParams(FilterDTO filter, List<Integer> topicIds) {
        var result = new FilterRequestParams();
        setProfileParams(result, filter);
        result.setTopicIds(topicIds);
        result.setStatus(filter.getStatus());
        result.setMode(filter.getMode());
        return result;
    }

    public FilterRequestParams getParams(FilterDTO filter) {
        var result = new FilterRequestParams();
        setProfileParams(result, filter);
        result.setTopicIds(filter.getTopicId() > 0 ? List.of(filter.getTopicId()) : List.of());
        result.setStatus(filter.getStatus());
        result.setMode(filter.getMode());
        return result;
    }

    private void setProfileParams(
            FilterRequestParams filterRequestParams, FilterDTO filter) {
        switch (filter.getFilterProfile()) {
            case 1 -> filterRequestParams.setSubmitterId(filter.getUserId());
            case 2 -> filterRequestParams.setAgreedWisherId(filter.getUserId());
            case 3 -> {
                filterRequestParams.setSubmitterId(filter.getUserId());
                filterRequestParams.setExclude(true);
            }
            case 4 -> {
                filterRequestParams.setAgreedWisherId(filter.getUserId());
                filterRequestParams.setExclude(true);
            }
            case 5 -> filterRequestParams.setWisherId(filter.getUserId());
            default -> {
            }
        }
    }
}
