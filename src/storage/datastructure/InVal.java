package storage.datastructure;

import java.util.HashSet;
import java.util.Set;

import messaging.message.UpdateMessage;
import messaging.message.MessageGid;

/**
 * {@link UpdateMessage}s that participated in computing val
 * @date 2014-10-13
 */
public class InVal
{
	private Set<MessageGid> umid_set = new HashSet<>();

	public InVal() {
	}

	public void addUmid(UpdateMessage update_msg)
	{
		this.addUmid(update_msg.getMsgGid());
	}

	public void addUmid(MessageGid umid)
	{
		this.umid_set.add(umid);
	}

	public boolean contains(MessageGid umid)
	{
		return this.umid_set.contains(umid);
	}
}