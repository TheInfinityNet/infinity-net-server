package com.infinitynet.server.dtos.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileResponse {

    String uuid;

    String url;

    String backupPath;

    String type;

    double size; // in bytes

    Date createdAt;

}
