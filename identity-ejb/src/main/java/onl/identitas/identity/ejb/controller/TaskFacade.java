/*
 * Copyright (C) 2015 Identitas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package onl.identitas.identity.ejb.controller;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import onl.identitas.identity.ejb.entities.Task;

/**
 *
 * @author José M. Fernández-Alba @lt;jmfernandezdalba@gmail.com@gt;
 */
@Stateless
public class TaskFacade extends AbstractFacade<Task> implements TaskFacadeLocal {

@PersistenceContext(unitName = "onl.identitas_identity-ejb_ejb_1.0-SNAPSHOTPU")
private EntityManager em;

public TaskFacade() {
    super(Task.class);
}

@Override
protected EntityManager getEntityManager() {
    return em;
}
}
