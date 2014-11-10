package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil
{
	public static void write2File(String file_name, String content)
	{
		BufferedWriter bw = null;

		try
		{
			bw = new BufferedWriter(new FileWriter(new File(file_name)));

			bw.write(content);
			bw.flush();
		} catch (FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		} finally
		{
			try
			{
				bw.close();
			} catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}

	}

}
