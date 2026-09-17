package org.web.shop_service.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VersionedResponse {
    @JsonIgnore
    private Long version;
}
