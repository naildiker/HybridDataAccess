package org.hdm.core.objects;

/**
 * Created by nail.diker on 12/25/2016.
 */
public class EntityOccurence implements IEntityOccurence
{
    String summary = "";

    @Override
    public IEntity getEntity() {
        return null;
    }

    @Override
    public void setString(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString()
    {
        return summary;
    }
}
