package de.rwth.idsg.bikeman.psinterface.dto.request;

import lombok.Data;
import lombok.experimental.Builder;

/**
 * Created by swam on 31/07/14.
 */

@Builder
@Data
public class StartTransactionDTO {
//    private Long userId;
    private String cardId;
    private String pedelecManufacturerId;
    private String stationManufacturerId;
    private String slotManufacturerId;
    private Long timestamp;
}
