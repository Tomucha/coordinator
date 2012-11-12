/*
 * Copyright 2002-2007 the original author or authors.
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

package org.springbyexample.web.servlet.view.tiles2;

import org.apache.tiles.Attribute;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.TilesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.JstlUtils;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Used for rendering and processing a dynamic tiles view.</p>
 * 
 * @author David Winterfeldt
 */
public class DynamicTilesViewProcessor {

    final Logger logger = LoggerFactory.getLogger(DynamicTilesViewProcessor.class);
	
	/**
	 * Keeps Tiles definition to use once derived.
	 */
	private String derivedDefinitionName = null; 
	
	private String tilesDefinitionName = "mainTemplate";
	private String tilesBodyAttributeName = "content";
	private String tilesDefinitionDelimiter = ".";
    private String contentNameVariable = "tileViewContentName";
    private String viewNameVariable = "tileViewName";

	/**
	 * Main template name.  The default is 'mainTemplate'.
	 * 
	 * @param 	tilesDefinitionName		Main template name used to lookup definitions.
	 */
	public void setTilesDefinitionName(String tilesDefinitionName) {
		this.tilesDefinitionName = tilesDefinitionName;
	}

	/**
	 * Tiles body attribute name.  The default is 'body'.
	 * 
	 * @param 	tilesBodyAttributeName		Tiles body attribute name.
	 */
	public void setTilesBodyAttributeName(String tilesBodyAttributeName) {
		this.tilesBodyAttributeName = tilesBodyAttributeName;
	}

	/**
	 * Sets Tiles definition delimiter.  For example, instead of using 
	 * the request 'info/about' to lookup the template definition 
	 * 'info/mainTemplate', the default delimiter of '.' 
	 * would look for '.info.mainTemplate' 
	 * 
	 * @param 	tilesDefinitionDelimiter	Optional delimiter to replace '/' in a url.
	 */
	public void setTilesDefinitionDelimiter(String tilesDefinitionDelimiter) {
		this.tilesDefinitionDelimiter = tilesDefinitionDelimiter;
	}

	/**
	 * Renders output using Tiles.
	 */
	protected void renderMergedOutputModel(String beanName, String url,
	                                       ServletContext servletContext,
	                                       HttpServletRequest request, HttpServletResponse response,
	                                       TilesContainer container)
	       throws Exception {
        JstlUtils.exposeLocalizationContext(new RequestContext(request, servletContext));

        if (!response.isCommitted()) {
            // Tiles is going to use a forward, but some web containers (e.g.
            // OC4J 10.1.3)
            // do not properly expose the Servlet 2.4 forward request
            // attributes... However,
            // must not do this on Servlet 2.5 or above, mainly for GlassFish
            // compatibility.
            if (servletContext.getMajorVersion() == 2 && servletContext.getMinorVersion() < 5) {
                WebUtils.exposeForwardRequestAttributes(request);
            }
        }

        String definitionName = startDynamicDefinition(beanName, url, request, response, container);

        if (viewNameVariable != null && !"".equals(viewNameVariable)) {
            request.setAttribute(viewNameVariable, beanName);
        }
        if (contentNameVariable != null && !"".equals(contentNameVariable)) {
            int i = beanName.lastIndexOf("/");
            request.setAttribute(contentNameVariable, i >= 0 ? beanName.substring(i + 1) : beanName);
        }
        
        container.render(definitionName, request, response);
        
        endDynamicDefinition(definitionName, beanName, request, response, container);
    }
	
	/**
	 * Starts processing the dynamic Tiles definition by creating a temporary definition for rendering.
	 */
	@SuppressWarnings("deprecation")
    protected String startDynamicDefinition(String beanName, String url,
                                            HttpServletRequest request, HttpServletResponse response,
                                            TilesContainer container) 
	        throws TilesException {
       String definitionName = processTilesDefinitionName(beanName, container,
                                                          request, response);

        // create a temporary context and render using the incoming url as the
        // body attribute
        if (!definitionName.equals(beanName)) {
            Attribute attr = new Attribute();
            attr.setName(tilesBodyAttributeName);
            attr.setValue(url);

            AttributeContext attributeContext = container.startContext(request, response);
            attributeContext.putAttribute(tilesBodyAttributeName, attr);

            logger.debug("URL used for Tiles body.  url='" + url + "'.");
        }
        
        return definitionName;
	}

	/**
	 * Closes the temporary Tiles definition.
	 */
	protected void endDynamicDefinition(String definitionName, String beanName, 
	                                    HttpServletRequest request, HttpServletResponse response,
	                                    TilesContainer container) {
        if (!definitionName.equals(beanName)) {
            container.endContext(request, response);
        }	    
	}
	   
	/**
	 * Processes values to get tiles template definition name.  First 
	 * a Tiles definition matching the url is checked, then a 
	 * url specific template is checked, and then just the 
	 * default root definition is used.
	 * 
	 * @throws 	org.apache.tiles.TilesException		If no valid Tiles definition is found.
	 */
	protected String processTilesDefinitionName(String beanName,
	                                            TilesContainer container, 
												HttpServletRequest request,
												HttpServletResponse response)
			throws TilesException {
		// if definition already derived use it, otherwise 
		// check if url (bean name) is a template definition, then 
		// check for main template
		if (derivedDefinitionName != null) {
			return derivedDefinitionName;
		} else if (container.isValidDefinition(beanName, request, response)) {
			derivedDefinitionName = beanName;
			
			return beanName;
		} else {
			String result = null;
			
			StringBuilder sb = new StringBuilder();
			int lastIndex = beanName.lastIndexOf("/");
			boolean rootDefinition = false;
			
			// if delim, tiles def will start with it
			if (StringUtils.hasLength(tilesDefinitionDelimiter)) {
				sb.append(tilesDefinitionDelimiter);
			}
			
            String pathComponents[] = null;
			// if no '/', then at context root
			if (lastIndex == -1) {
				rootDefinition = true;
			} else {
				String path = (beanName != null ? beanName.substring(0, lastIndex) : "");

                pathComponents = path.split("/");

                if (StringUtils.hasLength(tilesDefinitionDelimiter)) {
                    path = StringUtils.replace(path, "/", tilesDefinitionDelimiter);
                }

                sb.append(path);

                if (StringUtils.hasLength(tilesDefinitionDelimiter)) {
					sb.append(tilesDefinitionDelimiter);
				}
			}
			
			sb.append(tilesDefinitionName);
			
			if (container.isValidDefinition(sb.toString(), request, response)) {
				result = sb.toString();
			} else if (!rootDefinition) {
				final String fullPath = sb.toString();
                
                String delimiter = StringUtils.hasLength(tilesDefinitionDelimiter) ? tilesDefinitionDelimiter : "/";

                for (int l = pathComponents.length - 1; l >= 0; l--) {
                    sb = new StringBuilder();
                    for  (int i = 0; i < l; i++) {
                        sb.append(delimiter).append(pathComponents[i]);
                    }
                    sb.append(delimiter).append(tilesDefinitionName);
                    String name = sb.toString();
                    if (container.isValidDefinition(name, request, response)) {
                        result = name;
                        break;
                    }
                }

				if (result == null) {
					throw new TilesException("No defintion of found for " +
							"'" + fullPath +"'" +
							" or '" + sb.toString() +"'");
				}
			}
			
			derivedDefinitionName = result;

			return result;
		}
	}

    public void setContentNameVariable(String contentNameVariable) {
        this.contentNameVariable = contentNameVariable;
    }

    public void setViewNameVariable(String viewNameVariable) {
        this.viewNameVariable = viewNameVariable;
    }
}
