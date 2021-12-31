package org.mve.text;

public class Hexadecimal
{
	private static final byte[] H = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

	public static byte[] transform(long num)
	{
		byte[] ret = new byte[16];
		int i = 15;
		for (int j = 0; j < 16; j++)
		{
			ret[i--] = H[(int) (num & 0xF)];
			num >>>= 4;
		}
		return ret;
	}

	public static byte[] transform(byte[] data)
	{
		byte[] ret = new byte[data.length * 2];
		for (int i = 0; i < data.length; i++)
		{
			ret[i * 2] = H[(data[i] >> 4) & 0xF];
			ret[i * 2 + 1] = H[data[i] & 0xF];
		}
		return ret;
	}
}
