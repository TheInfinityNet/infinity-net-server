package com.infinitynet.server.dtos.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.infinitynet.server.dtos.others.Pagination;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginateResponse<T> {

    List<T> items;

    Pagination pagination;

}
