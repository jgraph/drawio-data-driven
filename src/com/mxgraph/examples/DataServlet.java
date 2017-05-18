/**
 * Copyright (c) 2017, JGraph Ltd
 */
package com.mxgraph.examples;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.w3c.dom.Node;

import com.mxgraph.io.mxCodec;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.view.mxGraph;

/**
 * Use all the JARS in the jars directory of this repo to run the standalone
 * example. Note that the update plugin only runs in lightbox mode. The
 * documentation is in the source code of the plugin at
 * 
 * http://www.draw.io/plugins/update.js
 * 
 * To use this servlet, run it on a given port and point your browser to
 * (assuming SSL is not used and assuming your browser runs on localhost):
 * 
 * http://www.draw.io/?lightbox=1&p=update&https=0&update-interval=2000&update-url=http://localhost:8080/data
 */
public class DataServlet extends HttpServlet
{
	private static final long serialVersionUID = -4442397463551836919L;

	public static int PORT = 8080;

	protected void writeResponse(HttpServletRequest request, PrintWriter writer)
	{
		// Creates graph with model
		mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();
		Object v1, v2;

		graph.getModel().beginUpdate();
		try
		{
			v1 = graph.insertVertex(parent, "v1", "Hello", 20, 20, 80, 30);
			v2 = graph.insertVertex(parent, "v2", "World!", 200, 150, 80, 30);
			graph.insertEdge(parent, null, "e1", v1, v2);
		}
		finally
		{
			graph.getModel().endUpdate();
		}

		mxCodec codec = new mxCodec();
		Node node = codec.encode(graph.getModel());

		// Updates URL after initial request
		writer.println("<updates url=\"http://localhost:8080/data?initialized=1\">");
		
		// Checks if model is initialized
		String init = request.getParameter("initialized");
		
		if (init == null)
		{		
			writer.println("<model>");
			writer.println(mxXmlUtils.getXml(node));
			writer.println("</model>");
			writer.println("<fit max-scale=\"2\"/>");
		}
		else
		{
			String c1 = (Math.random() < 0.5) ? "red" : ((Math.random() < 0.5) ? "green" : "blue");
			String c2 = (Math.random() < 0.5) ? "red" : ((Math.random() < 0.5) ? "green" : "blue");
			
			// Updates the color (other possible updates include label,
			// metadata, tooltip, geometry)
			writer.println(
					"<update id=\"v1\" style=\"fillColor=" + c1 + ";\"></update>");
			writer.println(
					"<update id=\"v2\" style=\"fillColor=" + c2 + ";\"></update>");
		}
		
		writer.println("</updates>");

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods",
				"POST, GET, OPTIONS, PUT, DELETE, HEAD");
		response.setContentType("text/xml; charset=UTF-8");

		OutputStream out = response.getOutputStream();
		String encoding = request.getHeader("Accept-Encoding");

		// Supports GZIP content encoding
		if (encoding != null && encoding.indexOf("gzip") >= 0)
		{
			response.setHeader("Content-Encoding", "gzip");
			out = new GZIPOutputStream(out);
		}

		PrintWriter writer = new PrintWriter(out);
		writeResponse(request, writer);
		writer.flush();
		writer.close();
	}

	public static void main(String[] args) throws Exception
	{
		Server server = new Server(PORT);

		// Servlets
		Context context = new Context(server, "/");
		context.addServlet(new ServletHolder(new DataServlet()), "/data");

		ResourceHandler fileHandler = new ResourceHandler();
		fileHandler.setResourceBase(".");

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { fileHandler, context });
		server.setHandler(handlers);

		System.out.println("Go to http://www.draw.io/?lightbox=1&test=1&p=update&https=0&update-interval=2000&update-url=http://localhost:" + PORT + "/data");

		server.start();
		server.join();
	}
}
