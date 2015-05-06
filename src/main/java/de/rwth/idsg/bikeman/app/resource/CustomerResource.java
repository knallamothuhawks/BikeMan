package de.rwth.idsg.bikeman.app.resource;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import de.rwth.idsg.bikeman.app.dto.ChangeTariffDTO;
import de.rwth.idsg.bikeman.app.dto.CreateCustomerDTO;
import de.rwth.idsg.bikeman.app.dto.ViewBookedTariffDTO;
import de.rwth.idsg.bikeman.app.dto.ViewCustomerDTO;
import de.rwth.idsg.bikeman.app.exception.AppException;
import de.rwth.idsg.bikeman.app.repository.CustomerRepository;
import de.rwth.idsg.bikeman.app.service.CurrentCustomerService;
import de.rwth.idsg.bikeman.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@RestController("CustomerResourceApp")
@RequestMapping(value = "/app", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class CustomerResource {

    @Autowired
    private CurrentCustomerService currentCustomerService;

    @Autowired
    private CustomerRepository customerRepository;

    private static final String BASE_PATH = "/customer";
    private static final String TARIFF_PATH = "/customer/tariff";
    private static final String TARIFF_AUTO_RENEWAL_PATH = "/customer/tariff/auto-renewal";

    @Timed
    @RequestMapping(value = BASE_PATH, method = RequestMethod.GET)
    public ViewCustomerDTO get() throws AppException {
        log.debug("REST request to get logged in customer");
        return currentCustomerService.get();
    }

    @Timed
    @RequestMapping(value = BASE_PATH, method = RequestMethod.PUT)
    public String update() throws AppException {
        log.debug("REST request to update logged in customer");
        return "TODO";
    }

    @Timed
    @JsonView(CreateCustomerDTO.View.class)
    @RequestMapping(value = BASE_PATH, method = RequestMethod.POST)
    public CreateCustomerDTO create(@Valid @RequestBody CreateCustomerDTO dto) throws AppException {
        log.debug("REST request to create customer");
        return customerRepository.create(dto);
    }

    @Timed
    @RequestMapping(value = TARIFF_PATH, method = RequestMethod.GET)
    public ViewBookedTariffDTO getCurrentTariff() {
        return currentCustomerService.getTariff();
    }


    @Timed
    @RequestMapping(value = TARIFF_PATH, method = RequestMethod.PUT)
    public ChangeTariffDTO setTariff(@Valid @RequestBody ChangeTariffDTO dto) throws AppException {
        return currentCustomerService.setTariff(dto);
    }

    @Timed
    @RequestMapping(value = TARIFF_AUTO_RENEWAL_PATH, method = RequestMethod.POST)
    public void enableAutomaticRenewal(HttpServletResponse response) {
        Boolean success = currentCustomerService.enableAutomaticRenewal();

        if (!success) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
    }

    @Timed
    @RequestMapping(value = TARIFF_AUTO_RENEWAL_PATH, method = RequestMethod.DELETE)
    public void disableAutomaticRenewal(HttpServletResponse response) {
        Boolean success = currentCustomerService.disableAutomaticRenewal();

        if (!success) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
    }
}