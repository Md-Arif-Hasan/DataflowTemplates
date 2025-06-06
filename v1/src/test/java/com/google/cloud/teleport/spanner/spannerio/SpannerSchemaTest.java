/*
 * Copyright (C) 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.teleport.spanner.spannerio;

import static org.junit.Assert.assertEquals;

import com.google.cloud.spanner.Dialect;
import com.google.cloud.spanner.Type;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** A test of {@link SpannerSchema}. */
@RunWith(JUnit4.class)
public class SpannerSchemaTest {

  @Test
  public void testSingleTable() throws Exception {
    SpannerSchema schema =
        SpannerSchema.builder()
            .addColumn("test", "pk", "STRING(48)")
            .addKeyPart("test", "pk", false)
            .addColumn("test", "maxKey", "STRING(MAX)")
            .addColumn("test", "numericVal", "NUMERIC")
            .addColumn("test", "jsonVal", "JSON")
            .addColumn("test", "protoVal", "PROTO<customer.app.TestMessage>")
            .addColumn("test", "enumVal", "ENUM<customer.app.TestEnum>")
            .addColumn("test", "tokens", "TOKENLIST")
            .addColumn("test", "uuidCol", "UUID")
            .build();

    assertEquals(1, schema.getTables().size());
    assertEquals(8, schema.getColumns("test").size());
    assertEquals(1, schema.getKeyParts("test").size());
    assertEquals(Type.json(), schema.getColumns("test").get(3).getType());
    assertEquals(
        Type.proto("customer.app.TestMessage"), schema.getColumns("test").get(4).getType());
    assertEquals(
        Type.protoEnum("customer.app.TestEnum"), schema.getColumns("test").get(5).getType());
    assertEquals(Type.bytes(), schema.getColumns("test").get(6).getType());
    assertEquals(Type.string(), schema.getColumns("test").get(7).getType());
  }

  @Test
  public void testTwoTables() throws Exception {
    SpannerSchema schema =
        SpannerSchema.builder()
            .addColumn("test", "pk", "STRING(48)")
            .addKeyPart("test", "pk", false)
            .addColumn("test", "maxKey", "STRING(MAX)")
            .addColumn("other", "pk", "INT64")
            .addKeyPart("other", "pk", true)
            .addColumn("other", "maxKey", "STRING(MAX)")
            .build();

    assertEquals(2, schema.getTables().size());
    assertEquals(2, schema.getColumns("test").size());
    assertEquals(1, schema.getKeyParts("test").size());

    assertEquals(2, schema.getColumns("other").size());
    assertEquals(1, schema.getKeyParts("other").size());
  }

  @Test
  public void testSinglePgTable() throws Exception {
    SpannerSchema schema =
        SpannerSchema.builder(Dialect.POSTGRESQL)
            .addColumn("test", "pk", "character varying(48)")
            .addKeyPart("test", "pk", false)
            .addColumn("test", "maxKey", "character varying")
            .addColumn("test", "numericVal", "numeric")
            .addColumn("test", "commitTime", "spanner.commit_timestamp")
            .addColumn("test", "jsonbCol", "jsonb")
            .addColumn("test", "tokens", "spanner.tokenlist")
            .addColumn("test", "uuidCol", "uuid")
            .build();

    assertEquals(1, schema.getTables().size());
    assertEquals(7, schema.getColumns("test").size());
    assertEquals(1, schema.getKeyParts("test").size());
    assertEquals(Type.timestamp(), schema.getColumns("test").get(3).getType());
    assertEquals(Type.bytes(), schema.getColumns("test").get(5).getType());
    assertEquals(Type.string(), schema.getColumns("test").get(6).getType());
  }

  @Test
  public void testTwoPgTables() throws Exception {
    SpannerSchema schema =
        SpannerSchema.builder(Dialect.POSTGRESQL)
            .addColumn("test", "pk", "character varying(48)")
            .addKeyPart("test", "pk", false)
            .addColumn("test", "maxKey", "character varying")
            .addColumn("test", "jsonbCol", "jsonb")
            .addColumn("other", "pk", "bigint")
            .addKeyPart("other", "pk", true)
            .addColumn("other", "maxKey", "character varying")
            .addColumn("other", "commitTime", "spanner.commit_timestamp")
            .build();

    assertEquals(2, schema.getTables().size());
    assertEquals(3, schema.getColumns("test").size());
    assertEquals(1, schema.getKeyParts("test").size());

    assertEquals(3, schema.getColumns("other").size());
    assertEquals(1, schema.getKeyParts("other").size());
    assertEquals(Type.timestamp(), schema.getColumns("other").get(2).getType());
  }
}
