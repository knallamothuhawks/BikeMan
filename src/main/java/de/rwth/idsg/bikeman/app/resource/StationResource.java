package de.rwth.idsg.bikeman.app.resource;

import com.codahale.metrics.annotation.Timed;
import de.rwth.idsg.bikeman.app.dto.RentPedelecDTO;
import de.rwth.idsg.bikeman.app.dto.ViewPedelecSlotDTO;
import de.rwth.idsg.bikeman.app.dto.ViewStationDTO;
import de.rwth.idsg.bikeman.app.dto.ViewStationSlotsDTO;
import de.rwth.idsg.bikeman.app.exception.AppException;
import de.rwth.idsg.bikeman.app.service.BookingService;
import de.rwth.idsg.bikeman.app.service.CurrentCustomerService;
import de.rwth.idsg.bikeman.app.service.PedelecService;
import de.rwth.idsg.bikeman.app.service.StationService;
import de.rwth.idsg.bikeman.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@RestController("StationResourceApp")
@RequestMapping(value = "/app", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class StationResource {

    @Autowired
    private StationService stationService;

    @Autowired
    private PedelecService pedelecService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private CurrentCustomerService currentCustomerService;

    private static final String BASE_PATH = "/stations";
    private static final String ID_PATH = "/stations/{id}";
    private static final String SLOT_PATH = "/stations/{id}/slots";
    private static final String RECOMMEND_PATH = "/stations/{id}/recommended-pedelec";
    private static final String RENTAL_PATH = "/stations/{stationId}/rent";

    @Timed
    @RequestMapping(value = BASE_PATH, method = RequestMethod.GET)
    public List<ViewStationDTO> getAll() throws AppException {
        log.debug("REST request to get all Stations");
        return stationService.getAll();
    }

    @Timed
    @RequestMapping(value = ID_PATH, method = RequestMethod.GET)
    public ViewStationDTO get(@PathVariable Long id) throws AppException {
        log.info("REST request to get Station : {}", id);
        return stationService.get(id);
    }

    @Timed
    @RequestMapping(value = SLOT_PATH, method = RequestMethod.GET)
    public ViewStationSlotsDTO getSlots(@PathVariable Long id) throws AppException {
        if (SecurityUtils.isAuthenticated()) {
            log.info("REST request to get Slots (for authenticated customer) of Station : {}", id);
            return stationService.getSlotsWithPreferredSlotId(id, currentCustomerService.getCurrentCustomer());
        } else {
            log.info("REST request to get Slots of Station : {}", id);
            return stationService.getSlots(id);
        }
    }

    @Timed
    @RequestMapping(value = RECOMMEND_PATH, method = RequestMethod.GET)
    public ViewPedelecSlotDTO getRecommendedPedelec(@PathVariable Long id, HttpServletResponse response) throws AppException {
        log.debug("REST request to get a suggestion for renting a pedelec");

        Optional<ViewPedelecSlotDTO> optional = bookingService.getSlot(currentCustomerService.getCurrentCustomer());
        if (optional.isPresent()) {
            return optional.get();
        }

        Optional<ViewPedelecSlotDTO> optionalPedelec = pedelecService.getRecommendedPedelec(id);

        if (optionalPedelec.isPresent()) {
            return optionalPedelec.get();
        }

        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        return null;
    }

    @Timed
    @RequestMapping(value = RENTAL_PATH, method = RequestMethod.POST)
    public ViewPedelecSlotDTO rentPedelec(HttpServletResponse response,
                                          @Valid @RequestBody RentPedelecDTO dto,
                                          @PathVariable Long stationId
                                          ) {
        log.debug("REST request to remotely rent a pedelec.");

        return stationService.authorizeRemote(
                stationId,
                dto.getStationSlotId(),
                currentCustomerService.getCurrentCustomer(),
                dto.getCardPin()
            );
    }

}
