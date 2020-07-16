// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.storage;

import io.vlingo.xoom.codegen.CodeTemplateParameters;
import io.vlingo.xoom.codegen.ImportParameter;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static io.vlingo.xoom.codegen.CodeTemplateParameter.*;
import static io.vlingo.xoom.codegen.storage.DatabaseType.*;
import static io.vlingo.xoom.codegen.storage.ModelClassification.COMMAND;
import static io.vlingo.xoom.codegen.storage.ModelClassification.QUERY;
import static io.vlingo.xoom.codegen.storage.StorageType.STATE_STORE;

public class DatabaseTypeTest {

    @Test
    public void testParametersEnrichmentOnProductionDatabases() {

        final CodeTemplateParameters postgresParametersOnQueryModel =
                POSTGRES.addConfigurationParameters(CodeTemplateParameters.with(STORAGE_TYPE, STATE_STORE)
                        .and(MODEL_CLASSIFICATION, QUERY));

        Assert.assertEquals("PostgresStorageDelegate", postgresParametersOnQueryModel.find(STORAGE_DELEGATE_NAME));
        Assert.assertEquals("io.vlingo.symbio.store.state.jdbc.postgres.PostgresStorageDelegate", postgresParametersOnQueryModel.<List<ImportParameter>>find(IMPORTS).get(0).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.symbio.store.common.jdbc.postgres.PostgresConfigurationProvider", postgresParametersOnQueryModel.<List<ImportParameter>>find(IMPORTS).get(1).getQualifiedClassName());

        final CodeTemplateParameters postgresParametersOnCommandModel =
                POSTGRES.addConfigurationParameters(CodeTemplateParameters.with(STORAGE_TYPE, STATE_STORE)
                        .and(MODEL_CLASSIFICATION, COMMAND));

        Assert.assertEquals("PostgresStorageDelegate", postgresParametersOnCommandModel.find(STORAGE_DELEGATE_NAME));
        Assert.assertEquals("io.vlingo.symbio.store.state.jdbc.postgres.PostgresStorageDelegate", postgresParametersOnCommandModel.<List<ImportParameter>>find(IMPORTS).get(0).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.symbio.store.common.jdbc.postgres.PostgresConfigurationProvider", postgresParametersOnQueryModel.<List<ImportParameter>>find(IMPORTS).get(1).getQualifiedClassName());

        final CodeTemplateParameters mySqlParameters =
                MYSQL.addConfigurationParameters(CodeTemplateParameters.with(STORAGE_TYPE, STATE_STORE)
                        .and(MODEL_CLASSIFICATION, QUERY));

        Assert.assertEquals("MySQLStorageDelegate", mySqlParameters.find(STORAGE_DELEGATE_NAME));
        Assert.assertEquals("io.vlingo.symbio.store.state.jdbc.mysql.MySQLStorageDelegate", mySqlParameters.<List<ImportParameter>>find(IMPORTS).get(0).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.symbio.store.common.jdbc.mysql.MySQLConfigurationProvider", mySqlParameters.<List<ImportParameter>>find(IMPORTS).get(1).getQualifiedClassName());

        final CodeTemplateParameters hsqldbParameters =
                HSQLDB.addConfigurationParameters(CodeTemplateParameters.with(STORAGE_TYPE, STATE_STORE)
                        .and(MODEL_CLASSIFICATION, QUERY));

        Assert.assertEquals("HSQLDBStorageDelegate", hsqldbParameters.find(STORAGE_DELEGATE_NAME));
        Assert.assertEquals("io.vlingo.symbio.store.state.jdbc.hsqldb.HSQLDBStorageDelegate", hsqldbParameters.<List<ImportParameter>>find(IMPORTS).get(0).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.symbio.store.common.jdbc.hsqldb.HSQLDBConfigurationProvider", hsqldbParameters.<List<ImportParameter>>find(IMPORTS).get(1).getQualifiedClassName());

        final CodeTemplateParameters yugaByteParameters =
                YUGA_BYTE.addConfigurationParameters(CodeTemplateParameters.with(STORAGE_TYPE, STATE_STORE)
                        .and(MODEL_CLASSIFICATION, QUERY));

        Assert.assertEquals("YugaByteStorageDelegate", yugaByteParameters.find(STORAGE_DELEGATE_NAME));
        Assert.assertEquals("io.vlingo.symbio.store.state.jdbc.yugabyte.YugaByteStorageDelegate", yugaByteParameters.<List<ImportParameter>>find(IMPORTS).get(0).getQualifiedClassName());
        Assert.assertEquals("io.vlingo.symbio.store.common.jdbc.yugabyte.YugaByteConfigurationProvider", yugaByteParameters.<List<ImportParameter>>find(IMPORTS).get(1).getQualifiedClassName());
    }

}
