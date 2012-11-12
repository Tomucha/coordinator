/*
* JBoss, Home of Professional Open Source
* Copyright 2009, Red Hat, Inc. and/or its affiliates, and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,  
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package cz.clovekvtisni.coordinator.server.validation.constraints.impl;

import cz.clovekvtisni.coordinator.server.validation.constraints.EqualProperties;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Hardy Ferentschik
 */
public class EqualPropertiesValidator implements ConstraintValidator<EqualProperties, Object> {

    private EqualProperties annotation;

    @Override
    public void initialize(EqualProperties constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(Object bean, ConstraintValidatorContext context) {
        Object prevValue = null;
        BeanWrapper beanWrapper = new BeanWrapperImpl(bean);
        for (int i = 0, l = annotation.properties().length; i < l; i++) {
            Object value = beanWrapper.getPropertyValue(annotation.properties()[i]);
            if (i == 0) {
                prevValue = value;
            }
            else {
                if (value == null && prevValue != null || value != null && !value.equals(prevValue)) {
                    context.buildConstraintViolationWithTemplate(annotation.message()).addNode(annotation.properties()[0]).addConstraintViolation().disableDefaultConstraintViolation();
                    return false;
                }
            }
        }
        return true;
    }
}
