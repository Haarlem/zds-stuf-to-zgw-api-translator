///*
// * Copyright 2020-2021 The Open Zaakbrug Contributors
// *
// * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
// * European Commission - subsequent versions of the EUPL (the "Licence");
// *
// * You may not use this work except in compliance with the Licence.
// * You may obtain a copy of the Licence at:
// *
// * https://joinup.ec.europa.eu/software/page/eupl5
// *
// * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the Licence for the specific language governing permissions and limitations under the Licence.
// */
//package nl.haarlem.translations.zdstozgw.debug;
//
//
//import jakarta.servlet.*;
//
//import org.springframework.web.context.WebApplicationContext;
//import org.springframework.web.context.support.WebApplicationContextUtils;
//
//import nextapp.echo2.app.ApplicationInstance;
//import nextapp.echo2.webcontainer.WebContainerServlet;
//import nl.nn.testtool.echo2.Echo2Application;
//
//import java.io.IOException;
//
///**
// * @author Jaco de Groot
// */
//public class DebugServlet implements Servlet {
//	private static final long serialVersionUID = 1L;
//	private WebApplicationContext webApplicationContext;
//
//	@Override
//	public void init(ServletConfig servletConfig) throws ServletException {
//		webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());
//	}
//
//    @Override
//    public ServletConfig getServletConfig() {
//        return null;
//    }
//
//	public ApplicationInstance newApplicationInstance() {
//		return (Echo2Application)webApplicationContext.getBean("echo2Application");
//	}
//
//    @Override
//    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws jakarta.servlet.ServletException, IOException {
//
//    }
//
//    @Override
//    public String getServletInfo() {
//        return "";
//    }
//
//    @Override
//    public void destroy() {
//
//    }
//}
