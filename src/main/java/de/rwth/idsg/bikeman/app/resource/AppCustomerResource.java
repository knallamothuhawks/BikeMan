package de.rwth.idsg.bikeman.app.resource;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import de.rwth.idsg.bikeman.app.dto.*;
import de.rwth.idsg.bikeman.app.exception.AppException;
import de.rwth.idsg.bikeman.app.service.AppCurrentCustomerService;
import de.rwth.idsg.bikeman.app.service.AppCustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@RestController
@RequestMapping(value = "/app", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class AppCustomerResource {

    @Autowired
    private AppCurrentCustomerService appCurrentCustomerService;

    @Autowired
    private AppCustomerService appCustomerService;

    private static final String BASE_PATH = "/customer";
    private static final String CHANGE_PIN_PATH = "/customer/pin";
    private static final String CHANGE_PASSWORD_PATH = "/customer/password";
    private static final String CHANGE_ADDRESS_PATH = "/customer/address";
    private static final String ACTIVATION_PATH = "/customer/mailactivation/";
    private static final String PASSWORD_RESET_PATH = "/customer/passwordreset";
    private static final String PASSWORD_RESET_INIT_PATH = "/customer/passwordreset-request";
    private static final String PASSWORD_RESET_STATUS_PATH = "/customer/passwordreset/{key}/status";
    private static final String TARIFF_PATH = "/customer/tariff";
    private static final String TARIFF_AUTO_RENEWAL_PATH = "/customer/tariff/auto-renewal";

    @Timed
    @RequestMapping(value = BASE_PATH, method = RequestMethod.GET)
    public ViewCustomerDTO get() throws AppException {
        log.debug("REST request to get logged in customer");
        return appCurrentCustomerService.get();
    }

    @Timed
    @RequestMapping(value = BASE_PATH, method = RequestMethod.PUT)
    public String update() throws AppException {
        log.debug("REST request to update logged in customer");
        return "TODO";
    }

    @Timed
    @RequestMapping(value = CHANGE_PIN_PATH, method = RequestMethod.PUT)
    public void changePin(HttpServletResponse response, @Valid @RequestBody ChangePinDTO dto) throws AppException {
        log.debug("REST request to change PIN of customer");
        if (!appCurrentCustomerService.changePin(dto)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    @Timed
    @RequestMapping(value = CHANGE_PASSWORD_PATH, method = RequestMethod.PUT)
    public void changePassword(HttpServletResponse response, @Valid @RequestBody ChangePasswordDTO dto) throws AppException {
        log.debug("REST request to change Password of customer");
        if (!appCurrentCustomerService.changePassword(dto)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    @Timed
    @RequestMapping(value = CHANGE_ADDRESS_PATH, method = RequestMethod.PUT)
    public void changeAddress(@Valid @RequestBody ChangeAddressDTO dto) throws AppException {
        log.debug("REST request to change Address of customer");
        appCurrentCustomerService.changeAddress(dto);
    }

    @Timed
    @JsonView(CreateCustomerDTO.View.class)
    @RequestMapping(value = BASE_PATH, method = RequestMethod.POST)
    public CreateCustomerDTO create(@Valid @RequestBody CreateCustomerDTO dto) throws AppException {
        log.debug("REST request to create customer");
        return appCustomerService.create(dto);
    }

    @Timed
    @RequestMapping(value = PASSWORD_RESET_INIT_PATH, method = RequestMethod.POST)
    public void initPasswordReset(@Valid @RequestBody CreatePasswordResetRequestDTO dto, HttpServletResponse response) throws AppException {
        log.debug("REST request to initiate reset password");

        if (!appCustomerService.requestPasswordReset(dto.getLogin())) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
    }

    @Timed
    @RequestMapping(value = PASSWORD_RESET_PATH, method = RequestMethod.POST)
    public void resetPassword(@Valid @RequestBody CreatePasswordDTO dto, HttpServletResponse response) throws AppException {
        log.debug("REST request to reset password");

        if (!appCustomerService.changePassword(dto.getLogin(), dto.getKey(), dto.getPassword(), dto.getPasswordConfirm())) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
    }

    @Timed
    @RequestMapping(value = PASSWORD_RESET_STATUS_PATH, method = RequestMethod.GET)
    public void passwordResetStatus(@PathVariable String key, HttpServletResponse response) throws AppException {
        log.debug("REST request to get status of password reset key");

        if (!appCustomerService.validatePasswordResetKey(key)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Timed
    @RequestMapping(value = TARIFF_PATH, method = RequestMethod.GET)
    public ViewBookedTariffDTO getCurrentTariff() {
        return appCurrentCustomerService.getTariff();
    }


    @Timed
    @RequestMapping(value = TARIFF_PATH, method = RequestMethod.PUT)
    public ChangeTariffDTO setTariff(@Valid @RequestBody ChangeTariffDTO dto) throws AppException {
        return appCurrentCustomerService.setTariff(dto);
    }

    @Timed
    @RequestMapping(value = TARIFF_AUTO_RENEWAL_PATH, method = RequestMethod.POST)
    public void enableAutomaticRenewal(HttpServletResponse response) {
        Boolean success = appCurrentCustomerService.enableAutomaticRenewal();

        if (!success) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
    }

    @Timed
    @RequestMapping(value = TARIFF_AUTO_RENEWAL_PATH, method = RequestMethod.DELETE)
    public void disableAutomaticRenewal(HttpServletResponse response) {
        Boolean success = appCurrentCustomerService.disableAutomaticRenewal();

        if (!success) {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
    }
}
