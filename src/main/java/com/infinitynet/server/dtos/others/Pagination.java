package com.infinitynet.server.dtos.others;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pagination {

    int offset;

    int limit;

    Integer nextOffset;

    Integer previousOffset;

    long totalCount;

    int pageCount;

    int currentPage;

    public Pagination(int offset, int limit, long totalCount) {
        this.offset = offset;
        this.limit = limit;
        this.totalCount = totalCount;
        this.nextOffset = (offset + limit < totalCount) ? offset + limit : null;
        this.previousOffset = (offset - limit >= 0) ? offset - limit : null;
        this.pageCount = (int) Math.ceil((double) totalCount / limit);
        this.currentPage = (int) Math.ceil((double) offset / limit) + 1;
    }

}
