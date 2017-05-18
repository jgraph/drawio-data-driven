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
 * Add all the JARS in the jars directory of this repo to run it as a
 * standalone example. Note that the update plugin only runs in lightbox mode.
 * The documentation is in the source code of the plugin at
 * 
 * http://www.draw.io/plugins/update.js
 * 
 * To use this servlet, run it on a given port and point your browser to
 * (assuming SSL is not used and assuming your browser runs on localhost):
 * 
 * http://www.draw.io/?lightbox=1&test=1&p=update&https=0&update-interval=2000&update-url=http://localhost:8080/data
 * 
 * A general demo is available here:
 * 
 * https://www.draw.io/?lightbox=1&highlight=0000ff&edit=_blank&p=update#R7Vtdc6s2EP01fu2AxOdj6ps0fbgzd5rptK8yyFgNII8s28n99RUgbIREbGIDdhs%2FZMxqJaFzdle7kjOD8%2BztN4bWq%2B80xukMWPHbDH6bAeA7jvhbCN4rgWu7lSBhJK5E9lHwQn5iKbSkdEtivFEUOaUpJ2tVGNE8xxFXZIgxulfVljSVs8rx1yjBikYheIlQqkv%2FIjFfVdIAeEf5MybJqp7Z9sKqZYGi14TRbS7nmwG4LD9Vc4bqseSLbFYopvvGpPBxBueMUl59o4t%2FivUBK0ULAbEcs%2Bi5XceI4z%2BZFMY4o82G33OO2Q7JVmBZckLSBKKcInub41Sw91Q9ii9yUqXVavG2RgznvLmWp44O8n3Fu2wluvMUkUyInlEepyRPxNcfjEZ4s5Gg8PeaiM2eZCnKxdOvK8rIT5rzck2WEGw4YlzaDiwES5Kmc5pSVvaFy3Dp47hUZPQVN1qgB0NYtOhrkcvbYcbxW5sXYe6YZpizd6HyVkNb9ZCWDuXjvmE2gZStGibj1IQgaarJYWTQ5ELCaYYWdkL7HeXCRlkvPK8BH%2BgLnwkv14AXuAZeroaXBpDoIIJMgc9%2BRTh%2BWaOoaNmLMFdgxrMCLbtAS0D3hDKSFsQ%2F43SHOYmQbJBWaQP53ADPKj9CjlKS5EIW4cJVTTgHYAE9T2cmdnEQO0b8YV%2F8HdV8A1ejAxrYgP3JMEWyhy0vrRDPisgK7QIWacHHSKb4Zc3koedBXWW8wWh%2Fsy7jd6FTEn0FkMMWyLrN25YxRvRG%2BVT09jQP%2BFtzAbai2WK7uQkX8KIAL5YGF0A4WEbXYeeQdUzjA%2Bs1o7tPeEDV7y7sH3g34wD%2BlwPoe3AwlgNodARn7MixSM3lI2Ui7Cc0R%2BnjUdoNwYZuWYSVvV%2FkjAnmXZsIsIrZPsSM4RRxslOd8hIIwtEgMK23iYc3FQR1AB4BA89gBq1IOgkC9mgI6MttguFPhoBeJw6FgD92KCi7PjCG3hsKa0pyvmmM%2FKMQNKJy6CpR2W2V2S11u5XJq%2BriSzX%2FkYvDQs7NVP7AO4L3vROVqttd5CluOxOcLk8Bujt8JSoenCxRMZy1DBWeWg6jBqp62sED9Ie1iiXe8ZfPlizV%2BnB8FyHBa%2BfGE4YEvXgfygTrkQ1pksbeFMnCVxlnMNXAmi46jlfHdZuhEiavnseasy7fVzG328fU1avLXh%2Bkb354YqBqddpAp1M5%2FdZgvEzblGpr29vEeXZgq3m2fSLRdluHWfZlmbZGj6Ox8xBFYlvjSDJ%2FF%2Fc5EpzDtc0Q9zumFOWxjMDHzOQb4mh2KjEpO0mUSZ7UfUbMS5y%2BODu3c6Rq6xb7n9uMe%2FPjTnemap9zzfmJ%2BO7o8b3DcdSAX9vHJCXLfIWj177xoOx0X%2FFAK40njAcD1SkG%2B6tNq2luHeRNUabY%2F4Mypbel%2Bu50kXGgMsVgmZ1WqETG6U7bz7l1uu%2BfwvQ2zCnr54EuwEwh01CS1dYwvh3Cge69zlx4Z%2F4ycU3qe62a1DtRk7YuLlr6F9ek9kCn30Cn6Y5YatdgdYLVxVJbH8gbvmuxZDg6GIolU%2BJ1awc7Hmg5UXDKicLL9K3rOt1Qvwwx0FnL7sHrPL%2FXvbgThh%2BoX0wSHGjnNpF0R6ExcG6KpPG2L8Pvi26VI%2BCqHEHrou1IPB7%2Fb6VSP%2F5vEHz8Fw%3D%3D
 * 
 * The updateUrl and updateInterval are embedded in the metadata of the diagram
 * encoded in the #R part in the above case.
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
