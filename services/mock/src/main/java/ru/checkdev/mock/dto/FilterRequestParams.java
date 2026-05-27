package ru.checkdev.mock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FilterRequestParams {

    private List<Integer> topicIds;
    private Integer submitterId;
    private Integer wisherId;
    private Integer agreedWisherId;
    private Integer status;
    private Integer mode;
    private boolean exclude;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FilterRequestParams that)) {
            return false;
        }
        return exclude == that.exclude
                && Objects.equals(topicIds, that.topicIds)
                && Objects.equals(submitterId, that.submitterId)
                && Objects.equals(wisherId, that.wisherId)
                && Objects.equals(agreedWisherId, that.agreedWisherId)
                && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return (((((((((Objects.hash(topicIds) * 31
                + Objects.hashCode(submitterId)) * 31)
                + Objects.hashCode(wisherId)) * 31)
                + Objects.hashCode(agreedWisherId)) * 31)
                + Objects.hashCode(status)) * 31)
                + Objects.hashCode(exclude)) * 31;
    }
}
