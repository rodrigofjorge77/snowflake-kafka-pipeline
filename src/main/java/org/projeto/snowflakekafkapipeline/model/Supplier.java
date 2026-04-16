package org.projeto.snowflakekafkapipeline.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {

    private Long sSuppkey;
    private String sName;
    private String sAcctbal;
}
