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
package onl.identitas.identity.web;

import java.util.function.Function;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author Chema
 */
@SuppressWarnings("ClassWithoutLogger")
public class AbstractManager2Test {

public static final String ACTION_RETURN = "return";
private static final long serialVersionUID = 1L;

@BeforeClass
public static void setUpClass() throws Exception {
}

@AfterClass
public static void tearDownClass() throws Exception {
}

@Mock
private EntityManagerFactory emf;
@Mock
private UserTransaction userTransaction;
@Mock
private EntityManager em;
@Mock
private Function<EntityManager, String> actionMock;
@InjectMocks
private SkinUrlManager manager;

public AbstractManager2Test() {
}

@BeforeMethod
public void setUpMethod() throws Exception {
	MockitoAnnotations.initMocks(this);
	when(emf.createEntityManager()).thenReturn(em);
}

@Test
public void testDoInTransaction() throws ManagerException {
	when(actionMock.apply(em)).thenReturn(ACTION_RETURN);

	manager.doInTransaction(actionMock);

	Mockito.verify(actionMock).apply(em);
}

@AfterMethod
public void tearDownMethod() throws Exception {
}
}
