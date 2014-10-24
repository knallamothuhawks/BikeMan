package de.rwth.idsg.bikeman.ixsi.processor.query;

import com.google.common.base.Optional;
import de.rwth.idsg.bikeman.ixsi.schema.AuthType;
import de.rwth.idsg.bikeman.ixsi.schema.Language;
import de.rwth.idsg.bikeman.ixsi.schema.PriceInformationRequestType;
import de.rwth.idsg.bikeman.ixsi.schema.PriceInformationResponseType;
import org.springframework.stereotype.Component;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 26.09.2014
 */
@Component
public class PriceInformationRequestProcessor extends AbstractUserRequestProcessor<PriceInformationRequestType, PriceInformationResponseType> {

    @Override
    public UserResponseParams<PriceInformationResponseType> processAnonymously(Optional<Language> lan, PriceInformationRequestType request) {
        return super.processAnonymously(lan, request);
    }

    @Override
    public UserResponseParams<PriceInformationResponseType> processForUser(Optional<Language> lan, AuthType auth, PriceInformationRequestType request) {
        return super.processForUser(lan, auth, request);
    }
}