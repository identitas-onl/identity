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
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author José M. Fernández-Alba @lt;jmfernandezdalba@gmail.com@gt;
 * @param <T> The entity class to provide.
 */
public abstract class AbstractFacade<T> {

private final Class<T> entityClass;

public AbstractFacade(Class<T> entityClass) {
    this.entityClass = entityClass;
}

protected abstract EntityManager getEntityManager();

public void create(T entity) {
    getEntityManager().persist(entity);
}

public void edit(T entity) {
    getEntityManager().merge(entity);
}

public void remove(T entity) {
    getEntityManager().remove(getEntityManager().merge(entity));
}

public T find(Object id) {
    return getEntityManager().find(entityClass, id);
}

public List<T> findAll() {
    CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(
            entityClass);
    cq.select(cq.from(entityClass));
    TypedQuery<T> q = getEntityManager().createQuery(cq);
    return q.getResultList();
}

public List<T> findRange(int from, int to) {
    CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder()
            .createQuery(entityClass);
    cq.select(cq.from(entityClass));
    TypedQuery<T> q = getEntityManager().createQuery(cq);
    q.setMaxResults(to - from + 1);
    q.setFirstResult(from);
    return q.getResultList();
}

public int count() {
    CriteriaQuery<Long> cq = getEntityManager().getCriteriaBuilder()
            .createQuery(Long.class);
    Root<T> rt = cq.from(entityClass);
    cq.select(getEntityManager().getCriteriaBuilder().count(rt));
    Query q = getEntityManager().createQuery(cq);
    return ((Number) q.getSingleResult()).intValue();
}
}
