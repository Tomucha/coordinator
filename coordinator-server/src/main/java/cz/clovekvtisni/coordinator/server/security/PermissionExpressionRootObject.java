/*
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cz.clovekvtisni.coordinator.server.security;

import org.springframework.util.Assert;

import java.lang.reflect.Method;

/**
 * Class describing the root object used during the expression evaluation.
 *
 * @author Costin Leau
 * @since 3.1
 */
class PermissionExpressionRootObject {

	private final Method method;

	private final Object[] args;

	private final Object target;

	public PermissionExpressionRootObject(Method method, Object[] args, Object target) {
        Assert.notNull(method, "Method is required");
		this.method = method;
		this.target = target;
		this.args = args;
	}

    public Method getMethod() {
		return this.method;
	}

	public String getMethodName() {
		return this.method.getName();
	}

	public Object[] getArgs() {
		return this.args;
	}

	public Object getTarget() {
		return this.target;
	}
}
