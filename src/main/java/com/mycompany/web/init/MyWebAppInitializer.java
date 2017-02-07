
package com.mycompany.web.init;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * This class configures application context for dispatcher servlet (instead if .xml configuring).
 *
 *XML equivalent:

        <servlet>
 			<servlet-name>appServlet</servlet-name>
 				<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
 			<init-param>
 				<param-name>contextConfigLocation</param-name>
 				<param-value>/WEB-INF/spring/appServlet/servlet-context.xml</param-value>
 			</init-param>
 			<load-on-startup>1</load-on-startup>
 			<multipart-config>
 				<max-file-size>3000000</max-file-size>
 			</multipart-config>
 		</servlet>

 		<servlet-mapping>
 			<servlet-name>appServlet</servlet-name>
 			<url-pattern>/</url-pattern>
 		</servlet-mapping>

 */
public class MyWebAppInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext container) throws ServletException {

		XmlWebApplicationContext appContext = new XmlWebApplicationContext();
		
		appContext.setConfigLocation("/WEB-INF/spring/appServlet/servlet-context.xml");
		
	    ServletRegistration.Dynamic dispatcher = container.addServlet("appServlet", new DispatcherServlet(appContext));

	    MultipartConfigElement multipartConfigElement = new MultipartConfigElement(null, 5000000, 5000000, 0);
	    dispatcher.setMultipartConfig(multipartConfigElement);
	    
	    dispatcher.setLoadOnStartup(1);
	    dispatcher.addMapping("/");		
		
	}	
	
}
