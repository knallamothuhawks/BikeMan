package de.rwth.idsg.velocity.web.rest;

import com.codahale.metrics.annotation.Timed;
import de.rwth.idsg.velocity.repository.ManagerRepository;
import de.rwth.idsg.velocity.web.rest.dto.modify.CreateEditManagerDTO;
import de.rwth.idsg.velocity.web.rest.dto.view.ViewManagerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

/**
 * REST controller for managing Pedelec.
 */
@RestController
@RequestMapping("/app")
@Produces(MediaType.APPLICATION_JSON)
public class ManagerResource {

    private static final Logger log = LoggerFactory.getLogger(PedelecResource.class);

    @Autowired
    private ManagerRepository managerRepository;

    private static final String BASE_PATH = "/rest/managers";
    private static final String ID_PATH = "/rest/managers/{id}";

    @Timed
    @RequestMapping(value = BASE_PATH, method = RequestMethod.GET)
    public List<ViewManagerDTO> getAll() throws BackendException {
        log.info("REST request to get all Manager");
        return managerRepository.findAll();
    }

    @Timed
    @RequestMapping(value = BASE_PATH, method = RequestMethod.POST)
    public void create(@Valid @RequestBody CreateEditManagerDTO manager) throws BackendException {
        log.debug("REST request to save manager : {}", manager);
        managerRepository.create(manager);
    }

    @Timed
    @RequestMapping(value = BASE_PATH, method = RequestMethod.PUT)
    public void update(@Valid @RequestBody CreateEditManagerDTO manager) throws BackendException {
        log.debug("REST request to update Manager");
        managerRepository.update(manager);
    }

    @Timed
    @RequestMapping(value = ID_PATH, method = RequestMethod.DELETE)
    public void delete(@PathVariable Long id) throws BackendException {
        log.debug("REST request to delete Manager : {}", id);
        managerRepository.delete(id);
    }

    ///// Methods to catch exceptions /////

    @ExceptionHandler(BackendException.class)
    public void backendConflict(HttpServletResponse response, BackendException e) throws IOException {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public void conflict(HttpServletResponse response, Exception e) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}