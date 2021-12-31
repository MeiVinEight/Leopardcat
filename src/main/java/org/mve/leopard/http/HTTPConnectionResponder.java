package org.mve.leopard.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.mve.IO;
import org.mve.leopard.HTTP;
import org.mve.leopard.Leopardcat;
import org.mve.leopard.connection.HTTPConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public record HTTPConnectionResponder(Leopardcat server) implements Runnable
{
	private static final Map<String, File> RESOURCE = new HashMap<>();

	@Override
	public void run()
	{
		while (this.server.running())
		{
			try
			{
				HTTPConnection connection = this.server.connection.poll();
				if (connection != null)
				{
					boolean keep = true;
					while (keep && connection.connecting())
					{
						keep = false;
						HTTPRequest request = new HTTPRequest(connection);
						HTTPRespond respond = new HTTPRespond(connection);
						if (connection.connecting())
						{
							System.out.println(Leopardcat.prefix() + " " + connection.address() + " -> " + request.method + " " + request.property("Host") + request.URL);

							respond.version = request.version;

							if ("keep-alive".equals(request.property("Connection")))
							{
								keep = true;
								respond.property("Connection", "keep-alive");
							}
							else
							{
								respond.property("Connection", "close");
							}

							if ("www.mve.ink".equals(request.property("Host")) || "mve.ink".equals(request.property("Host")) || "mve.zoyn.top".equals(request.property("Host")) || "106.54.163.152".equals(request.property("Host")) || "127.0.0.1".equals(request.property("Host")))
							{
								if ("/background".equals(request.URL))
								{
									respond.property("Connection", "close");
									respond.property("Content-Type", "image/jpeg");
									keep = false;
									URL url = new URL("https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1");
									HttpURLConnection huc = (HttpURLConnection) url.openConnection();
									JsonObject json = (JsonObject) JsonParser.parseReader(new InputStreamReader(huc.getInputStream()));
									huc.getInputStream().close();
									huc.disconnect();
									String imageURL = "https://www.bing.com" + json.get("images").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString();
									url = new URL(imageURL);
									huc = (HttpURLConnection) url.openConnection();
									IO.copy(huc.getInputStream(), respond.content);
									huc.getInputStream().close();
									huc.disconnect();
									respond.property("Content-Length", String.valueOf(respond.content.size()));
								}
								else if (RESOURCE.get(request.URL) != null)
								{
									respond(respond, RESOURCE.get(request.URL));
								}
								else
								{
									File resource = new File(Leopardcat.ROOT, request.URL.substring(1));
									if (resource.isFile() && check(resource))
									{
										respond(respond, resource);
									}
									else
									{
										HTTP.C404(respond);
									}
								}
							}
							else
							{
								HTTP.C404(respond);
							}

							try
							{
								respond.respond();
							}
							catch (IOException e)
							{
								e.printStackTrace();
							}
						}

					}
					System.out.println(Leopardcat.prefix() + " Disconnecting " + connection.address());
				}
				else
				{
					this.server.waiting();
				}
			}
			catch (Throwable t)
			{
				t.printStackTrace();
			}
		}
	}

	public static void respond(HTTPRespond respond, File resource)
	{
		respond.property("Content-Type", HTTP.type(resource.getName()));
		respond.property("Content-Length", String.valueOf(resource.length()));

		try (FileInputStream in = new FileInputStream(resource))
		{
			IO.copy(in, respond.content);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static boolean check(File file)
	{
		file = file.getAbsoluteFile();
		while (file != null)
		{
			if (file.equals(Leopardcat.ROOT))
			{
				return true;
			}
			file = file.getParentFile();
		}
		return false;
	}

	static
	{
		RESOURCE.put(null, new File(Leopardcat.ROOT, "rfx/README.html"));
		RESOURCE.put("/", new File(Leopardcat.ROOT, "rfx/README.html"));
		RESOURCE.put("/style.css", new File(Leopardcat.ROOT, "rfx/style.css"));
		RESOURCE.put("/page", new File(Leopardcat.ROOT, "page/index.html"));
	}
}
