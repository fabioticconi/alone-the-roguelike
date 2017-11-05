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

package com.github.fabioticconi.alone.systems;

import com.artemis.ComponentMapper;
import com.artemis.EntityEdit;
import com.github.fabioticconi.alone.components.*;
import com.github.fabioticconi.alone.components.actions.ActionContext;
import com.github.fabioticconi.alone.components.attributes.Strength;
import com.github.fabioticconi.alone.constants.WeaponType;
import com.github.fabioticconi.alone.messages.CannotMsg;
import com.github.fabioticconi.alone.messages.CrushMsg;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.EnumSet;

/**
 * Author: Fabio Ticconi
 * Date: 07/10/17
 */
public class CrushSystem extends PassiveSystem
{
    static final Logger log = LoggerFactory.getLogger(CrushSystem.class);

    ComponentMapper<Crushable> mCrushable;
    ComponentMapper<Speed>     mSpeed;
    ComponentMapper<Strength>  mStrength;
    ComponentMapper<Position>  mPosition;
    ComponentMapper<Weapon>    mWeapon;

    StaminaSystem sStamina;
    ItemSystem    sItem;
    MessageSystem msg;
    MapSystem     map;

    public CrushAction crush(final int entityId, final int targetId)
    {
        final CrushAction c = new CrushAction();

        c.actorId = entityId;

        c.targets.add(targetId);

        return c;
    }

    public int makeStone(final int x, final int y)
    {
        final int id = world.create();

        final EntityEdit edit = world.edit(id);
        edit.create(Position.class).set(x, y);
        edit.create(Sprite.class).set('o', Color.DARK_GRAY.brighter());
        edit.create(Weapon.class).set(WeaponType.BLUNT, 1);
        edit.create(Wearable.class);
        edit.add(new Name("A stone"));

        return id;
    }

    public int makeBoulder(final int x, final int y)
    {
        final int        id   = world.create();
        final EntityEdit edit = world.edit(id);

        edit.create(Position.class).set(x, y);
        edit.create(Sprite.class).set('#', Color.DARK_GRAY.brighter(), true);
        edit.create(LightBlocker.class);
        edit.create(Pushable.class);
        edit.create(Crushable.class);
        edit.add(new Name("A boulder"));

        return id;
    }

    public class CrushAction extends ActionContext
    {
        @Override
        public boolean tryAction()
        {
            if (targets.size() != 1)
                return false;

            final int targetId = targets.get(0);

            if (targetId < 0 || !mCrushable.has(targetId))
                return false;

            final int hammerId = sItem.getWeapon(actorId, EnumSet.of(WeaponType.BLUNT), true);

            if (hammerId < 0)
            {
                msg.send(actorId, targetId, new CannotMsg("crush", "without a blunt weapon"));

                return false;
            }

            final Weapon weapon = mWeapon.get(hammerId);

            if (weapon.damage < 3)
            {
                msg.send(actorId, targetId, new CannotMsg("crush", "with such a weak weapon"));

                return false;
            }

            targets.add(hammerId);

            // FIXME further adjust delay and cost using the hammer power
            delay = mSpeed.get(actorId).value;
            cost = delay / (mStrength.get(actorId).value + 3f);

            return true;
        }

        @Override
        public void doAction()
        {
            if (targets.size() != 2)
                return;

            final int targetId = targets.get(0);

            final Position p = mPosition.get(targetId);

            // from a tree we get a trunk and two branches
            map.obstacles.del(p.x, p.y);
            world.delete(targetId);

            for (int i = 0; i < 3; i++)
                map.items.setFirstFree(makeStone(p.x, p.y), p.x, p.y);

            // consume a fixed amount of stamina
            sStamina.consume(actorId, cost);

            msg.send(actorId, targetId, new CrushMsg());
        }
    }
}
