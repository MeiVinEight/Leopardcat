package org.mve.leopard.connection;

import org.mve.leopard.Leopardcat;
import org.mve.text.Hexadecimal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HTTPConnection
{
	private final Socket socket;
	public final InputStream I;
	public final OutputStream O;

	public HTTPConnection(Socket socket) throws IOException
	{
		this.socket = socket;
		this.I = socket.getInputStream();
		this.O = socket.getOutputStream();
	}

	public boolean connecting()
	{
		return this.socket.isConnected() && !this.socket.isClosed();
	}

	public String address()
	{
		byte[] address = Hexadecimal.transform(this.socket.getInetAddress().getAddress());
		return new String(address, 0, 2) +
			"." +
			new String(address, 2, 2) +
			"." +
			new String(address, 4, 2) +
			"." +
			new String(address, 6, 2) +
			":" +
			new String(Hexadecimal.transform(socket.getPort())).substring(12);
	}

	public void close() throws IOException
	{
		this.socket.close();
		System.out.println(Leopardcat.prefix() + " Disconnecting " + this.address());
	}
}
