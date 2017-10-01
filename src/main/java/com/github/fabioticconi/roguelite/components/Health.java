/*
 * Copyright (C) 2017 Fabio Ticconi
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

package com.github.fabioticconi.roguelite.components;

import com.artemis.Component;

/**
 * Author: Fabio Ticconi
 * Date: 28/09/17
 */
public class Health extends Component
{
    public float maxValue;
    public float value;

    public Health()
    {

    }

    public Health(final float value)
    {
        set(value);
    }

    public void set(final float health, final float maxHealth)
    {
        this.maxValue = maxHealth;
        this.value = health;
    }

    public void set(final float value)
    {
        set(value, value);
    }
}