package org.mve.leopard.http;

import org.mve.leopard.connection.HTTPConnection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class HTTPRequest
{
	public final HTTPConnection connection;
	public final String method;
	public final String URL;
	public final String protocol;
	public final String version;
	public final long length;
	private final Map<String, String> property = new HashMap<>();

	public HTTPRequest(HTTPConnection connection) throws IOException
	{
		this.connection = connection;
		String line = this.property();
		if (this.connection.connecting())
		{
			try
			{
				String[] split = line.split(" ");
				this.method = split[0];
				this.URL = split[1];
				String[] protocol = split[2].split("/");
				this.protocol = protocol[0];
				this.version = protocol[1];
			}
			catch (Throwable t)
			{
				throw new IOException("Unknown HTTP: " + line);
			}
		}
		else
		{
			this.method = this.URL = this.protocol = this.version = "";
		}

		StringBuilder header = new StringBuilder();
		while ((line = this.property()).length() > 0 && this.connection.connecting())
		{
			header.append(line).append("\r\n");
		}

		if (this.connection.connecting())
		{
			Properties properties = new Properties();
			properties.load(new StringReader(header.toString()));
			properties.forEach((k, v) -> HTTPRequest.this.property.put(k.toString(), v == null ? "" : v.toString()));
		}
		this.length = Long.parseLong(this.property.getOrDefault("Content-Length", "0"));
	}

	public String property(String key)
	{
		return this.property.get(key);
	}

	private String property() throws IOException
	{
		if (this.connection.connecting())
		{
			int c;
			int length = 0;
			int carriage = -1;

			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			while ((c = this.connection.I.read()) != '\n' && c >= 0)
			{
				buf.write(c);
				if (c == '\r')
				{
					carriage = length;
				}
				length++;
			}

			if (c == -1)
			{
				this.connection.close();
			}

			if (length > 0)
			{
				buf.flush();
				byte[] b = buf.toByteArray();
				return new String(b, 0, carriage + 1 == length ? carriage : length);
			}
		}
		return "";
	}
}
