package cz.clovekvtisni.coordinator.server.aspect;


import cz.clovekvtisni.coordinator.exception.MaException;
import cz.clovekvtisni.coordinator.server.security.AppContext;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
public class ExceptionTranslationAspect implements Ordered {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionTranslationAspect.class);

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AppContext appContext;

    @AfterThrowing(pointcut = "this(cz.clovekvtisni.coordinator.server.service.Service)", throwing = "ex")
    public void exception(Throwable ex) throws Throwable {
        processException(ex);
    }

/*
    protected Throwable translateException(Throwable throwable) {
        if (throwable instanceof MaException) {
            logger.debug("exception occurred", throwable);
            return throwable;
        }
        if (throwable instanceof ValidationException) {
            return throwable;
        }
        logger.debug("exception occurred", throwable);
        return MaException.internal(throwable.toString());
    }
*/

    protected void processException(Throwable throwable) throws Throwable {
/*
        throwable = translateException(throwable);
*/
        if (throwable instanceof MaException) {
            logger.debug("translate exception ", throwable);
            MaException ex = (MaException) throwable;
            try {
                ex.setLocalizedMessage(getMessage("error." + ex.getCode(), ex.getParams()));
            } catch (Throwable e) {
                logger.error("can't localize exception", e);
            }
        }
        throw throwable;
    }

    protected String getMessage(String code, String... params) {
        try {
            return messageSource.getMessage(code, params, appContext.getLocale());
        } catch (NoSuchMessageException e) {
            return code + ": " + Arrays.toString(params);
        }
    }


    @Override
    public int getOrder() {
        return -100000;
    }
}
