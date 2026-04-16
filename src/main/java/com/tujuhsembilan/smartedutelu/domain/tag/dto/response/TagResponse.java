package com.tujuhsembilan.smartedutelu.domain.tag.dto.response;

import com.tujuhsembilan.smartedutelu.domain.tag.entity.Tag;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TagResponse {

    private UUID id;
    private String name;
    private String slug;
    private String type;

    public static TagResponse from(Tag t) {
        return TagResponse.builder()
                .id(t.getId())
                .name(t.getName())
                .slug(t.getSlug())
                .type(t.getType())
                .build();
    }
}
