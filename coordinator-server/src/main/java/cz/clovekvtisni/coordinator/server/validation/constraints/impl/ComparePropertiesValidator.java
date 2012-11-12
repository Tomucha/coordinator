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

import cz.clovekvtisni.coordinator.server.validation.constraints.CompareProperties;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Hardy Ferentschik
 */
public class ComparePropertiesValidator implements ConstraintValidator<CompareProperties, Object> {

    private CompareProperties annotation;

    @Override
    public void initialize(CompareProperties constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean isValid(Object bean, ConstraintValidatorContext context) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(bean);
        Object value1 = beanWrapper.getPropertyValue(annotation.firstProperty());
        Object value2 = beanWrapper.getPropertyValue(annotation.secondProperty());

        boolean valid;

        switch (annotation.operator()) {
            case NOT_EQ:
                valid = (value1 == null && value2 != null || value1 != null && !value1.equals(value2));
                break;
            case EQ:
                valid = (value1 == null && value2 == null || value1 != null && value1.equals(value2));
                break;
            case LT:
                valid = value1 == null || value2 == null || ((Comparable)value1).compareTo(value2) < 0;
                break;
            case LE:
                valid = value1 == null || value2 == null || ((Comparable)value1).compareTo(value2) <= 0;
                break;
            case GT:
                valid = value1 == null || value2 == null || ((Comparable)value1).compareTo(value2) > 0;
                break;
            case GE:
                valid = value1 == null || value2 == null || ((Comparable)value1).compareTo(value2) >= 0;
                break;
            default:
                throw new IllegalStateException("unsupported operator " + annotation.operator());
        }

        if (!valid) {
            context.buildConstraintViolationWithTemplate(annotation.message()).addNode(annotation.firstProperty()).addConstraintViolation().disableDefaultConstraintViolation();
        }

        return valid;
    }
}
