package org.projeto.snowflakekafkapipeline.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Part {

    private Long pPartkey;
    private String pName;
    private String pBrand;
    private String pType;
    private Integer pSize;
}
