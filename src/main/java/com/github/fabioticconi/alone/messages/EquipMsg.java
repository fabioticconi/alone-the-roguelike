/*
 * Copyright (C) 2015-2017 Fabio Ticconi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.github.fabioticconi.alone.messages;

/**
 * Author: Fabio Ticconi
 * Date: 04/11/17
 */
public class EquipMsg extends AbstractMessage
{
    private final boolean remove;

    public EquipMsg(final boolean remove)
    {
        this.remove = remove;
    }

    @Override
    public String format()
    {
        if (remove)
            return formatUnequip();

        return String.format("%s %s %s", actor, thirdPerson ? "equips" : "equip", target.toLowerCase());
    }

    private String formatUnequip()
    {
        return String.format("%s %s %s", actor, thirdPerson ? "removes" : "remove", target.toLowerCase());
    }
}
