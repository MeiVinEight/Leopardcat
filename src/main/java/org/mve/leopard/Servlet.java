package org.mve.leopard;

import org.mve.leopard.http.HTTPRequest;
import org.mve.leopard.http.HTTPRespond;

public interface Servlet
{
	void respond(HTTPRequest request, HTTPRespond respond);
}
