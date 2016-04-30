/*
 * Copyright (C) 2016 Identitas
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
package onl.identitas.identity.ejb.converters;

import java.sql.Date;
import java.time.LocalDate;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 *
 * @author José M. Fernández-Alba @lt;jmfernandezdalba@gmail.com@gt;
 */
@Converter(autoApply = true)
public class LocalDateAttributeConverter implements
        AttributeConverter<LocalDate, Date> {

@Override
public Date convertToDatabaseColumn(LocalDate locDate) {
    return locDate != null ? Date.valueOf(locDate) : null;
}

@Override
public LocalDate convertToEntityAttribute(Date sqlDate) {
    return sqlDate != null ? sqlDate.toLocalDate() : null;
}
}
