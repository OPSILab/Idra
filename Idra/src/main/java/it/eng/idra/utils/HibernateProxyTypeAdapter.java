/*******************************************************************************
 * Idra - Open Data Federation Platform
 * Copyright (C) 2021 Engineering Ingegneria Informatica S.p.A.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package it.eng.idra.utils;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import org.hibernate.proxy.HibernateProxy;

// TODO: Auto-generated Javadoc
/**
 * This TypeAdapter unproxies Hibernate proxied objects, and serializes them
 * through the registered (or default) TypeAdapter of the base class.
 */
public class HibernateProxyTypeAdapter extends TypeAdapter<HibernateProxy> {

  /** The Constant FACTORY. */
  public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
      return (HibernateProxy.class.isAssignableFrom(type.getRawType())
          ? (TypeAdapter<T>) new HibernateProxyTypeAdapter(
              (TypeAdapter) gson.getAdapter(TypeToken.get(type.getRawType().getSuperclass())))
          : null);
    }
  };

  /** The delegate. */
  private final TypeAdapter<Object> delegate;

  /*
   * (non-Javadoc)
   * 
   * @see com.google.gson.TypeAdapter#read(com.google.gson.stream.JsonReader)
   */
  @Override
  public HibernateProxy read(JsonReader in) throws IOException {
    throw new UnsupportedOperationException("Not supported");
  }

  /**
   * Instantiates a new hibernate proxy type adapter.
   *
   * @param delegate the delegate
   */
  private HibernateProxyTypeAdapter(TypeAdapter<Object> delegate) {
    this.delegate = delegate;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.gson.TypeAdapter#write(com.google.gson.stream.JsonWriter,
   * java.lang.Object)
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void write(JsonWriter out, HibernateProxy value) throws IOException {
    if (value == null) {
      out.nullValue();
      return;
    }
    delegate.write(out, ((HibernateProxy) value).getHibernateLazyInitializer().getImplementation());
  }
}
