package com.github.fabioticconi.roguelike.components;

import com.artemis.Component;

public class Group extends Component
{
    public int groupId;

    public Group()
    {

    }

    public Group(final int groupId)
    {
        this.groupId = groupId;
    }
}
