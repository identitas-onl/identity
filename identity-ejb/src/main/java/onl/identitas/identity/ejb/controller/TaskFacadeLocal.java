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

import java.util.List;
import javax.ejb.Local;
import onl.identitas.identity.ejb.entities.Task;

/**
 *
 * @author José M. Fernández-Alba @lt;jmfernandezdalba@gmail.com@gt;
 */
@Local
public interface TaskFacadeLocal {

void create(Task task);

void edit(Task task);

void remove(Task task);

Task find(Object id);

List<Task> findAll();

List<Task> findRange(int from, int to);

int count();

}
