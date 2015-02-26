package de.rwth.idsg.bikeman.psinterface.rest;

import de.rwth.idsg.bikeman.psinterface.Utils;
import de.rwth.idsg.bikeman.psinterface.dto.request.*;
import de.rwth.idsg.bikeman.psinterface.dto.response.AuthorizeConfirmationDTO;
import de.rwth.idsg.bikeman.psinterface.dto.response.AvailablePedelecDTO;
import de.rwth.idsg.bikeman.psinterface.dto.response.BootConfirmationDTO;
import de.rwth.idsg.bikeman.psinterface.dto.response.HeartbeatDTO;
import de.rwth.idsg.bikeman.service.CardAccountService;
import de.rwth.idsg.bikeman.web.rest.exception.DatabaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by swam on 31/07/14.
 */

@RestController
@RequestMapping(value = "/psi", produces = MediaType.APPLICATION_JSON)
@Slf4j
public class PsiController {

    @Inject private PsiService psiService;
    @Inject private CardAccountService cardAccountService;

    private static final String BOOT_NOTIFICATION_PATH = "/boot";
    private static final String AUTHORIZE_PATH = "/authorize";
    private static final String HEARTBEAT_PATH = "/heartbeat";
    private static final String ACTIVATE_CARD_PATH = "/activate-card";
    private static final String AVAIL_PEDELECS_PATH = "/available-pedelecs";

    private static final String STATION_STATUS_PATH = "/status/station";
    private static final String PEDELEC_STATUS_PATH = "/status/pedelec";
    private static final String CHARGING_STATUS_PATH = "/status/charging";
    private static final String FIRMWARE_STATUS_PATH = "/status/firmware";
    private static final String LOGS_STATUS_PATH = "/status/logs";

    private static final String TRANSACTION_START_PATH = "/transaction/start";
    private static final String TRANSACTION_STOP_PATH = "/transaction/stop";

    // -------------------------------------------------------------------------
    // Station
    // -------------------------------------------------------------------------

    @RequestMapping(value = BOOT_NOTIFICATION_PATH, method = RequestMethod.POST)
    public BootConfirmationDTO bootNotification(@RequestBody BootNotificationDTO bootNotificationDTO,
                                                HttpServletRequest request) throws DatabaseException {
        log.debug("[From: {}] Received bootNotification", Utils.getFrom(request));
        return psiService.handleBootNotification(bootNotificationDTO);
    }

    @RequestMapping(value = HEARTBEAT_PATH, method = RequestMethod.GET)
    public HeartbeatDTO heartbeat(HttpServletRequest request) {
        log.debug("[From: {}] Received heartbeat", Utils.getFrom(request));

        HeartbeatDTO heartbeatDTO = new HeartbeatDTO();
        heartbeatDTO.setTimestamp(Utils.nowInSeconds());
        return heartbeatDTO;
    }

    @RequestMapping(value = AVAIL_PEDELECS_PATH, method = RequestMethod.GET)
    public List<AvailablePedelecDTO> getAvailablePedelecs(HttpServletRequest request) throws DatabaseException {
        String endpointAddress = Utils.getFrom(request);
        log.debug("[From: {}] Received getAvailablePedelecs", endpointAddress);

        // TODO: uncomment this when in production
        //Long stationId = psiService.getStationIdByEndpointAddress(endpointAddress);

        Long stationId = 1L;
        return psiService.getAvailablePedelecs(stationId);
    }

    // -------------------------------------------------------------------------
    // User
    // -------------------------------------------------------------------------

    @RequestMapping(value = ACTIVATE_CARD_PATH, method = RequestMethod.POST)
    public AuthorizeConfirmationDTO activateCard(@RequestBody CardActivationDTO cardActivationDTO,
                                                 HttpServletRequest request, HttpServletResponse response) {
        log.info("[From: {}] Received activate card request for activation key'{}'",
                Utils.getFrom(request), cardActivationDTO.getActivationKey());

        return cardAccountService.activateCardAccount(cardActivationDTO, response);
    }

    @RequestMapping(value = AUTHORIZE_PATH, method = RequestMethod.POST)
    public AuthorizeConfirmationDTO authorize(@RequestBody CustomerAuthorizeDTO customerAuthorizeDTO,
                                              HttpServletRequest request) throws DatabaseException {
        String cardId = customerAuthorizeDTO.getCardId();
        log.info("[From: {}] Received authorization request for card id '{}'", Utils.getFrom(request), cardId);
        AuthorizeConfirmationDTO dto = psiService.handleAuthorize(customerAuthorizeDTO);
        log.info("User with card id '{}' is authorized", cardId);
        return dto;
    }

    // -------------------------------------------------------------------------
    // Transaction
    // -------------------------------------------------------------------------

    @RequestMapping(value = TRANSACTION_START_PATH, method = RequestMethod.POST)
    public void startTransaction(@RequestBody StartTransactionDTO startTransactionDTO,
                                 HttpServletRequest request) throws DatabaseException {
        log.info("[From: {}] Received startTransaction: {}", Utils.getFrom(request), startTransactionDTO);
        psiService.handleStartTransaction(startTransactionDTO);
    }

    @RequestMapping(value = TRANSACTION_STOP_PATH, method = RequestMethod.POST)
    public void stopTransaction(@RequestBody StopTransactionDTO stopTransactionDTO,
                                HttpServletRequest request) throws DatabaseException {
        log.info("[From: {}] Received stopTransaction: {}", Utils.getFrom(request), stopTransactionDTO);
        psiService.handleStopTransaction(stopTransactionDTO);
    }

    // -------------------------------------------------------------------------
    // Status
    // -------------------------------------------------------------------------

    @RequestMapping(value = STATION_STATUS_PATH, method = RequestMethod.POST)
    public void stationStatusNotification(@RequestBody StationStatusDTO stationStatusDTO,
                                          HttpServletRequest request) {
        log.debug("[From: {}] Received stationStatusNotification: {}", Utils.getFrom(request), stationStatusDTO);
        // TODO
    }

    @RequestMapping(value = PEDELEC_STATUS_PATH, method = RequestMethod.POST)
    public void pedelecStatusNotification(@RequestBody PedelecStatusDTO pedelecStatusDTO,
                                          HttpServletRequest request) {
        log.debug("[From: {}] Received pedelecStatusNotification: {}", Utils.getFrom(request), pedelecStatusDTO);
        // TODO
    }

    @RequestMapping(value = CHARGING_STATUS_PATH, method = RequestMethod.POST)
    public void chargingStatusNotification(@RequestBody List<ChargingStatusDTO> chargingStatusDTOs,
                                           HttpServletRequest request) {
        log.debug("[From: {}] Received chargingStatusNotification: {}", Utils.getFrom(request), chargingStatusDTOs);
        // TODO
    }

    @RequestMapping(value = FIRMWARE_STATUS_PATH, method = RequestMethod.POST)
    public void firmwareStatusNotification(@RequestBody FirmwareStatusDTO firmwareStatusDTO,
                                           HttpServletRequest request) {
        log.debug("[From: {}] Received firmwareStatusNotification: {}", Utils.getFrom(request), firmwareStatusDTO);
        // TODO
    }

    @RequestMapping(value = LOGS_STATUS_PATH, method = RequestMethod.POST)
    public void logsStatusNotification(@RequestBody LogsStatusDTO logsStatusDTO,
                                       HttpServletRequest request) {
        log.debug("[From: {}] Received logsStatusNotification: {}", Utils.getFrom(request), logsStatusDTO);
        // TODO
    }

}