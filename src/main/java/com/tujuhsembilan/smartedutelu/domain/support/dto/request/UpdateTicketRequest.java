package com.tujuhsembilan.smartedutelu.domain.support.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateTicketRequest {

    private UUID categoryId;
    private UUID priorityId;
    private UUID statusId;
    private UUID assignedTo;
}
