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
package onl.identitas.identity.web.internal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 *
 * @author Chema
 */
public class ExampleActivator implements BundleActivator {

private static final Logger LOG = LogManager.getLogger();

@Override
public void start(BundleContext bc) throws Exception {
	LOG.entry(bc);
}

@Override
public void stop(BundleContext bc) throws Exception {
	LOG.entry(bc);
}
}
