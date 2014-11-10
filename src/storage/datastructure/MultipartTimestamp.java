package storage.datastructure;

import java.io.Serializable;
import java.util.Arrays;

import communication.Configuration;

public class MultipartTimestamp implements Serializable, Comparable<MultipartTimestamp> {
	private static final long serialVersionUID = -973635775503835870L;

	private int[] ts = null;

	public MultipartTimestamp()
	{
		// All the "int" elements are automatically initialized to 0.
		this.ts = new int[Configuration.REPLICA_NUM];
	}

	public MultipartTimestamp(MultipartTimestamp mpts)
	{
		this.ts = Arrays.copyOf(mpts.ts, mpts.ts.length);
	}

	public MultipartTimestamp(int[] mpts)
	{
		this.ts = new int[Configuration.REPLICA_NUM];
		for(int i = 0; i < Configuration.REPLICA_NUM; i++)
		{
			this.ts[i] = mpts[i];
		}
	}

	public int getComponentByReplica(int replica_id)
	{
		return this.ts[replica_id];
	}

	/**
	 * Compare two {@link MultipartTimestamp}s
	 * @param mpts {@link MultipartTimestamp} to compare
	 * @return
	 *  1, if this {@link MultipartTimestamp} is bigger;
	 * 	-1, if it is smaller;
	 *  0, if they are equal.
	 */
	@Override
	public int compareTo(MultipartTimestamp mpts)
	{
		final int SMALLER = -1;
		final int BIGGER = 1;
		final int EQUAL = 0;

		for ( int i = 0; i < this.ts.length; i++ )
		{
			if(this.ts[i] > mpts.ts[i])
				return BIGGER;
			if (this.ts[i] < mpts.ts[i])
				return SMALLER;
		}

		return EQUAL;
	}

	/**
	 * Advance its specified component by one
	 * <b>Note:</b> the original {@link MultipartTimestamp} that
	 * calls this method has changed.
	 *
	 * @param index the component specified by "index" is to be advanced
	 */
	public void advance(int index)
	{
		this.ts[index]++;
	}

	/**
	 * Replace its specified component with corresponding component of another {@link MultipartTimestamp}
	 * @param replace_mpts	the {@link MultipartTimestamp} used to replace this one
	 * @param index the component specified by "index" is to be replaced
	 */
	public void replaceAtIndex(MultipartTimestamp replace_mpts, int index)
	{
		this.ts[index] = replace_mpts.ts[index];
	}

	/**
	 * Merge with another {@link MultipartTimestamp}
	 * <b>Note:</b> the original {@link MultipartTimestamp} that
	 * calls this method has changed.
	 *
	 * @param mpts a {@link MultipartTimestamp} to be merged with
	 */
	public void merge(MultipartTimestamp mpts)
	{
		for ( int i = 0; i < this.ts.length; i++ )
			this.ts[i] = Math.max(this.ts[i], mpts.ts[i]);
	}

	@Override
	public String toString()
	{
		return Arrays.toString(this.ts);
	}
}