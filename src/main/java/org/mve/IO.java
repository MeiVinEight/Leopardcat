package org.mve;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IO
{
	public static void copy(InputStream in, OutputStream out) throws IOException
	{
		byte[] b = new byte[1024];
		int len;
		while ((len = in.read(b)) > 0)
		{
			out.write(b, 0, len);
		}
	}
}
