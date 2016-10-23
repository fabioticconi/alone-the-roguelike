/**
 * Copyright 2016 Fabio Ticconi
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.fabioticconi.roguelike.components;

import com.artemis.Component;
import com.googlecode.lanterna.TextCharacter;

/**
 *
 * @author Fabio Ticconi
 */
public class Sprite extends Component
{
    public TextCharacter c;

    public Sprite()
    {

    }

    public Sprite(final TextCharacter c)
    {
        this.c = c;
    }
}
