package org.mve.leopard;

import org.mve.leopard.http.HTTPRespond;

public class HTTP
{
	public static String state(int code)
	{
		return switch (code)
		{
			case 100 -> "Continue";
			case 101 -> "Switching Protocols";
			case 200 -> "OK";
			case 201 -> "Created";
			case 202 -> "Accepted";
			case 203 -> "Non-authoritative Information";
			case 204 -> "No Content";
			case 205 -> "Reset Content";
			case 206 -> "Partial Content";
			case 300 -> "Multiple Choices";
			case 301 -> "Moved Permanently";
			case 302 -> "Found";
			case 303 -> "See Other";
			case 304 -> "Not Modified";
			case 305 -> "Use Proxy";
			case 306 -> "Unused";
			case 307 -> "Temporary Redirect";
			case 400 -> "Bad Request";
			case 401 -> "Unauthorized";
			case 402 -> "Payment Required";
			case 403 -> "Forbidden";
			case 404 -> "Not Found";
			case 405 -> "Method Not Allowed";
			case 406 -> "Not Acceptable";
			case 407 -> "Proxy Authentication Required";
			case 408 -> "Request Timeout";
			case 409 -> "Conflict";
			case 410 -> "Gone";
			case 411 -> "Length Required";
			case 412 -> "Precondition Failed";
			case 413 -> "Request Entity Too Large";
			case 414 -> "Request-url Too Long";
			case 415 -> "Unsupported Media Type";
			case 416 -> "Requested Range Not Satisfiable";
			case 417 -> "Expectation Failed";
			case 500 -> "Internal Server Error";
			case 501 -> "Not Implemented";
			case 502 -> "Bad Gateway";
			case 503 -> "Service Unavailable";
			case 504 -> "Gateway Timeout";
			case 505 -> "HTTP Version Not Supported";
			default -> throw new IllegalStateException("Unexpected value: " + code);
		};
	}

	public static String type(String name)
	{
		return switch (name.toLowerCase().substring(name.lastIndexOf('.') + 1))
		{
			case "html" -> "text/html";
			case "js" -> "application/javascript";
			case "css" -> "text/css";
			default -> "*/*";
		};
	}

	public static void C404(HTTPRespond respond)
	{
		respond.code = 404;
		respond.property("Content-Type", "text/html");
		respond.property("Content-Length", "0");
	}
}
