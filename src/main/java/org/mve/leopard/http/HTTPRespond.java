package org.mve.leopard.http;

import org.mve.leopard.HTTP;
import org.mve.leopard.connection.HTTPConnection;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class HTTPRespond
{
	private final HTTPConnection connection;
	public String protocol = "HTTP";
	public String version = "2.0";
	public int code = 200;
	private final Map<String, String> property = new HashMap<>();
	public final ByteArrayOutputStream content = new ByteArrayOutputStream();
	private boolean responded = false;

	public HTTPRespond(HTTPConnection connection)
	{
		this.connection = connection;
	}

	public void property(String key, String value)
	{
		this.property.put(key, value);
	}

	public void write(byte[] data, int offset, int length)
	{
		this.content.write(data, offset, length);
	}

	public void write(byte[] data)
	{
		this.write(data, 0, data.length);
	}

	public void respond() throws IOException
	{
		if (!this.responded)
		{
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.connection.O));
			writer.write(this.protocol + "/" + this.version + " " + this.code + " " + HTTP.state(this.code) + "\r\n");
			this.property.forEach((k, v) ->
			{
				try
				{
					writer.write(k + ": " + v + "\r\n");
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			});
			writer.write("\r\n");
			writer.flush();
			this.content.flush();
			this.connection.O.write(this.content.toByteArray());
			this.connection.O.flush();
			this.responded = true;
		}
	}

	public boolean responded()
	{
		return this.responded;
	}
}
