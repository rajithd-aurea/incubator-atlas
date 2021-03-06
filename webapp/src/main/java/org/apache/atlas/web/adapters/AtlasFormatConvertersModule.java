/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.atlas.web.adapters;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.apache.atlas.web.adapters.v1.ReferenceableToAtlasEntityConverter;
import org.apache.atlas.web.adapters.v1.StructToAtlasStructConverter;
import org.apache.atlas.web.adapters.v1.TraitToAtlasClassificationConverter;
import org.apache.atlas.web.adapters.v2.AtlasClassificationToTraitConverter;
import org.apache.atlas.web.adapters.v2.AtlasEntityToReferenceableConverter;
import org.apache.atlas.web.adapters.v2.AtlasStructToStructConverter;

public class AtlasFormatConvertersModule extends AbstractModule {

  protected void configure() {
      Multibinder<AtlasFormatAdapter> multibinder
          = Multibinder.newSetBinder(binder(), AtlasFormatAdapter.class);
      multibinder.addBinding().to(AtlasStructToStructConverter.class).asEagerSingleton();
      multibinder.addBinding().to(AtlasEntityToReferenceableConverter.class).asEagerSingleton();
      multibinder.addBinding().to(AtlasClassificationToTraitConverter.class).asEagerSingleton();

      multibinder.addBinding().to(AtlasPrimitiveFormatConverter.class).asEagerSingleton();
      multibinder.addBinding().to(AtlasEnumFormatConverter.class).asEagerSingleton();
      multibinder.addBinding().to(AtlasMapFormatConverter.class).asEagerSingleton();
      multibinder.addBinding().to(AtlasArrayFormatConverter.class).asEagerSingleton();

      multibinder.addBinding().to(ReferenceableToAtlasEntityConverter.class).asEagerSingleton();
      multibinder.addBinding().to(StructToAtlasStructConverter.class).asEagerSingleton();
      multibinder.addBinding().to(TraitToAtlasClassificationConverter.class).asEagerSingleton();
  }

}