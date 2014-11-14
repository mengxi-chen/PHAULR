package storage.datastructure;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import messaging.message.MessageGid;

public class Logs implements Serializable 
{
	private static final long serialVersionUID = 6531676830190572158L;

	private HashMap<MessageGid, LogRecord> log_map = new HashMap<>();

	public Logs() {
	}

	public void addLogRecord(LogRecord log_record)
	{
		this.log_map.put(log_record.getUmid(), log_record);
	}

	/**
	 * Merge another {@link Logs} into this one.
	 * Only the log records smaller than @param rep_ts are merged.
	 * <b>Note:</b> this {@link Logs} are changed.
	 *
	 * @param logs
	 * @param rep_ts
	 */
    public void merge(Logs logs, MultipartTimestamp rep_ts)
    {
        LogRecord log_record = null;
        MultipartTimestamp ts = null;

        for (Map.Entry<MessageGid, LogRecord> umid_log_entry : this.log_map.entrySet())
        {
            log_record = umid_log_entry.getValue();
            ts = log_record.getTs();

            if(! (ts.compareTo(rep_ts) <= 0))
            	this.addLogRecord(log_record);
        }
    }

    public HashMap<MessageGid, LogRecord> get_log_map() {
    	return this.log_map;
    }

    public void remove_logrecord(LogRecord logRecord){
    	log_map.remove(logRecord.getUmid());
    }
    
    public int getSize()
    {
    	return this.log_map.size();
    }
}