package com.infinitynet.server.dtos.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Pagination {
    int offset;

    int limit;

    int nextOffset;

    int previousOffset;

    int totalCount;

    int pageCount;

    int currentPage;
}
