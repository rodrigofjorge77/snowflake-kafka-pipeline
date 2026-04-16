package org.projeto.snowflakekafkapipeline.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Orders {

    private Long oOrderkey;
    private String oTotalprice;
    private String oOrderdate;
}
