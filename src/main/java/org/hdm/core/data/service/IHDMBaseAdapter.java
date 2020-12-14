package org.hdm.core.data.service;

/**
 * Created by nail.diker on 09/20/2016.
 */
public interface IHDMBaseAdapter
{
	public Boolean connect(IHDMConnectInfo connectionInfo);
	public void  close();

}
