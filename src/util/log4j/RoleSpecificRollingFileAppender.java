package util.log4j;

import org.apache.log4j.RollingFileAppender;

/**
 * Specify different names for different replicas and clients.
 * @date 2014-10-28
 */
public class RoleSpecificRollingFileAppender extends RollingFileAppender
{
    @Override
    public void  setFile(String file_name)
    {
        super.setFile(file_name);
    }
}
