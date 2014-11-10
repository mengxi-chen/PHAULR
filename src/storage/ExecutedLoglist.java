package storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import messaging.message.MessageGid;
import storage.datastructure.*;

public class ExecutedLoglist
{
	private Logs executed_log;

	public ExecutedLoglist(){
		executed_log = new Logs();
	}

	//fill logRecord's dependences
	public void addDeps(LogRecord logRecord){

		ArrayList<LogRecord> arraylist = new ArrayList<LogRecord>();

		//compare logRecord.prev and logrecord's ts in executed log
		MultipartTimestamp u = logRecord.getPrev();

		HashMap<MessageGid, LogRecord> l = executed_log.get_log_map();

		Collection<LogRecord> collection = l.values();
        Iterator<LogRecord> iter = collection.iterator();
        while(iter.hasNext()){
            LogRecord te = iter.next();
    		MultipartTimestamp m = te.getTs();
            if(m.compareTo(u) == 0) {
            	arraylist.add(te);
            }
        }
//		logRecord.set_deps(arraylist);

        executed_log.addLogRecord(logRecord);
	}

	//apply operation
		public void applyOp(String op) {

		}
}