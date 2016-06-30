import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.*;
import org.jdom.*;
import org.jdom.input.SAXBuilder;

public class movie_search extends HttpServlet 
{
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException, MalformedURLException
	{request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
		Enumeration e = request.getParameterNames();
		
		String perl_url = "http://cs-server.usc.edu:10828/cgi-bin/get_movies_hw8.pl?";
		
		while (e.hasMoreElements()) 
		{
			String name = (String)e.nextElement();
			String value = URLEncoder.encode(request.getParameter(name), "UTF-8");//request.getParameter(name);
			 
			value = value.replace(' ','+');
			perl_url += name + "=" + value;
			if(e.hasMoreElements())
			{
				perl_url += "&";
			}
		}
		
		URL url = new URL(perl_url); //URLEncoder.encode(url,"UTF-8")
		URLConnection urlConnection = url.openConnection();
		urlConnection.setAllowUserInteraction(false);
		InputStream urlStream = url.openStream();
		
		SAXBuilder builder;
		Document doc;
		Element root;
		List movies;
		
		String output = null;
		try
		{
			builder = new SAXBuilder();
			doc = builder.build(urlStream);
			root = doc.getRootElement();
			movies = root.getChildren();
			
			Element movie;
			String title; 
			String image;  
			String year;  
			String director;  
			String rating;
			String link;
			
			if(movies.size() == 0)
			{
				output = "{\n  \"movies\":{\n\n  \"movie\":[\n]}\n}";
			}
			else
			{
				output = "{\n  \"movies\":{\n\n  \"movie\":[\n";
				for (int i =0;i<movies.size() ;i++ )
				{
					movie = (Element)movies.get(i);
					title = movie.getAttributeValue("title");
					output += "  {\"title\":\""+title+"\",\n";
					
					image = movie.getAttributeValue("image");
					output += "  \"image\":\""+image+"\",\n";
					
					year = movie.getAttributeValue("year");
					output += "  \"year\":\""+year+"\",\n";
					
					director = movie.getAttributeValue("director");
					output += "  \"director\":\""+director+"\",\n";
					
					rating = movie.getAttributeValue("rating");
					output += "  \"rating\":\""+rating+"\",\n";
					
					link = movie.getAttributeValue("link");
					output += "  \"link\":\""+link+"\"\n";
					
					output += "    }";
					if (i!=movies.size()-1)
					{
						output += ",\n\n";
					}
				}
				output += "]}\n}";
			}
			out.println(output);
		}
		catch(JDOMException ej)
		{
			ej.printStackTrace();
			out.println(ej.getMessage().toString());
		}
	}
}