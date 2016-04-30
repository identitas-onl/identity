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

import java.util.function.Consumer;
import java.util.function.Function;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static onl.identitas.identity.ejb.controller.AbstractManager.DO_IN_TRANSACTION_ERROR_MSG;
import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author José M. Fernández-Alba <jmfernandezdalba@gmail.com>
 */
@SuppressWarnings("ClassWithoutLogger")
public class AbstractManagerTest {

public static final String ACTION_RETURN = "return";

@Mock
private EntityManagerFactory emf;
@Mock
private UserTransaction userTransaction;
@Mock
private EntityManager em;
@InjectMocks
private ProjectManager manager;

@BeforeMethod
public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    when(emf.createEntityManager()).thenReturn(em);
}

@Test
@SuppressWarnings("unchecked")
public void doInTransaction_Function() throws ManagerException,
                                              NotSupportedException,
                                              SystemException,
                                              RollbackException,
                                              HeuristicMixedException,
                                              HeuristicRollbackException {
    //Arrange
    //-Parameters
    Function<EntityManager, String> actionMock = mock(Function.class);
    when(actionMock.apply(em)).thenReturn(ACTION_RETURN);

    //-Expected result
    String expectedResult = ACTION_RETURN;

    //Act
    String actualResult = manager.doInTransaction(actionMock);

    //Assert
    InOrder inOrder = inOrder(userTransaction, actionMock, em);

    inOrder.verify(userTransaction).begin();
    inOrder.verify(actionMock).apply(em);
    inOrder.verify(userTransaction).commit();
    inOrder.verify(em).close();

    assertThat(actualResult).isEqualTo(expectedResult);
}

@Test
@SuppressWarnings("unchecked")
public void doInTransaction_Consumer() throws ManagerException,
                                              NotSupportedException,
                                              SystemException,
                                              RollbackException,
                                              HeuristicMixedException,
                                              HeuristicRollbackException {
    //Arrange
    //-Parameters
    Consumer<EntityManager> actionMock = mock(Consumer.class);

    //Act
    manager.doInTransaction(actionMock);

    //Assert
    InOrder inOrder = inOrder(userTransaction, actionMock, em);

    inOrder.verify(userTransaction).begin();
    inOrder.verify(actionMock).accept(em);
    inOrder.verify(userTransaction).commit();
    inOrder.verify(em).close();
}

@Test
@SuppressWarnings("unchecked")
public void doInTransaction_Function_ExceptionThrown() throws ManagerException,
                                                              NotSupportedException,
                                                              SystemException,
                                                              RollbackException,
                                                              HeuristicMixedException,
                                                              HeuristicRollbackException {
    //Arrange
    //-Parameters
    Function<EntityManager, String> actionMock = mock(Function.class);

    //-State
    doThrow(SystemException.class).when(userTransaction).begin();

    //Act
    Throwable actualResult = catchThrowable(
            () -> manager.doInTransaction(actionMock));

    //Assert
    InOrder inOrder = inOrder(userTransaction, em);

    inOrder.verify(userTransaction).rollback();
    inOrder.verify(em).close();

    assertThat(actualResult)
            .isInstanceOf(ManagerException.class)
            .hasCauseInstanceOf(SystemException.class)
            .hasMessageStartingWith(DO_IN_TRANSACTION_ERROR_MSG)
            .hasMessageEndingWith(actualResult.getLocalizedMessage());
}

@Test
@SuppressWarnings("unchecked")
public void doInTransaction_Consumer_ExceptionThrown() throws ManagerException,
                                                              NotSupportedException,
                                                              SystemException,
                                                              RollbackException,
                                                              HeuristicMixedException,
                                                              HeuristicRollbackException {
    //Arrange
    //-Parameters
    Consumer<EntityManager> actionMock = mock(Consumer.class);

    //-State
    doThrow(SystemException.class).when(userTransaction).begin();

    //Act
    Throwable actualResult = catchThrowable(
            () -> manager.doInTransaction(actionMock));

    //Assert
    InOrder inOrder = inOrder(userTransaction, em);

    inOrder.verify(userTransaction).rollback();
    inOrder.verify(em).close();

    assertThat(actualResult)
            .isInstanceOf(ManagerException.class)
            .hasCauseInstanceOf(SystemException.class)
            .hasMessageStartingWith(DO_IN_TRANSACTION_ERROR_MSG)
            .hasMessageEndingWith(actualResult.getLocalizedMessage());
}
}
