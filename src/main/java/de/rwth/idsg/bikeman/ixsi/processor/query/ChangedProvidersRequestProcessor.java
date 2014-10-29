package de.rwth.idsg.bikeman.ixsi.processor.query;

import de.rwth.idsg.bikeman.ixsi.ErrorFactory;
import de.rwth.idsg.bikeman.ixsi.IXSIConstants;
import de.rwth.idsg.bikeman.ixsi.dto.query.ChangedProvidersResponseDTO;
import de.rwth.idsg.bikeman.ixsi.repository.QueryIXSIRepository;
import de.rwth.idsg.bikeman.ixsi.schema.ChangedProvidersRequestType;
import de.rwth.idsg.bikeman.ixsi.schema.ChangedProvidersResponseType;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 26.09.2014
 */
@Component
public class ChangedProvidersRequestProcessor implements
        StaticRequestProcessor<ChangedProvidersRequestType, ChangedProvidersResponseType> {

    @Inject private QueryIXSIRepository queryIXSIRepository;

    @Override
    public ChangedProvidersResponseType process(ChangedProvidersRequestType request) {
        long timestamp = request.getTimestamp().getMillis();
        ChangedProvidersResponseDTO responseDTO = queryIXSIRepository.changedProviders(timestamp);

        ChangedProvidersResponseType response = new ChangedProvidersResponseType();

        if (responseDTO.isProvidersChanged()) {
            response.getProvider().add(IXSIConstants.Provider.id);
        }

        return response;
    }

    // -------------------------------------------------------------------------
    // Error handling
    // -------------------------------------------------------------------------

    @Override
    public ChangedProvidersResponseType invalidSystem() {
        ChangedProvidersResponseType b = new ChangedProvidersResponseType();
        b.getError().add(ErrorFactory.invalidSystem());
        return b;
    }
}